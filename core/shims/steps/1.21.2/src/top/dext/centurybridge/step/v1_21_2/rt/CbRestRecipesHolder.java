package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import net.minecraft.class_8786;

/**
 * Side-neutral state of the 1.21.1 recipe synchronisation ({@link CbRestRecipeSync}): the server last seen
 * sending recipes, and the recipes this client last received (set by the client side, read by side-neutral
 * code such as ClientboundUpdateRecipesPacket.getRecipes()).
 */
public final class CbRestRecipesHolder {

    private CbRestRecipesHolder() {
    }

    static volatile net.minecraft.server.MinecraftServer server;

    private static volatile List<class_8786<?>> received = List.of();

    /** the server running in this process, or null */
    public static net.minecraft.server.MinecraftServer server() {
        return server;
    }

    /** the recipes this client last received from the server (empty on a server or before any arrived) */
    public static List<class_8786<?>> receivedRecipes() {
        return received;
    }

    static void setReceived(List<class_8786<?>> recipes) {
        received = List.copyOf(recipes);
    }
}
