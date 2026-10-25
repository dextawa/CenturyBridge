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

    static ResourceKey<net.minecraft.world.level.block.Block> blockKey(String path) {
        return ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.Identifier.fromNamespaceAndPath("cb_s26", path));
    }

    static net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProps(String path) {
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().setId(blockKey(path));
    }

    /** a 26.1.2 DripstoneUtils subclass using its protected helpers */
    static class DripstoneSub extends DripstoneUtils {
        void column(List<net.minecraft.world.level.block.state.BlockState> out) {
            buildBaseToTipColumn(net.minecraft.core.Direction.UP, 4, true, out::add);
        }
    }

    /** a 26.1.2 PointedDripstoneBlock subclass: super(properties) */
    static class DripBlock extends net.minecraft.world.level.block.PointedDripstoneBlock {
        DripBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties p) {
            super(p);
        }
    }

    /** a 26.1.2 structure processor: extends the abstract class, getType(), processBlock with the original info */
    static class StoneProc extends net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor {
        static final com.mojang.serialization.MapCodec<StoneProc> CODEC = com.mojang.serialization.MapCodec.unit(StoneProc::new);
        static net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<StoneProc> TYPE;

        @Override
        public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo processBlock(
                net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.core.BlockPos pivot,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo original,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo current,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings settings) {
            return new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo(current.pos(),
                Blocks.STONE.defaultBlockState(), current.nbt());
        }

        @Override
        protected net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<?> getType() {
            return TYPE;
        }
    }

    /** records what a later 26.1.2 processor is handed, and calls super.processBlock */
    static class SpyProc extends net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor {
        net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo seenOriginal, seenCurrent;

        @Override
        public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo processBlock(
                net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.core.BlockPos pivot,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo original,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo current,
                net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings settings) {
            seenOriginal = original;
            seenCurrent = current;
            return super.processBlock(level, pos, pivot, original, current, settings);
        }

        @Override
        protected net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<?> getType() {
            return net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.NOP;
        }
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

        // PlayerTeam's 26.1.2 colour and int options on 26.2's TeamColor and byte options (rt/CbS26Team)
        net.minecraft.world.scores.PlayerTeam team = new net.minecraft.world.scores.PlayerTeam(new net.minecraft.world.scores.Scoreboard(), "cbs26");
        boolean noColor = team.getColor() == net.minecraft.ChatFormatting.RESET;
        team.setColor(net.minecraft.ChatFormatting.DARK_AQUA);
        boolean aqua = team.getColor() == net.minecraft.ChatFormatting.DARK_AQUA;
        team.setColor(net.minecraft.ChatFormatting.BOLD);
        check("team.color", noColor && aqua && team.getColor() == net.minecraft.ChatFormatting.BOLD, team.getColor());
        team.setAllowFriendlyFire(false);
        team.setSeeFriendlyInvisibles(true);
        int packed = team.packOptions();
        team.unpackOptions(1);
        check("team.options", packed == 2 && team.isAllowFriendlyFire() && !team.canSeeFriendlyInvisibles(), packed);

        // EntityType.byString (rt/CbS26Entity)
        check("entity.byString", EntityType.byString("minecraft:pig").orElse(null) == EntityType.PIG
            && EntityType.byString("minecraft:no_such").isEmpty(), "");

        // dripstone worldgen on 26.2's speleothems (rt/CbS26Dripstone)
        try {
            var cluster = new net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration(12,
                net.minecraft.util.valueproviders.ConstantInt.of(4), net.minecraft.util.valueproviders.ConstantInt.of(5), 1, 3,
                net.minecraft.util.valueproviders.ConstantInt.of(2), net.minecraft.util.valueproviders.ConstantFloat.of(0.5F),
                net.minecraft.util.valueproviders.ConstantFloat.of(0.25F), 0.1F, 3, 8);
            check("dripstone.cluster.fields", cluster.floorToCeilingSearchRange == 12 && cluster.height.sample(null) == 4
                && cluster.radius.sample(null) == 5 && cluster.maxStalagmiteStalactiteHeightDiff == 1 && cluster.heightDeviation == 3
                && cluster.dripstoneBlockLayerThickness.sample(null) == 2 && cluster.density.sample(null) == 0.5F
                && cluster.wetness.sample(null) == 0.25F && cluster.chanceOfDripstoneColumnAtMaxDistanceFromCenter == 0.1F
                && cluster.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn == 3 && cluster.maxDistanceFromCenterAffectingHeightBias == 8, cluster);
            com.google.gson.JsonElement cj = net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration.CODEC
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, cluster).getOrThrow();
            var back = net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration.CODEC
                .parse(com.mojang.serialization.JsonOps.INSTANCE, cj).getOrThrow();
            check("dripstone.cluster.codec", cj.getAsJsonObject().has("dripstone_block_layer_thickness")
                && cj.getAsJsonObject().has("chance_of_dripstone_column_at_max_distance_from_center")
                && cj.getAsJsonObject().size() == 11 && back.heightDeviation == 3 && back.radius.sample(null) == 5, cj);
            var pointed = new net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration(0.1F, 0.2F, 0.3F, 0.4F);
            var dflt = net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration.CODEC
                .parse(com.mojang.serialization.JsonOps.INSTANCE, new com.google.gson.JsonObject()).getOrThrow();
            check("dripstone.pointed", pointed.chanceOfTallerDripstone == 0.1F && pointed.chanceOfDirectionalSpread == 0.2F
                && pointed.chanceOfSpreadRadius2 == 0.3F && pointed.chanceOfSpreadRadius3 == 0.4F
                && dflt.chanceOfTallerDripstone == 0.2F && dflt.chanceOfDirectionalSpread == 0.7F && dflt.chanceOfSpreadRadius3 == 0.5F, dflt);
            check("dripstone.feature", describe(net.minecraft.world.level.levelgen.feature.Feature.POINTED_DRIPSTONE).startsWith("value\tminecraft:worldgen/feature\t")
                && describe(net.minecraft.world.level.levelgen.feature.Feature.DRIPSTONE_CLUSTER).startsWith("value\tminecraft:worldgen/feature\t"),
                describe(net.minecraft.world.level.levelgen.feature.Feature.POINTED_DRIPSTONE));
            var large = new net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration(30,
                net.minecraft.util.valueproviders.ConstantInt.of(3), net.minecraft.util.valueproviders.ConstantFloat.of(1.0F), 0.33F,
                net.minecraft.util.valueproviders.ConstantFloat.of(1.0F), net.minecraft.util.valueproviders.ConstantFloat.of(1.0F),
                net.minecraft.util.valueproviders.ConstantFloat.of(0.0F), 4, 0.6F);
            com.google.gson.JsonElement lj = net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration.CODEC
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, large).getOrThrow();
            lj.getAsJsonObject().remove("floor_to_ceiling_search_range");
            var lback = net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration.CODEC
                .parse(com.mojang.serialization.JsonOps.INSTANCE, lj).getOrThrow();
            check("dripstone.large", large.minRadiusForWind == 4 && large.maxColumnRadiusToCaveHeightRatio == 0.33F
                && !lj.getAsJsonObject().has("replaceable_blocks") && lback.floorToCeilingSearchRange == 30 && lback.minBluntnessForWind == 0.6F, lj);
            check("dripstone.utils", DripstoneUtils.isDripstoneBase(Blocks.DRIPSTONE_BLOCK.defaultBlockState())
                && DripstoneUtils.isDripstoneBaseOrLava(Blocks.DRIPSTONE_BLOCK.defaultBlockState()), ""); // other states read the tag, unbound at init on 26.1.2 too
            List<net.minecraft.world.level.block.state.BlockState> column = new ArrayList<>();
            new DripstoneSub().column(column);
            check("dripstone.column", column.size() == 4 && column.get(0).is(Blocks.POINTED_DRIPSTONE)
                && "base".equals(column.get(0).getValue(net.minecraft.world.level.block.PointedDripstoneBlock.THICKNESS).getSerializedName())
                && "tip_merge".equals(column.get(3).getValue(net.minecraft.world.level.block.PointedDripstoneBlock.THICKNESS).getSerializedName()), column);
            net.minecraft.world.level.block.state.BlockState hanging = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(net.minecraft.world.level.block.PointedDripstoneBlock.TIP_DIRECTION, net.minecraft.core.Direction.DOWN);
            check("dripstone.canDrip", !net.minecraft.world.level.block.PointedDripstoneBlock.canDrip(Blocks.POINTED_DRIPSTONE.defaultBlockState())
                && net.minecraft.world.level.block.PointedDripstoneBlock.canDrip(hanging), "");
            net.minecraft.world.level.block.PointedDripstoneBlock plain = new net.minecraft.world.level.block.PointedDripstoneBlock(blockProps("plain_drip"));
            DripBlock sub = new DripBlock(blockProps("sub_drip"));
            net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, blockKey("plain_drip"), plain);
            net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, blockKey("sub_drip"), sub);
            check("dripstone.block", plain.defaultBlockState().hasProperty(net.minecraft.world.level.block.PointedDripstoneBlock.THICKNESS)
                && sub.defaultBlockState().hasProperty(net.minecraft.world.level.block.PointedDripstoneBlock.TIP_DIRECTION), sub);
        } catch (Throwable e) {
            StringBuilder st = new StringBuilder(e.toString());
            for (StackTraceElement el : e.getStackTrace()) {
                if (el.getClassName().startsWith("cb.") || el.getClassName().contains("centurybridge")) {
                    st.append(" @ ").append(el);
                }
            }
            check("dripstone", false, st);
        }

        // structure processors: 26.1.2's abstract class, registry of types, original info (facade + rt/CbS26Processors)
        try {
            StoneProc.TYPE = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.register("cb_s26_stone", StoneProc.CODEC);
            check("processor.register", StoneProc.TYPE.codec() == StoneProc.CODEC
                && "minecraft:cb_s26_stone".equals(String.valueOf(BuiltInRegistries.STRUCTURE_PROCESSOR.getKey(StoneProc.TYPE)))
                && BuiltInRegistries.STRUCTURE_PROCESSOR.getValue(net.minecraft.resources.Identifier.withDefaultNamespace("block_ignore"))
                    == net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.BLOCK_IGNORE, StoneProc.TYPE);
            com.google.gson.JsonElement pj = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.SINGLE_CODEC
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, new StoneProc()).getOrThrow();
            check("processor.codec", pj.toString().contains("minecraft:cb_s26_stone")
                && net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.SINGLE_CODEC
                    .parse(com.mojang.serialization.JsonOps.INSTANCE, pj).getOrThrow() instanceof StoneProc, pj);
            SpyProc spy = new SpyProc();
            var settings = new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings()
                .addProcessor(new StoneProc()).addProcessor(spy);
            var raw = new net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo(
                new net.minecraft.core.BlockPos(1, 2, 3), Blocks.DIRT.defaultBlockState(), null);
            var out = net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.processBlockInfos(null,
                new net.minecraft.core.BlockPos(100, 64, 100), net.minecraft.core.BlockPos.ZERO, settings, List.of(raw));
            check("processor.original", out.size() == 1 && out.get(0).state().is(Blocks.STONE) && spy.seenOriginal == raw
                && spy.seenCurrent != null && spy.seenCurrent.state().is(Blocks.STONE), spy.seenOriginal);
            var prot = new net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor(net.minecraft.tags.BlockTags.FEATURES_CANNOT_REPLACE);
            com.google.gson.JsonElement prj = net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor.CODEC
                .codec().encodeStart(com.mojang.serialization.JsonOps.INSTANCE, prot).getOrThrow();
            check("processor.protected", prot.cannotReplace == net.minecraft.tags.BlockTags.FEATURES_CANNOT_REPLACE
                && prj.toString().contains("#minecraft:features_cannot_replace"), prj);
        } catch (Throwable e) {
            StringBuilder st = new StringBuilder(e.toString());
            for (StackTraceElement el : e.getStackTrace()) {
                if (el.getClassName().startsWith("cb.") || el.getClassName().contains("centurybridge")) {
                    st.append(" @ ").append(el);
                }
            }
            check("processor", false, st);
        }

        // BlockFamilies copper constants on 26.2's WeatheringCopperCollection (rt/CbS26BlockFamilies)
        check("families.copper", net.minecraft.data.BlockFamilies.COPPER_BLOCK.getBaseBlock() == Blocks.COPPER_BLOCK
            && net.minecraft.data.BlockFamilies.EXPOSED_COPPER.getBaseBlock() == Blocks.EXPOSED_COPPER
            && net.minecraft.data.BlockFamilies.WAXED_OXIDIZED_COPPER.getBaseBlock() == Blocks.WAXED_OXIDIZED_COPPER
            && net.minecraft.data.BlockFamilies.WEATHERED_CUT_COPPER.getBaseBlock() == Blocks.WEATHERED_CUT_COPPER
            && net.minecraft.data.BlockFamilies.WAXED_CUT_COPPER.getBaseBlock() == Blocks.WAXED_CUT_COPPER,
            net.minecraft.data.BlockFamilies.EXPOSED_COPPER.getBaseBlock());

        // ServerboundSpectateEntityPacket on 26.2's ServerboundSpectatorActionPacket (rt/CbS26Packets)
        var spec = new net.minecraft.network.protocol.game.ServerboundSpectateEntityPacket(42);
        check("packet.spectate", spec.entityId() == 42, spec);
        // ClientboundSetPlayerTeamPacket.Parameters getters on 26.2's record (rt/CbS26Team)
        net.minecraft.world.scores.PlayerTeam pteam = new net.minecraft.world.scores.PlayerTeam(new net.minecraft.world.scores.Scoreboard(), "cbs26p");
        pteam.setColor(net.minecraft.ChatFormatting.GOLD);
        pteam.setAllowFriendlyFire(true);
        var params = new net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters(pteam);
        check("packet.teamParameters", params.getColor() == net.minecraft.ChatFormatting.GOLD && (params.getOptions() & 1) == 1
            && params.getNametagVisibility() == net.minecraft.world.scores.Team.Visibility.ALWAYS, params.getColor());

        // SimpleRandomFeatureConfiguration on 26.2's CompositeFeatureConfiguration
        net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature> none = net.minecraft.core.HolderSet.direct();
        var simple = new net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration(none);
        check("feature.simpleRandom", simple.features == none && simple.getSubFeatures().count() == 0, simple);

        // generic drift (rt/CbS26Generic): PlayerTeam.Packed colour as Optional<ChatFormatting>
        var gpacked = new net.minecraft.world.scores.PlayerTeam.Packed("cbs26k", Optional.empty(), Optional.of(net.minecraft.ChatFormatting.RED),
            true, true, net.minecraft.network.chat.Component.empty(), net.minecraft.network.chat.Component.empty(),
            net.minecraft.world.scores.Team.Visibility.ALWAYS, net.minecraft.world.scores.Team.Visibility.ALWAYS,
            net.minecraft.world.scores.Team.CollisionRule.ALWAYS, List.of());
        Optional<net.minecraft.ChatFormatting> pc = gpacked.color();
        check("generic.packedColor", pc.isPresent() && pc.get() == net.minecraft.ChatFormatting.RED, pc);
        check("generic.densityCodec", net.minecraft.world.level.levelgen.DensityFunction.CODEC != null
            && net.minecraft.world.level.levelgen.DensityFunction.DIRECT_CODEC != null
            && net.minecraft.world.level.levelgen.DensityFunction.HOLDER_HELPER_CODEC != null, "codecs");
        java.util.Set<org.apache.commons.lang3.tuple.Pair<String, net.minecraft.util.profiling.metrics.MetricCategory>> charted =
            net.minecraft.util.profiling.InactiveProfiler.INSTANCE.getChartedPaths();
        check("generic.chartedPaths", charted.isEmpty(), charted);

        // CubicSpline (rt/CbS26Spline): value transformer, apply, mapAll
        net.minecraft.util.BoundedFloatFunction<Float> coord = new net.minecraft.util.BoundedFloatFunction<>() {
            public float apply(Float f) { return f; }
            public float minValue() { return -10f; }
            public float maxValue() { return 10f; }
        };
        net.minecraft.util.BoundedFloatFunction<Float> twice = new net.minecraft.util.BoundedFloatFunction<>() {
            public float apply(Float f) { return f * 2f; }
            public float minValue() { return -20f; }
            public float maxValue() { return 20f; }
        };
        net.minecraft.util.CubicSpline<Float, net.minecraft.util.BoundedFloatFunction<Float>> spline =
            net.minecraft.util.CubicSpline.builder(coord, twice).addPoint(0f, 0f).addPoint(1f, 1f).build();
        if (spline instanceof net.minecraft.util.CubicSpline.Multipoint<Float, net.minecraft.util.BoundedFloatFunction<Float>> mp) {
            float mid = mp.apply(0.5f), end = mp.apply(1f);
            check("spline.apply", Math.abs(mid - 1f) < 1e-4 && Math.abs(end - 2f) < 1e-4, mid + "," + end);
            var mapped = mp.mapAll(c -> c);
            check("spline.mapAll", mapped instanceof net.minecraft.util.CubicSpline.Multipoint<?, ?> m2
                && Math.abs(((net.minecraft.util.CubicSpline.Multipoint<Float, ?>) m2).apply(1f) - 2f) < 1e-4, mapped);
        } else {
            check("spline.apply", false, spline);
        }
        var cst = (net.minecraft.util.CubicSpline.Constant<Float, net.minecraft.util.BoundedFloatFunction<Float>>) net.minecraft.util.CubicSpline.<Float, net.minecraft.util.BoundedFloatFunction<Float>>constant(3f);
        check("spline.constant", cst.apply(0f) == 3f, cst);

        System.out.println("CB-S26 DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS: " + FAILS));
    }
}
