package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.facade.SearchRegistryKey;

/**
 * 1.20.6's SearchRegistry on 1.21's SessionSearchTrees. 1.20.6's registry was one object of the game's
 * (Minecraft.getSearchTree / populateSearchTree): trees registered under keys with the supplier building them from
 * their contents, populated when the contents were known, refreshed on resource reload. 1.21 keeps three fixed trees
 * per connection (creative names, creative tags, recipes). Here every SessionSearchTrees answers 1.20.6's register /
 * getTree / populate / onResourceManagerReload (CbClientSearchTreesMixin): a key registered there is 1.20.6's entry;
 * CREATIVE_NAMES / CREATIVE_TAGS / RECIPE_COLLECTIONS are 1.21's trees of that object, populated with 1.20.6's
 * suppliers. The game's own registry is a SessionSearchTrees of CenturyBridge's whose three trees are the current
 * connection's (its own before one is open), so what 1.20.6 code registers on it outlives connections, as it did.
 */
@Environment(EnvType.CLIENT)
public final class CbClientSearch {

    private CbClientSearch() {
    }

    // ------------------------------------------------------------------ SearchRegistry's three keys

    /** CREATIVE_NAMES */
    public static final SearchRegistryKey<net.minecraft.class_1799> field_5495 = new SearchRegistryKey<>();

    /** CREATIVE_TAGS */
    public static final SearchRegistryKey<net.minecraft.class_1799> field_5494 = new SearchRegistryKey<>();

    /** RECIPE_COLLECTIONS */
    public static final SearchRegistryKey<net.minecraft.class_516> field_46200 = new SearchRegistryKey<>();

    // ------------------------------------------------------------------ 1.20.6's suppliers for them (Minecraft.createSearchTrees)

    /** a tooltip as 1.20.6's search read it: each line's text without formatting, trimmed, empty ones dropped */
    private static Stream<String> cbText(List<net.minecraft.class_2561> lines) {
        return lines.stream().map(line -> net.minecraft.class_124.method_539(line.getString()).trim()).filter(s -> !s.isEmpty());
    }

    /** CREATIVE_NAMES: each stack's creative tooltip, and its item's id */
    public static net.minecraft.class_1129<net.minecraft.class_1799> cbCreativeNames(List<net.minecraft.class_1799> stacks) {
        return new net.minecraft.class_1126<>(
            stack -> cbText(stack.method_7950(net.minecraft.class_1792.class_9635.field_51353, null,
                net.minecraft.class_1836.field_41070.method_47371())),
            stack -> Stream.of(net.minecraft.class_7923.field_41178.method_10221(stack.method_7909())),
            stacks);
    }

    /** CREATIVE_TAGS: each stack's tags */
    public static net.minecraft.class_1129<net.minecraft.class_1799> cbCreativeTags(List<net.minecraft.class_1799> stacks) {
        return new net.minecraft.class_1121<>(stack -> stack.method_40133().map(net.minecraft.class_6862::comp_327), stacks);
    }

    /** RECIPE_COLLECTIONS: each collection's results' tooltips, and their items' ids */
    public static net.minecraft.class_1129<net.minecraft.class_516> cbRecipeCollections(List<net.minecraft.class_516> collections) {
        return new net.minecraft.class_1126<>(
            collection -> {
                net.minecraft.class_1792.class_9635 context = net.minecraft.class_1792.class_9635.method_59530(collection.method_48479());
                return collection.method_2650().stream()
                    .flatMap(recipe -> cbText(recipe.comp_1933().method_8110(collection.method_48479())
                        .method_7950(context, null, net.minecraft.class_1836.field_41070)));
            },
            collection -> collection.method_2650().stream()
                .map(recipe -> net.minecraft.class_7923.field_41178.method_10221(recipe.comp_1933().method_8110(collection.method_48479())
                    .method_7909())),
            collections);
    }

    // ------------------------------------------------------------------ the game's registry: Minecraft.getSearchTree / populateSearchTree

    private static net.minecraft.class_1124 gameRegistry;

