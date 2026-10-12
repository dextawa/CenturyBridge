package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.function.Consumer;

/**
 * Client side of CbItemTabs: the search tab's default search-tree builder, 1.21's equivalent of the one
 * 1.20.6's Minecraft set (it repopulated the creative name and tag search trees with the tab's items): the
 * connection's SessionSearchTrees updated with them, as 1.21's creative screen does when the tabs rebuild.
 * Without a connection there are no search trees to update.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbItemTabsClient {

    private CbItemTabsClient() {
    }

    static Consumer<List<net.minecraft.class_1799>> searchTreeBuilder() {
        return items -> {
            net.minecraft.class_634 connection = net.minecraft.class_310.method_1551().method_1562();
            if (connection != null) {
                connection.method_60347().method_60357(connection.method_29091(), items);
                connection.method_60347().method_60355(items);
            }
        };
    }
}
