package cb.s7level;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_18;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_2746;
import net.minecraft.class_2753;
import net.minecraft.class_2754;
import net.minecraft.class_2758;
import net.minecraft.class_2791;
import net.minecraft.class_2814;
import net.minecraft.class_2837;
import net.minecraft.class_2841;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3499;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_3621;
import net.minecraft.class_3748;
import net.minecraft.class_6755;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import net.minecraft.class_8490;
import net.minecraft.class_8574;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.21.1-era server mod (intermediary names) for s7level's bridges of the 1.21.1 -> 1.21.2 step: block state
 * property subclasses, palette copies, block entity types and their builders (vanilla and Fabric API's), Fabric API's
 * block settings, packed ticks, FlowingFluid.canSpreadTo overrides deciding spreading, SavedData.save(file) overrides,
 * the carved pumpkin's Equipable class, JigsawBlock.canAttach and LootDataType.deserialize. Every check prints one
 * "CB-LEVELTEST ok|FAIL ..." line; the last is "CB-LEVELTEST DONE <passed>/<checks>".
 */
public class S7LevelTestMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        System.out.println("CB-LEVELTEST " + (ok ? "ok   " : "FAIL ") + what);
        if (!ok) {
            FAILS.add(what);
        }
    }

    // ---- property subclasses (1.21.2 made the vanilla ones final) ----

    static class MyBool extends class_2746 {
        MyBool(String name) {
            super(name);
        }
    }

    static class MyInt extends class_2758 {
        MyInt(String name, int min, int max) {
            super(name, min, max);
        }
    }

    static class MyEnum extends class_2754<class_2350> {
        MyEnum(String name, Collection<class_2350> values) {
            super(name, class_2350.class, values);
        }
    }

    static class MyDir extends class_2753 {
        MyDir(String name, Collection<class_2350> values) {
            super(name, values);
        }
    }

    // ---- block entity type subclass ----

    static class MyBeType extends class_2591<net.minecraft.class_2586> {
        MyBeType(Set<class_2248> blocks) {
            super((pos, state) -> null, blocks, null);
        }
    }

    // ---- fluids: canSpreadTo override ----

    static class NoSpreadWater extends class_3621.class_3623 {
        int asked;

        @Override
        protected boolean method_15738(class_1922 level, class_2338 pos, class_2680 state, class_2350 dir, class_2338 toPos,
                                       class_2680 toState, class_3610 toFluid, class_3611 fluid) {
            asked++;
            return false;
        }

        void runSpread(class_1937 level, class_2338 pos) {
            this.method_15725(level, pos, this.method_15785());
        }

        @Override
        public boolean method_15780(class_3611 fluid) {
            return fluid == this || super.method_15780(fluid);
        }
    }

    static class PlainWater extends class_3621.class_3623 {
        void runSpread(class_1937 level, class_2338 pos) {
            this.method_15725(level, pos, this.method_15785());
        }

        @Override
        public boolean method_15780(class_3611 fluid) {
            return fluid == this || super.method_15780(fluid);
        }

        boolean askSpread(class_1937 level, class_2338 pos) {
            class_2680 air = class_2246.field_10124.method_9564();
            return this.method_15738(level, pos, air, class_2350.field_11033, pos.method_10074(), air,
                air.method_26227(), net.minecraft.class_3612.field_15910);
        }
    }

    // ---- saved data with a save(file) override ----

    static class MyData extends class_18 {
        static boolean fileSaveAsked;

        @Override
        public class_2487 method_75(class_2487 tag, class_7225.class_7874 registries) {
            tag.method_10569("cb", 7);
            return tag;
        }

        @Override
        public void method_17919(File file, class_7225.class_7874 registries) {
            fileSaveAsked = true;
            super.method_17919(file, registries);
        }
    }

    static NoSpreadWater noSpread;
    static PlainWater plain;
    static class_2248 fbsBlock, noSpreadBlock, plainBlock;
    static class_2591<net.minecraft.class_2586> builtType, madeType, fabricBuiltType;
    static MyBeType subType;

    @Override
    public void onInitialize() {
        try {
            noSpread = class_2378.method_10230(class_7923.field_41173, class_2960.method_60655("cb_s7level", "nospread"), new NoSpreadWater());
            plain = class_2378.method_10230(class_7923.field_41173, class_2960.method_60655("cb_s7level", "plain"), new PlainWater());
            fbsBlock = class_2378.method_10230(class_7923.field_41175, class_2960.method_60655("cb_s7level", "fbs"),
                new class_2248(FabricBlockSettings.copyOf(class_2246.field_10340).strength(3.0f)));
            // the test fluids' own liquid blocks: the block a fluid spreads from holds that fluid
            noSpreadBlock = class_2378.method_10230(class_7923.field_41175, class_2960.method_60655("cb_s7level", "nospread_block"),
                new net.minecraft.class_2404(noSpread, net.minecraft.class_4970.class_2251.method_9630(class_2246.field_10382)));
            plainBlock = class_2378.method_10230(class_7923.field_41175, class_2960.method_60655("cb_s7level", "plain_block"),
                new net.minecraft.class_2404(plain, net.minecraft.class_4970.class_2251.method_9630(class_2246.field_10382)));
            // 1.21.1 made block entity types during mod init too: each takes its registry's intrusive holder
            builtType = reg("built", class_2591.class_2592.<net.minecraft.class_2586>method_20528((pos, st) -> null, class_2246.field_10340).method_11034(null));
            madeType = reg("made", new class_2591<>((pos, st) -> null, Set.of(class_2246.field_10340), null));
            subType = reg("sub", new MyBeType(Set.of(class_2246.field_10566)));
            @SuppressWarnings("unchecked")
            FabricBlockEntityType.Builder<net.minecraft.class_2586> fb =
                (FabricBlockEntityType.Builder<net.minecraft.class_2586>) (Object) class_2591.class_2592.<net.minecraft.class_2586>method_20528((pos, st) -> null, class_2246.field_10340);
            fabricBuiltType = reg("fabric", fb.build());
        } catch (Throwable e) {
            check(false, "registering test fluids/block threw " + e);
            e.printStackTrace(System.out);
        }
        Thread t = new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
                Object game = FabricLoader.getInstance().getGameInstance();
                if (game instanceof MinecraftServer server && server.method_3806() && server.method_30002() != null) {
                    server.execute(() -> run(server));
                    return;
                }
            }
        }, "cb-s7level");
        t.setDaemon(true);
        t.start();
    }

    interface Step {
        void run() throws Exception;
    }

    static <T extends class_2591<?>> T reg(String name, T type) {
        return class_2378.method_10230(class_7923.field_41181, class_2960.method_60655("cb_s7level", name), type);
    }

    static void step(String what, Step r) {
        try {
            r.run();
        } catch (Throwable e) {
            check(false, what + " threw " + e);
            e.printStackTrace(System.out);
        }
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();

        step("properties", () -> {
            MyBool b = new MyBool("cbflag");
            check(b instanceof class_2746 && b.method_11898().size() == 2 && b.method_11900("true").equals(Optional.of(true)),
                "a 1.21.1 BooleanProperty subclass loads and answers its values");
            MyInt n = new MyInt("cblevel", 0, 3);
            check(n.method_11898().size() == 4 && n.method_11900("2").equals(Optional.of(2)),
                "a 1.21.1 IntegerProperty subclass loads (values 0..3)");
            MyEnum e = new MyEnum("cbdir", List.of(class_2350.field_11043, class_2350.field_11035));
            check(e.method_11898().size() == 2 && e.method_11900("south").equals(Optional.of(class_2350.field_11035)),
                "a 1.21.1 EnumProperty subclass loads (values in order)");
            MyDir d = new MyDir("cbfacing", List.of(class_2350.field_11034, class_2350.field_11039));
            Object o = d;
            check(o instanceof class_2753 && d.method_11898().size() == 2 && d.method_11899().equals("cbfacing"),
                "a 1.21.1 DirectionProperty subclass loads and is a DirectionProperty");
            Object bo = b;
            check(!(bo instanceof class_2753), "a boolean property is no DirectionProperty");
        });

        step("palette copy", () -> {
            class_2814<class_2680> p = new class_2814<>(class_2248.field_10651, 4, null);
            class_2680 stone = class_2246.field_10340.method_9564();
            int id = p.method_12291(stone);
            class_2837<class_2680> c = p.method_39956();
            check(c != p && c.method_12197() == 1 && c.method_12288(id) == stone,
                "HashMapPalette.copy() answers an equal palette (its own resize handler)");
        });

        step("paletted container RO copy", () -> {
            class_2841<class_2680> real = new class_2841<>(class_2248.field_10651, class_2246.field_10124.method_9564(), class_2841.class_6563.field_34569);
            real.method_35321(1, 2, 3, class_2246.field_10340.method_9564());
            MyRO ro = new MyRO(real);
            Object copy = MyRO.class.getMethod("method_39957").invoke(ro);
            check(copy instanceof class_2841<?> pc && pc != real && pc.method_12321(1, 2, 3) == class_2246.field_10340.method_9564()
                && pc.method_12321(0, 0, 0) == class_2246.field_10124.method_9564(),
                "a 1.21.1 PalettedContainerRO implementation answers 1.21.2's copy() with the same values");
        } );

        step("block entity types", () -> {
            check(builtType.method_20526(class_2246.field_10340.method_9564()) && !builtType.method_20526(class_2246.field_10566.method_9564()),
                "BlockEntityType.Builder.of(..).build(null) is valid for its blocks only");
            check(madeType.method_20526(class_2246.field_10340.method_9564()), "new BlockEntityType(factory, blocks, null)");
            check(subType.method_20526(class_2246.field_10566.method_9564()), "a 1.21.1 BlockEntityType subclass");
            check(fabricBuiltType.method_20526(class_2246.field_10340.method_9564()), "Fabric API's FabricBlockEntityType.Builder.build()");
        });

        step("fabric block settings", () -> {
            check(fbsBlock != null && fbsBlock.method_36555() == 3.0f && fbsBlock.method_9520() == 3.0f,
                "FabricBlockSettings.copyOf(stone).strength(3) builds a block with that strength");
        });

        step("packed ticks", () -> {
            class_2791.class_6745 packed = new class_2791.class_6745(new class_6755<>(), new class_6755<>());
            check(packed.comp_239() != null && packed.comp_240() != null, "new ChunkAccess.PackedTicks(containers)");
        });

        step("fluid spreading", () -> {
            class_2338 spawn = level.method_43126();
            class_2338 a = new class_2338(spawn.method_10263(), 250, spawn.method_10260());
            class_2338 b = a.method_10069(8, 0, 0);
            class_2680 air = class_2246.field_10124.method_9564();
            for (class_2338 p : List.of(a, b)) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        level.method_8652(p.method_10069(dx, 0, dz), air, 3);
                        level.method_8652(p.method_10069(dx, -1, dz), air, 3);
                    }
                }
            }
            // the spreading fluid's own block, as in the world (its new liquid below is computed from it)
            level.method_8652(a, noSpreadBlock.method_9564(), 2);
            level.method_8652(b, class_2246.field_10382.method_9564(), 2);
            noSpread.runSpread(level, a);
            // control: vanilla water's own tick through the same (mixin-carrying) spread code
            level.method_8316(b).method_15770(level, b);
            boolean noBelow = level.method_8320(a.method_10074()).method_27852(class_2246.field_10124);
            boolean noSide = level.method_8320(a.method_10069(1, 0, 0)).method_27852(class_2246.field_10124);
            check(noSpread.asked > 0 && noBelow && noSide,
                "a canSpreadTo override answering no keeps the fluid from spreading (asked " + noSpread.asked + "x)");
            check(level.method_8320(b.method_10074()).method_27852(class_2246.field_10382),
                "vanilla water (no override) still spreads down through the same code");
            check(plain.askSpread(level, a), "canSpreadTo(..) called by a 1.21.1 subclass answers 1.21.1's body (into air: yes)");
        });

        step("saved data", () -> {
            MyData data = level.method_17983().method_17924(new class_18.class_8645<>(MyData::new, (tag, reg) -> new MyData(), null), "cb_s7level");
            data.method_80();
            MyData.fileSaveAsked = false;
            level.method_17983().method_125();
            check(MyData.fileSaveAsked, "DimensionDataStorage asks a 1.21.1 save(file, registries) override");
        });

        step("carved pumpkin", () -> {
            Object pumpkin = class_2246.field_10147;
            Object lantern = class_2246.field_10009;
            check(pumpkin instanceof class_8574 && !(lantern instanceof class_8574),
                "the carved pumpkin is an EquipableCarvedPumpkinBlock, the jack o'lantern is not");
        });

        step("jigsaw", () -> {
            class_2487 t1 = new class_2487();
            t1.method_10582("target", "cb:x");
            t1.method_10582("joint", "rollable");
            class_2487 t2 = new class_2487();
            t2.method_10582("name", "cb:x");
            class_2680 up = class_2246.field_16540.method_9564().method_11657(class_3748.field_23262, net.minecraft.class_5000.field_23387);
            class_2680 down = class_2246.field_16540.method_9564().method_11657(class_3748.field_23262, net.minecraft.class_5000.field_23383);
            class_3499.class_3501 i1 = new class_3499.class_3501(class_2338.field_10980, up, t1);
            class_3499.class_3501 i2 = new class_3499.class_3501(class_2338.field_10980, down, t2);
            check(class_3748.method_16546(i1, i2), "JigsawBlock.canAttach compares 1.21.1's target and name strings");
        });

        step("stacked contents", () -> {
            net.minecraft.class_1662 sc = new net.minecraft.class_1662();
            new net.minecraft.class_1277(new net.minecraft.class_1799(class_2246.field_10340, 5)).method_7683(sc);
            int stone = net.minecraft.class_1792.method_7880(class_2246.field_10340.method_8389());
            check(sc.field_7550.get(stone) == 5, "SimpleContainer.fillStackedContents(StackedContents) counts its stacks (stone x5: "
                + sc.field_7550.get(stone) + ")");
            net.minecraft.class_1662 sc2 = new net.minecraft.class_1662();
            net.minecraft.class_1737 own = c -> c.field_7550.put(stone, 3);
            own.method_7683(sc2);
            check(sc2.field_7550.get(stone) == 3, "a 1.21.1 StackedContentsCompatible lambda fills 1.21.1's StackedContents");
        });

        step("loot data", () -> {
            JsonObject cond = new JsonObject();
            cond.addProperty("condition", "minecraft:survives_explosion");
            check(class_8490.field_44496.method_51204(class_2960.method_60655("cb", "c"), JsonOps.INSTANCE, cond).isPresent(),
                "LootDataType.PREDICATE.deserialize parses a condition");
        });

        System.out.println("CB-LEVELTEST DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILED: " + FAILS));
    }

    /** a 1.21.1 read-only container view over a real one */
    static class MyRO implements net.minecraft.class_7522<class_2680> {
        final class_2841<class_2680> real;

        MyRO(class_2841<class_2680> real) {
            this.real = real;
        }

        public class_2680 method_12321(int x, int y, int z) {
            return this.real.method_12321(x, y, z);
        }

        public void method_39793(java.util.function.Consumer<class_2680> c) {
            this.real.method_39793(c);
        }

        public void method_12325(net.minecraft.class_2540 buf) {
            this.real.method_12325(buf);
        }

        public int method_12327() {
            return this.real.method_12327();
        }

        public boolean method_19526(java.util.function.Predicate<class_2680> p) {
            return this.real.method_19526(p);
        }

        public void method_21732(class_2841.class_4464<class_2680> counter) {
            this.real.method_21732(counter);
        }

        public class_2841<class_2680> method_44350() {
            return this.real.method_44350();
        }

        public net.minecraft.class_7522.class_6562<class_2680> method_44345(net.minecraft.class_2359<class_2680> map, class_2841.class_6563 s) {
            return this.real.method_44345(map, s);
        }
    }
}
