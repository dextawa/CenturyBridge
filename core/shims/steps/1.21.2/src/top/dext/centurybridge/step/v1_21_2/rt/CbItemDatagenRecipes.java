package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.stream.Stream;
import net.minecraft.class_1860;
import net.minecraft.class_161;
import net.minecraft.class_5321;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import net.minecraft.class_8779;
import net.minecraft.class_8790;

/**
 * A 1.21.2 RecipeProvider standing for 1.21.1's static RecipeProvider helpers (dev-time datagen). 1.21.1's helpers were
 * static and took the RecipeOutput; 1.21.2's are instance methods of a provider bound to its registries and output.
 * Each 1.21.1 helper forwards to {@link #of} the output it was handed: the provider the 1.21.1 RecipeProvider facade
 * bound to its own output while its buildRecipes runs, else one made for that output; a helper without an output
 * (a builder, a criterion) to {@link #current}. The registries are the running facade's, else the built-in registries
 * (what 1.21.1's helpers resolved items and blocks in). The forwarding statics are generated
 * (core/tools/gen_s7item_datagen_recipes.py); protected 1.21.2 members are reachable because this is a subclass.
 */
public class CbItemDatagenRecipes extends net.minecraft.class_2446 {

    private static final ThreadLocal<CbItemDatagenRecipes> CURRENT = new ThreadLocal<>();

    /** an output that takes nothing, for a provider asked only for builders and criteria */
    private static final class_8790 NOWHERE = new class_8790() {
        @Override
        public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
            throw new IllegalStateException("a recipe was saved through 1.21.1's RecipeProvider helpers with no output");
        }

        @Override
        public class_161.class_162 method_53818() {
            return class_161.class_162.method_51698();
        }

