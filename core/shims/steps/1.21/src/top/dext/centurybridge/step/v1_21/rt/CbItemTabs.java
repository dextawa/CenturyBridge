package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * CreativeModeTab's 1.20.6 surface on 1.21.
 *
 * <p>Background texture: 1.20.6 named it by a suffix ("items.png") the client prefixed with
 * textures/gui/container/creative_inventory/tab_; 1.21 names the texture's whole location
 * (CreativeModeTab.createTextureLocation does the same prefixing). A location outside that directory has
 * no 1.20.6 suffix; its full name is answered.
 *
 * <p>Search tree: 1.20.6's tab held a search-tree builder (setSearchTreeBuilder) that rebuildSearchTree
 * handed the tab's search items; 1.20.6's client set the search tab's to repopulate its creative name and
 * tag search trees. 1.21 keeps those trees in the connection's SessionSearchTrees and dropped both
 * methods. A builder a 1.20.6 mod sets is kept here per tab; the search tab's default, on a client, is the
 * 1.21 equivalent of 1.20.6's (CbItemTabsClient); a dedicated server has none, as in 1.20.6.
 */
public final class CbItemTabs {

    private CbItemTabs() {
    }

    private static final String TAB_DIR = "textures/gui/container/creative_inventory/tab_";

    /** Builder.backgroundSuffix(suffix) of 1.20.6 as 1.21's backgroundTexture(location) (callReshapes conv) */
    public static net.minecraft.class_2960 textureOf(String suffix) {
        return net.minecraft.class_2960.method_60656(TAB_DIR + suffix);
    }

    /** CreativeModeTab.getBackgroundSuffix() of 1.20.6 from 1.21's texture location (callReshapes ret; hook wrap) */
    public static String suffixOf(net.minecraft.class_2960 texture) {
        if (texture.method_12836().equals("minecraft") && texture.method_12832().startsWith(TAB_DIR)) {
            return texture.method_12832().substring(TAB_DIR.length());
        }
        return texture.toString();
    }

    private static final Map<net.minecraft.class_1761, Consumer<List<net.minecraft.class_1799>>> BUILDERS =
        new com.google.common.collect.MapMaker().weakKeys().makeMap();

    /** CreativeModeTab.setSearchTreeBuilder(builder) (instanceRedirect) */
    public static void method_47308(net.minecraft.class_1761 tab, Consumer<List<net.minecraft.class_1799>> builder) {
        if (builder == null) {
            BUILDERS.remove(tab);
        } else {
            BUILDERS.put(tab, builder);
        }
    }

    /** CreativeModeTab.rebuildSearchTree() (instanceRedirect): the tab's builder, handed a copy of its search items */
    public static void method_47314(net.minecraft.class_1761 tab) {
        Consumer<List<net.minecraft.class_1799>> builder = BUILDERS.get(tab);
        if (builder == null && tab == net.minecraft.class_7706.method_47344()
                && net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            builder = CbItemTabsClient.searchTreeBuilder();
        }
        if (builder != null) {
            builder.accept(com.google.common.collect.Lists.newArrayList(tab.method_47313()));
        }
    }
}
