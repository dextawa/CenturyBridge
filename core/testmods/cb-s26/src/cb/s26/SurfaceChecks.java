package cb.s26;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.Identifier;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * 26.1.2 surface rules (s26b, queue item 5; rt/CbS26SurfaceRules): a mod's rule built on SurfaceRules.isBiome, the
 * vanilla rule sets a mod calls (SurfaceRuleData), a mod's own RuleSource with 26.1.2's KeyDispatchDataCodec codec(),
 * and their codec round trips through 26.2's dispatch codecs.
 */
final class SurfaceChecks {
    private SurfaceChecks() {}

    /** a 26.1.2 mod's own rule source; nested in a SurfaceRules subclass, which may name its protected Context and SurfaceRule */
    static class ModRules extends SurfaceRules {
        record GoldRule(int depth) implements SurfaceRules.RuleSource {
            static final KeyDispatchDataCodec<GoldRule> CODEC = KeyDispatchDataCodec.of(
                com.mojang.serialization.Codec.INT.fieldOf("depth").xmap(GoldRule::new, GoldRule::depth));

            @Override
            public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
                return CODEC;
            }

            @Override
            public SurfaceRule apply(Context context) {
                return (x, y, z) -> y < depth ? Blocks.GOLD_BLOCK.defaultBlockState() : null;
            }
        }
    }

    static void run() {
        // isBiome: 26.1.2 kept the keys and encoded them as a biome_is list
        SurfaceRules.ConditionSource desert = SurfaceRules.isBiome(Biomes.DESERT, Biomes.BADLANDS);
        S26TestMod.check("surface.isBiome equals", desert.equals(SurfaceRules.isBiome(Biomes.DESERT, Biomes.BADLANDS))
            && !desert.equals(SurfaceRules.isBiome(Biomes.DESERT)), desert);
        SurfaceRules.RuleSource rule = SurfaceRules.ifTrue(desert, SurfaceRules.state(Blocks.GOLD_BLOCK.defaultBlockState()));
        try {
            JsonElement json = SurfaceRules.RuleSource.CODEC.encodeStart(JsonOps.INSTANCE, rule).getOrThrow();
            String keys = json.getAsJsonObject().getAsJsonObject("if_true").get("biome_is").toString();
            S26TestMod.check("surface.isBiome encodes biome_is", keys, "[\"minecraft:desert\",\"minecraft:badlands\"]");
            SurfaceRules.RuleSource back = SurfaceRules.RuleSource.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
            S26TestMod.check("surface.isBiome rule round trip", back.equals(rule), json);
        } catch (RuntimeException e) {
            S26TestMod.check("surface.isBiome rule round trip", false, e);
        }

        // a mod's own rule source, registered as 26.1.2 mods registered it (the MapCodec its KeyDispatchDataCodec holds)
        Registry.register(BuiltInRegistries.MATERIAL_RULE, Identifier.fromNamespaceAndPath("cb_s26", "gold"), ModRules.GoldRule.CODEC.codec());
        ModRules.GoldRule gold = new ModRules.GoldRule(7);
        S26TestMod.check("surface.mod codec() call", gold.codec() == ModRules.GoldRule.CODEC
            && ((SurfaceRules.RuleSource) gold).codec().codec() == ModRules.GoldRule.CODEC.codec(), gold.codec());
        S26TestMod.check("surface.vanilla codec() call", rule.codec().codec() == BuiltInRegistries.MATERIAL_RULE.getValue(Identifier.withDefaultNamespace("condition")),
            rule.codec());
        try {
            SurfaceRules.RuleSource seq = SurfaceRules.sequence(gold, rule);
            JsonElement json = SurfaceRules.RuleSource.CODEC.encodeStart(JsonOps.INSTANCE, seq).getOrThrow();
            SurfaceRules.RuleSource back = SurfaceRules.RuleSource.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
            S26TestMod.check("surface.mod rule round trip", back.equals(seq) && json.toString().contains("\"cb_s26:gold\""), json);
        } catch (RuntimeException e) {
            S26TestMod.check("surface.mod rule round trip", false, e);
        }

        // the vanilla rule sets a 26.1.2 mod builds on, and their round trips
        try {
            SurfaceRules.RuleSource over = SurfaceRuleData.overworldLike(true, false, true);
            S26TestMod.check("surface.overworld() equals overworldLike(true,false,true)", SurfaceRuleData.overworld().equals(over), "");
            for (String name : new String[] {"overworld", "nether"}) {
                SurfaceRules.RuleSource r = name.equals("nether") ? SurfaceRuleData.nether() : over;
                JsonElement json = SurfaceRules.RuleSource.CODEC.encodeStart(JsonOps.INSTANCE, r).getOrThrow();
                SurfaceRules.RuleSource back = SurfaceRules.RuleSource.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                S26TestMod.check("surface." + name + " round trip", back.equals(r), json.toString().length());
            }
        } catch (RuntimeException e) {
            S26TestMod.check("surface.SurfaceRuleData", false, e);
        }
    }
}