        @Override
        public void method_62738() {
        }
    };

    protected CbItemDatagenRecipes(class_7225.class_7874 registries, class_8790 output) {
        super(registries, output);
    }

    /** nothing of its own to build: it only lends its helpers */
    @Override
    public void method_10419() {
    }

    /** the output that takes nothing (the 1.21.1 RecipeProvider facade's own 1.21.2 side) */
    public static class_8790 nowhere() {
        return NOWHERE;
    }

    /** the built-in registries 1.21.1's helpers resolved in, as a lookup provider */
    public static class_7225.class_7874 builtIn() {
        return class_7225.class_7874.method_46761(Stream.of(class_7923.field_41178, class_7923.field_41175));
    }

    /** the 1.21.1 RecipeProvider facade's run binds a provider to its output while its buildRecipes runs */
    static CbItemDatagenRecipes bind(class_7225.class_7874 registries, class_8790 output) {
        CbItemDatagenRecipes p = new CbItemDatagenRecipes(registries, output);
        CURRENT.set(p);
        return p;
    }

    static void unbind() {
        CURRENT.remove();
    }

    /** the 1.21.1 RecipeProvider facade (another package): {@link #bind} for its buildRecipes */
    public static void bindFor(class_7225.class_7874 registries, class_8790 output) {
        bind(registries, output);
    }

    /** {@link #unbind}, after its buildRecipes */
    public static void unbindFor() {
        unbind();
    }

    /** the provider for a 1.21.1 helper handed this output */
    static CbItemDatagenRecipes of(class_8790 output) {
        CbItemDatagenRecipes p = CURRENT.get();
        if (p != null && p.field_53721 == output) {
            return p;
        }
        return new CbItemDatagenRecipes(p != null ? p.field_48981 : builtIn(), output);
    }

    /** the provider for a 1.21.1 helper without an output */
    static CbItemDatagenRecipes current() {
        CbItemDatagenRecipes p = CURRENT.get();
        return p != null ? p : new CbItemDatagenRecipes(builtIn(), NOWHERE);
    }

    /**
     * 1.21.1's RecipeProvider.run(output, registries), shared by the facades standing for 1.21.1's providers
     * (RecipeProvider, VanillaRecipeProvider, BundleRecipeProvider): buildRecipes gets an output that writes each recipe
     * as JSON under the recipe path and its advancement through buildAdvancement, refusing a duplicate id; the root
     * advancement 1.21.1's output gave its recipes' advancements; a 1.21.2 provider bound to that output answers the
     * static helpers while buildRecipes runs
     */
    public static java.util.concurrent.CompletableFuture<?> runLegacy(net.minecraft.class_7403 output, class_7225.class_7874 registries,
            net.minecraft.class_7784.class_7489 recipePaths, java.util.function.Consumer<class_8790> buildRecipes,
            java.util.function.Function<class_8779, java.util.concurrent.CompletableFuture<?>> buildAdvancement) {
        java.util.Set<net.minecraft.class_2960> ids = new java.util.HashSet<>();
        java.util.List<java.util.concurrent.CompletableFuture<?>> writes = new java.util.ArrayList<>();
        class_8790 recipes = new class_8790() {
            @Override
            public void method_53819(class_5321<class_1860<?>> key, class_1860<?> recipe, class_8779 advancement) {
                net.minecraft.class_2960 id = key.method_29177();
                if (!ids.add(id)) {
                    throw new IllegalStateException("Duplicate recipe " + id);
                }
                writes.add(net.minecraft.class_2405.method_53496(output, registries, class_1860.field_47319, recipe,
                    recipePaths.method_44107(id)));
                if (advancement != null) {
                    writes.add(buildAdvancement.apply(advancement));
                }
            }

            @Override
            public class_161.class_162 method_53818() {
                return class_161.class_162.method_51698().method_708(net.minecraft.class_5797.field_39377);
            }

            /** 1.21.1's output had no root advancement of its own */
            @Override
            public void method_62738() {
            }
        };
        bind(registries, recipes);
        try {
            buildRecipes.accept(recipes);
        } finally {
            unbind();
        }
        return java.util.concurrent.CompletableFuture.allOf(writes.toArray(java.util.concurrent.CompletableFuture[]::new));
    }

    /**
     * A 1.21.2 RecipeProvider whose buildRecipes is a 1.21.1 one (Fabric API 0.106's createRecipeProvider of a 0.116
     * FabricRecipeProvider): the 1.21.1 body runs on the output with this provider answering the static helpers
     */
    public static net.minecraft.class_2446 delegating(class_7225.class_7874 registries, class_8790 output,
            java.util.function.Consumer<class_8790> buildRecipes) {
        return new CbItemDatagenRecipes(registries, output) {
            @Override
            public void method_10419() {
                CbItemDatagenRecipes before = CURRENT.get();
                CURRENT.set(this);
                try {
                    buildRecipes.accept(output);
                } finally {
                    if (before != null) {
                        CURRENT.set(before);
                    } else {
                        CURRENT.remove();
                    }
                }
            }
        };
    }

    /** 1.21.1 VanillaRecipeProvider.buildRecipes(output): a 1.21.2 VanillaRecipeProvider bound to the output builds them */
    public static void vanillaRecipes(class_8790 output) {
        new net.minecraft.class_7803(runningRegistries(), output).method_10419();
    }

    /** the registries of the 1.21.1 provider run in progress (its buildRecipes), else the built-in ones */
    public static class_7225.class_7874 runningRegistries() {
        CbItemDatagenRecipes p = CURRENT.get();
        return p != null ? p.field_48981 : builtIn();
    }

    // ---- 1.21.1 RecipeProvider instance members, called naming the class (the receiver is a 1.21.1 provider: the facade) ----

    /** a 1.21.1 data provider standing for RecipeProvider (the RecipeProvider and VanillaRecipeProvider facades) */
    public interface LegacyProvider {
        java.util.concurrent.CompletableFuture<?> method_10319(net.minecraft.class_7403 output);

        String method_10321();

        java.util.concurrent.CompletableFuture<?> method_56888(net.minecraft.class_7403 output, class_7225.class_7874 registries);

        java.util.concurrent.CompletableFuture<?> method_46206(net.minecraft.class_7403 output, class_7225.class_7874 registries,
            class_8779 advancement);
    }

    private static LegacyProvider legacy(net.minecraft.class_2446 provider) {
        if (provider instanceof LegacyProvider legacy) {
            return legacy;
        }
        throw new IllegalStateException(provider.getClass().getName() + " is 1.21.2's RecipeProvider, not 1.21.1's data provider");
    }

    /** run(output) */
    public static java.util.concurrent.CompletableFuture<?> method_10319(net.minecraft.class_2446 provider, net.minecraft.class_7403 output) {
        return legacy(provider).method_10319(output);
    }

    /** getName() */
    public static String method_10321(net.minecraft.class_2446 provider) {
        return legacy(provider).method_10321();
    }

    /** run(output, registries) */
    public static java.util.concurrent.CompletableFuture<?> method_56888(net.minecraft.class_2446 provider, net.minecraft.class_7403 output,
            class_7225.class_7874 registries) {
        return legacy(provider).method_56888(output, registries);
    }

    /** buildAdvancement(output, registries, advancement) */
    public static java.util.concurrent.CompletableFuture<?> method_46206(net.minecraft.class_2446 provider, net.minecraft.class_7403 output,
            class_7225.class_7874 registries, class_8779 advancement) {
        return legacy(provider).method_46206(output, registries, advancement);
    }

    // ---- the helpers whose 1.21.2 form differs beyond the output (MANUAL in the generator) ----

    /** 1.21.1's RecipeProvider.method_48530(output, item, id): 1.21.2 names the recipe by key */
    public static void method_48530(class_8790 output, net.minecraft.class_1792 item, net.minecraft.class_2960 id) {
        of(output).method_48530(item, CbItemRecipeBook.key(id));
    }

    /**
     * 1.21.1's method_48532(output, result, baseMaterial): two of the result from a shaped recipe -- diamonds round the
     * result and a block of the base material tag ("#S#", "#C#", "###"), unlocked by having the result; 1.21.1's body
     * on 1.21.2's builder and the provider's criterion
     */
    public static void method_48532(class_8790 output, net.minecraft.class_1935 result, net.minecraft.class_6862<net.minecraft.class_1792> baseMaterial) {
        CbItemDatagenRecipes p = of(output);
        net.minecraft.class_2447.method_10436(p.field_48981.method_46762(net.minecraft.class_7924.field_41197), net.minecraft.class_7800.field_40642, result, 2)
            .method_10434('#', net.minecraft.class_1802.field_8477)
            .method_10433('C', baseMaterial)
            .method_10434('S', result)
            .method_10439("#S#").method_10439("#C#").method_10439("###")
            .method_10429(net.minecraft.class_2446.method_32807(result), p.method_10426(result))
            .method_10431(output);
    }

    /**
     * 1.21.1's method_36546(result, base): its private method_33531(BUILDING_BLOCKS, result, Ingredient.of(base)) -- six
     * of the result, shaped "###" / "###" of the base. 1.21.2's counterpart is private with the same body; here it is on
     * the provider's shaped builder.
     */
    public static net.minecraft.class_5797 method_36546(net.minecraft.class_1935 result, net.minecraft.class_1935 base) {
        return current().method_62747(net.minecraft.class_7800.field_40635, result, 6)
            .method_10428('#', net.minecraft.class_1856.method_8101(base))
            .method_10439("###").method_10439("###");
    }

    // ---- BEGIN GENERATED (gen_s7item_datagen_recipes.py) ----
    /** 1.21.1's RecipeProvider.method_46207(Lnet/minecraft/class_8790;Lnet/minecraft/class_7699;)V */
    public static void method_46207(net.minecraft.class_8790 arg0, net.minecraft.class_7699 arg1) {
        of(arg0).method_46207(arg1);
    }

    /** 1.21.1's RecipeProvider.method_36444(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;Ljava/lang/String;)V */
    public static void method_36444(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2, java.lang.String arg3) {
        of(arg0).method_36444(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_36445(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;Ljava/lang/String;I)V */
    public static void method_36445(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2, java.lang.String arg3, int arg4) {
        of(arg0).method_36445(arg1, arg2, arg3, arg4);
    }

    /** 1.21.1's RecipeProvider.method_36233(Lnet/minecraft/class_8790;Ljava/util/List;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;FILjava/lang/String;)V */
    public static void method_36233(net.minecraft.class_8790 arg0, java.util.List arg1, net.minecraft.class_7800 arg2, net.minecraft.class_1935 arg3, float arg4, int arg5, java.lang.String arg6) {
        of(arg0).method_36233(arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /** 1.21.1's RecipeProvider.method_36234(Lnet/minecraft/class_8790;Ljava/util/List;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;FILjava/lang/String;)V */
    public static void method_36234(net.minecraft.class_8790 arg0, java.util.List arg1, net.minecraft.class_7800 arg2, net.minecraft.class_1935 arg3, float arg4, int arg5, java.lang.String arg6) {
        of(arg0).method_36234(arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /** 1.21.1's RecipeProvider.method_29728(Lnet/minecraft/class_8790;Lnet/minecraft/class_1792;Lnet/minecraft/class_7800;Lnet/minecraft/class_1792;)V */
    public static void method_29728(net.minecraft.class_8790 arg0, net.minecraft.class_1792 arg1, net.minecraft.class_7800 arg2, net.minecraft.class_1792 arg3) {
        of(arg0).method_29728(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_46209(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_46209(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_46209(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_47521(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;Ljava/lang/String;)V */
    public static void method_47521(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3, java.lang.String arg4) {
        of(arg0).method_47521(arg1, arg2, arg3, arg4);
    }

    /** 1.21.1's RecipeProvider.method_47522(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_47522(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_47522(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_24475(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_6862;I)V */
    public static void method_24475(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_6862 arg2, int arg3) {
        of(arg0).method_24475(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_24477(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_6862;I)V */
    public static void method_24477(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_6862 arg2, int arg3) {
        of(arg0).method_24477(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_24476(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24476(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24476(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24478(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24478(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24478(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_42754(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_42754(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_42754(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_33544(Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)Lnet/minecraft/class_5797; */
    public static net.minecraft.class_5797 legacy_method_33544(net.minecraft.class_1935 arg0, net.minecraft.class_1856 arg1) {
        return current().method_33544(arg0, arg1);
    }

    /** 1.21.1's RecipeProvider.method_32813(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32813(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_32813(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_32814(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32814(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_32814(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_32804(Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)Lnet/minecraft/class_5797; */
    public static net.minecraft.class_5797 legacy_method_32804(net.minecraft.class_7800 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1856 arg2) {
        return current().method_32804(arg0, arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_32808(Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)Lnet/minecraft/class_5797; */
    public static net.minecraft.class_5797 legacy_method_32808(net.minecraft.class_1935 arg0, net.minecraft.class_1856 arg1) {
        return current().method_32808(arg0, arg1);
    }

    /** 1.21.1's RecipeProvider.method_33553(Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)Lnet/minecraft/class_5797; */
    public static net.minecraft.class_5797 legacy_method_33553(net.minecraft.class_1935 arg0, net.minecraft.class_1856 arg1) {
        return current().method_33553(arg0, arg1);
    }

    /** 1.21.1's RecipeProvider.method_46208(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_46208(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_46208(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_51890(Lnet/minecraft/class_8790;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V */
    public static void method_51890(net.minecraft.class_8790 arg0, java.util.List arg1, java.util.List arg2, java.lang.String arg3) {
        of(arg0).method_51890(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_24885(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24885(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24885(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24887(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24887(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24887(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24889(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24889(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24889(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24890(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24890(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24890(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24891(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24891(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24891(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24892(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24892(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24892(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24893(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24893(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24893(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_24894(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_24894(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_24894(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_32233(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32233(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_32233(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_32809(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32809(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_32809(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_32810(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32810(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_32810(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_32811(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32811(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_32811(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_32812(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_32812(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_32812(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_46212(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_46212(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_46212(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_32805(Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)Lnet/minecraft/class_2447; */
    public static net.minecraft.class_2447 legacy_method_32805(net.minecraft.class_7800 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1856 arg2) {
        return current().method_32805(arg0, arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_33717(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_33717(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3) {
        of(arg0).method_33717(arg1, arg2, arg3);
    }

    /** 1.21.1's RecipeProvider.method_33715(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;I)V */
    public static void method_33715(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_1935 arg3, int arg4) {
        of(arg0).method_33715(arg1, arg2, arg3, arg4);
    }

    /** 1.21.1's RecipeProvider.method_36325(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;)V */
    public static void method_36325(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_7800 arg3, net.minecraft.class_1935 arg4) {
        of(arg0).method_36325(arg1, arg2, arg3, arg4);
    }

    /** 1.21.1's RecipeProvider.method_36446(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Ljava/lang/String;Ljava/lang/String;)V */
    public static void method_36446(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_7800 arg3, net.minecraft.class_1935 arg4, java.lang.String arg5, java.lang.String arg6) {
        of(arg0).method_36446(arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /** 1.21.1's RecipeProvider.method_36449(Lnet/minecraft/class_8790;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Lnet/minecraft/class_7800;Lnet/minecraft/class_1935;Ljava/lang/String;Ljava/lang/String;)V */
    public static void method_36449(net.minecraft.class_8790 arg0, net.minecraft.class_7800 arg1, net.minecraft.class_1935 arg2, net.minecraft.class_7800 arg3, net.minecraft.class_1935 arg4, java.lang.String arg5, java.lang.String arg6) {
        of(arg0).method_36449(arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /** 1.21.1's RecipeProvider.method_48533(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1935;)V */
    public static void method_48533(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1935 arg2) {
        of(arg0).method_48533(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_60922(Lnet/minecraft/class_8790;Lnet/minecraft/class_1935;Lnet/minecraft/class_1856;)V */
    public static void method_60922(net.minecraft.class_8790 arg0, net.minecraft.class_1935 arg1, net.minecraft.class_1856 arg2) {
        of(arg0).method_60922(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_17585(Lnet/minecraft/class_8790;Ljava/lang/String;Lnet/minecraft/class_1865;Lnet/minecraft/class_1874$class_3958;I)V */
    public static void method_17585(net.minecraft.class_8790 arg0, java.lang.String arg1, net.minecraft.class_1865 arg2, net.minecraft.class_1874.class_3958 arg3, int arg4) {
        of(arg0).method_17585(arg1, arg2, arg3, arg4);
    }

    /** 1.21.1's RecipeProvider.method_34854(Lnet/minecraft/class_8790;Lnet/minecraft/class_7699;)V */
    public static void method_34854(net.minecraft.class_8790 arg0, net.minecraft.class_7699 arg1) {
        of(arg0).method_34854(arg1);
    }

    /** 1.21.1's RecipeProvider.method_54829(Lnet/minecraft/class_8790;Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;)V */
    public static void method_54829(net.minecraft.class_8790 arg0, net.minecraft.class_2248 arg1, net.minecraft.class_2248 arg2) {
        of(arg0).method_54829(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_54830(Lnet/minecraft/class_8790;Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;)V */
    public static void method_54830(net.minecraft.class_8790 arg0, net.minecraft.class_2248 arg1, net.minecraft.class_2248 arg2) {
        of(arg0).method_54830(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_33535(Lnet/minecraft/class_8790;Lnet/minecraft/class_5794;Lnet/minecraft/class_7699;)V */
    public static void method_33535(net.minecraft.class_8790 arg0, net.minecraft.class_5794 arg1, net.minecraft.class_7699 arg2) {
        of(arg0).method_33535(arg1, arg2);
    }

    /** 1.21.1's RecipeProvider.method_10426(Lnet/minecraft/class_1935;)Lnet/minecraft/class_175; */
    public static net.minecraft.class_175 legacy_method_10426(net.minecraft.class_1935 arg0) {
        return current().method_10426(arg0);
    }

    /** 1.21.1's RecipeProvider.method_10420(Lnet/minecraft/class_6862;)Lnet/minecraft/class_175; */
    public static net.minecraft.class_175 legacy_method_10420(net.minecraft.class_6862 arg0) {
        return current().method_10420(arg0);
    }
    // ---- END GENERATED ----
}
