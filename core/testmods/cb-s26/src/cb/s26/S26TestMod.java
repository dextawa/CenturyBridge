package cb.s26;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;

/**
 * A 26.1.2-era server mod (Mojang names, Fabric Loader API only) for s26's bridges of the 26.1.2 -> 26.2 step,
 * converted one step and run on a Fabric 26.2 server at initialisation (after the game's bootstrap). Every
 * constant 26.2 folded into a record or moved must be what 26.1.2 had under its registry and id; static members
 * 26.2 moved and members it renamed must answer as on 26.1.2. Prints "CB-S26 ok|FAIL ..." lines and
 * "CB-S26 DONE passed/checks".
 */
public class S26TestMod implements ModInitializer {
    private static final List<String> FAILS = new ArrayList<>();
    private static int checks;

    static void check(String what, boolean ok, Object detail) {
        checks++;
        if (!ok) {
            FAILS.add(what);
            System.out.println("CB-S26 FAIL " + what + " : " + detail);
        }
    }

    static void check(String what, String actual, String expected) {
        check(what, expected.equals(actual), actual + " (want " + expected + ")");
    }

    /** what a constant is, as core/tools/RegistryDump.java prints it: kind, registry, id */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static String describe(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof ResourceKey<?> k) {
            return "key\t" + k.registry() + "\t" + k.identifier();
        }
        if (v instanceof TagKey<?> t) {
            return "tag\t" + t.registry().identifier() + "\t" + t.location();
        }
        if (v instanceof Holder<?> h) {
            Optional<? extends ResourceKey<?>> k = (Optional) h.unwrapKey();
            return k.map(x -> "holder\t" + x.registry() + "\t" + x.identifier()).orElse("holder\tunbound");
        }
        Registry<Registry<?>> root = (Registry) BuiltInRegistries.REGISTRY;
        for (Registry<?> reg : root) {
            Object id = ((Registry) reg).getKey(v);
            if (id != null && ((Registry) reg).getValue((net.minecraft.resources.Identifier) id) == v) {
                return "value\t" + root.getKey(reg) + "\t" + id;
            }
        }
        return "unregistered " + v.getClass().getName();
    }

    @Override
    public void onInitialize() {
        RegConstChecks.run();

        // static members 26.2 moved to another class (gen_static_moves.py)
        check("moved.EntityType.PIG", describe(EntityType.PIG), "value\tminecraft:entity_type\tminecraft:pig");
        check("moved.EntityType.ITEM", describe(EntityType.ITEM), "value\tminecraft:entity_type\tminecraft:item");
        check("moved.BlockEntityType.CHEST", describe(BlockEntityType.CHEST), "value\tminecraft:block_entity_type\tminecraft:chest");
        check("moved.BlockEntityType.FURNACE", describe(BlockEntityType.FURNACE), "value\tminecraft:block_entity_type\tminecraft:furnace");
        check("moved.DripstoneUtils.isEmptyOrWater", DripstoneUtils.isEmptyOrWater(Blocks.AIR.defaultBlockState())
            && DripstoneUtils.isEmptyOrWater(Blocks.WATER.defaultBlockState())
            && !DripstoneUtils.isEmptyOrWater(Blocks.STONE.defaultBlockState()), "");
        check("moved.DripstoneUtils.isEmptyOrWaterOrLava", DripstoneUtils.isEmptyOrWaterOrLava(Blocks.LAVA.defaultBlockState())
            && !DripstoneUtils.isEmptyOrWaterOrLava(Blocks.DIRT.defaultBlockState()), "");
        check("moved.DripstoneUtils.isNeitherEmptyNorWater", DripstoneUtils.isNeitherEmptyNorWater(Blocks.STONE.defaultBlockState())
            && !DripstoneUtils.isNeitherEmptyNorWater(Blocks.AIR.defaultBlockState()), "");

        // members 26.2 renamed (pkgmoves-gen.json methodRenames)
        check("renamed.MobEffect.isInstantenous", MobEffects.INSTANT_HEALTH.value().isInstantenous()
            && !MobEffects.SPEED.value().isInstantenous(), "");

        // 26.1.2's copper records, which 26.2 replaced by WeatheringCopperCollection (facades + CbS26Copper)
        net.minecraft.world.level.block.WeatheringCopperBlocks bars = Blocks.COPPER_BARS;
        check("copper.Blocks.COPPER_BARS.exposed", describe(bars.exposed()), "value\tminecraft:block\tminecraft:exposed_copper_bars");
        check("copper.Blocks.COPPER_BARS.waxedOxidized", describe(bars.waxedOxidized()), "value\tminecraft:block\tminecraft:waxed_oxidized_copper_bars");
        check("copper.Blocks.COPPER_BARS.same", bars == Blocks.COPPER_BARS && bars.asList().size() == 8
            && bars.weatheringMapping().get(bars.unaffected()) == bars.exposed()
            && bars.waxedMapping().get(bars.weathered()) == bars.waxedWeathered(), bars);
        net.minecraft.world.item.WeatheringCopperItems lanterns = net.minecraft.world.item.Items.COPPER_LANTERN;
        check("copper.Items.COPPER_LANTERN.weathered", describe(lanterns.weathered()), "value\tminecraft:item\tminecraft:weathered_copper_lantern");
        List<Object> order = new ArrayList<>();
        lanterns.forEach(order::add);
        check("copper.Items.COPPER_LANTERN.forEach", order.size() == 8 && order.get(0) == lanterns.unaffected()
            && order.get(4) == lanterns.waxed(), order);
        net.minecraft.world.item.WeatheringCopperItems chains = net.minecraft.world.item.WeatheringCopperItems.create(
            Blocks.COPPER_CHAIN, net.minecraft.world.level.block.Block::asItem);
        check("copper.WeatheringCopperItems.create", chains.equals(net.minecraft.world.item.Items.COPPER_CHAIN), chains);

        // 26.1.2's Tuple, which 26.2 deleted (facade)
        net.minecraft.util.Tuple<String, Integer> t = new net.minecraft.util.Tuple<>("a", 1);
        t.setB(2);
        check("tuple", "a".equals(t.getA()) && t.getB() == 2, t);

        // EntitySelectorParser option flags on 26.2's option states (rt/CbS26Selector)
        try {
            net.minecraft.commands.arguments.selector.EntitySelectorParser p =
                new net.minecraft.commands.arguments.selector.EntitySelectorParser(new com.mojang.brigadier.StringReader("@e[name=x,limit=1]"), true);
            p.parse();
            check("selector.parsed", p.hasNameEquals() && !p.hasNameNotEquals() && p.isLimited() && !p.isSorted()
                && !p.hasGamemodeEquals() && !p.hasScores() && !p.isTypeLimited(), "");
            net.minecraft.commands.arguments.selector.EntitySelectorParser q =
                new net.minecraft.commands.arguments.selector.EntitySelectorParser(new com.mojang.brigadier.StringReader("@e[type=!pig,team=!red,gamemode=!creative]"), true);
            q.parse();
            check("selector.negative", q.isTypeLimitedInversely() && !q.isTypeLimited() && q.hasTeamNotEquals() && !q.hasTeamEquals()
                && q.hasGamemodeNotEquals() && !q.hasGamemodeEquals(), "");
            net.minecraft.commands.arguments.selector.EntitySelectorParser r =
                new net.minecraft.commands.arguments.selector.EntitySelectorParser(new com.mojang.brigadier.StringReader("@e[type=pig]"), true);
            r.parse();
            check("selector.type", r.isTypeLimited() && !r.isTypeLimitedInversely(), "");
            net.minecraft.commands.arguments.selector.EntitySelectorParser s =
                new net.minecraft.commands.arguments.selector.EntitySelectorParser(new com.mojang.brigadier.StringReader("@e"), true);
            s.setSorted(true);
            s.setHasAdvancements(true);
            s.setHasTeamEquals(true);
            boolean set = s.isSorted() && s.hasAdvancements() && s.hasTeamEquals();
            s.setSorted(false);
            s.setHasTeamEquals(false);
            s.setTypeLimitedInversely();
            check("selector.setters", set && !s.isSorted() && !s.hasTeamEquals() && s.hasAdvancements() && s.isTypeLimitedInversely(), "");
        } catch (Exception e) {
            check("selector", false, e);
        }

        // ChatFormatting's 26.1.2 data and lookups (rt/CbS26ChatFormatting)
        net.minecraft.ChatFormatting red = net.minecraft.ChatFormatting.RED;
        check("chatformatting.red", red.getChar() == 'c' && red.getId() == 12 && red.getColor() == 16733525 && red.isColor()
            && !red.isFormat() && "red".equals(red.getName()) && "red".equals(red.getSerializedName()), red);
        check("chatformatting.lookups", net.minecraft.ChatFormatting.getByName("Dark_Blue") == net.minecraft.ChatFormatting.DARK_BLUE
            && net.minecraft.ChatFormatting.getById(-5) == net.minecraft.ChatFormatting.RESET
            && net.minecraft.ChatFormatting.getById(15) == net.minecraft.ChatFormatting.WHITE
            && net.minecraft.ChatFormatting.BOLD.isFormat() && !net.minecraft.ChatFormatting.RESET.isColor()
            && net.minecraft.ChatFormatting.getNames(true, false).size() == 17
            && net.minecraft.ChatFormatting.getNames(false, true).contains("bold"), "");
        com.google.gson.JsonElement je = net.minecraft.ChatFormatting.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,
            net.minecraft.ChatFormatting.GOLD).getOrThrow();
        check("chatformatting.codec", "\"gold\"".equals(je.toString())
            && net.minecraft.ChatFormatting.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, je).getOrThrow() == net.minecraft.ChatFormatting.GOLD
            && net.minecraft.ChatFormatting.COLOR_CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, net.minecraft.ChatFormatting.ITALIC).isError(), je);

        // BlockPos.getCenter / getBottomCenter (rt/CbS26World)
        net.minecraft.core.BlockPos bp = new net.minecraft.core.BlockPos(1, 2, 3);
        check("blockpos.center", bp.getCenter().equals(new net.minecraft.world.phys.Vec3(1.5, 2.5, 3.5))
            && bp.getBottomCenter().equals(new net.minecraft.world.phys.Vec3(1.5, 2.0, 3.5)), bp.getCenter());

        System.out.println("CB-S26 DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS: " + FAILS));
    }
}