    /** the game's 1.20.6 registry (CenturyBridge's SessionSearchTrees; its three trees are the connection's) */
    public static synchronized net.minecraft.class_1124 cbGameRegistry() {
        if (gameRegistry == null) {
            gameRegistry = new net.minecraft.class_1124();
        }
        return gameRegistry;
    }

    /** the SessionSearchTrees whose fixed trees the game's registry answers: the connection's, else its own */
    public static net.minecraft.class_1124 cbFixedTreesOf(net.minecraft.class_1124 registry) {
        if (registry == gameRegistry) {
            net.minecraft.class_634 connection = net.minecraft.class_310.method_1551().method_1562();
            if (connection != null) {
                return connection.method_60347();
            }
        }
        return registry;
    }

    /** Minecraft.getSearchTree(key), as a 1.20.6 Minecraft subclass declares it */
    public interface CbLegacyGetSearchTree {
        <T> net.minecraft.class_1129<T> method_1484(SearchRegistryKey<T> key);
    }

    /** Minecraft.populateSearchTree(key, contents), as a 1.20.6 Minecraft subclass declares it */
    public interface CbLegacyPopulateSearchTree {
        <T> void method_43759(SearchRegistryKey<T> key, List<T> contents);
    }

    /** minecraft.getSearchTree(key), called 1.20.6's way */
    public static <T> net.minecraft.class_1129<T> cbGetSearchTree(net.minecraft.class_310 minecraft, SearchRegistryKey<T> key) {
        if (minecraft instanceof CbLegacyGetSearchTree own) {
            return own.method_1484(key);
        }
        return cbGetSearchTreeBody(minecraft, key);
    }

    /** getSearchTree(key) of 1.20.6: the game's registry's tree */
    public static <T> net.minecraft.class_1129<T> cbGetSearchTreeBody(net.minecraft.class_310 minecraft, SearchRegistryKey<T> key) {
        return ((CbClientSearchRegistry) (Object) cbGameRegistry()).method_4800(key);
    }

    /** minecraft.populateSearchTree(key, contents), called 1.20.6's way */
    public static <T> void cbPopulateSearchTree(net.minecraft.class_310 minecraft, SearchRegistryKey<T> key, List<T> contents) {
        if (minecraft instanceof CbLegacyPopulateSearchTree own) {
            own.method_43759(key, contents);
            return;
        }
        cbPopulateSearchTreeBody(minecraft, key, contents);
    }

    /** populateSearchTree(key, contents) of 1.20.6: the game's registry populates it */
    public static <T> void cbPopulateSearchTreeBody(net.minecraft.class_310 minecraft, SearchRegistryKey<T> key, List<T> contents) {
        ((CbClientSearchRegistry) (Object) cbGameRegistry()).method_43804(key, contents);
    }

    /** 1.20.6's SearchRegistry instance methods on a 1.21 SessionSearchTrees (CbClientSearchTreesMixin) */
    public interface CbClientSearchRegistry {
        <T> void method_4801(SearchRegistryKey<T> key, top.dext.centurybridge.step.v1_21.facade.SearchRegistryTreeBuilderSupplier<T> supplier);

        <T> net.minecraft.class_1129<T> method_4800(SearchRegistryKey<T> key);

        <T> void method_43804(SearchRegistryKey<T> key, List<T> contents);

        void method_14491(net.minecraft.class_3300 resources);
    }

    // ------------------------------------------------------------------ Minecraft.renderOnThread

    /** Minecraft.renderOnThread(), as a 1.20.6 Minecraft subclass declares it */
    public interface CbLegacyRenderOnThread {
        boolean method_22107();
    }

    /** minecraft.renderOnThread(), called 1.20.6's way */
    public static boolean cbRenderOnThread(net.minecraft.class_310 minecraft) {
        if (minecraft instanceof CbLegacyRenderOnThread own) {
            return own.method_22107();
        }
        return cbRenderOnThreadBody(minecraft);
    }

    /** renderOnThread() of 1.20.6: false (rendering never ran on a thread of its own) */
    public static boolean cbRenderOnThreadBody(net.minecraft.class_310 minecraft) {
        return false;
    }
}
