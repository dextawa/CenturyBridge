package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Minecraft client API on 1.21.2 that moved: the frame timer getter and the profiler pie
 * chart's key handling.
 */
@Environment(EnvType.CLIENT)
public final class CbRestClient {

    private CbRestClient() {
    }

    /** Minecraft.getTimer(): 1.21.2's getDeltaTracker, the same tracker */
    public static net.minecraft.class_9779 timer(net.minecraft.class_310 mc) {
        return mc.method_61966();
    }

    /**
     * Minecraft.debugFpsMeterKeyPress(key): the profiler pie chart's key press (descend into entry key,
     * 0 = up), which 1.21.2 moved with the chart to the debug overlay -- 1.21.1's body, on the chart's
     * own results and tree path.
     */
    public static void debugFpsMeterKeyPress(net.minecraft.class_310 mc, int key) {
        mc.method_53526().method_61981().method_61987(key);
    }

    /**
     * CommonListenerCookie.strictErrorHandling(): 1.21.1's server always sent true and 1.21.2's client always
     * disconnects on a packet error (1.21.1's behaviour for true), so the flag is true.
     */
    public static boolean strictErrorHandling(net.minecraft.class_8675 cookie) {
        return true;
    }

    /** ClientCommonPacketListenerImpl.strictErrorHandling (a protected field of 1.21.1): true, as above */
    public static boolean strictErrorHandlingField(net.minecraft.class_8673 listener) {
        return true;
    }

    /**
     * sendSystemMessage(message) of a client-side entity, 1.21.1's bodies: LocalPlayer and RemotePlayer added
     * the message to the chat; any other entity (Entity's body) did nothing.
     */
    public static void playerSystemMessage(net.minecraft.class_1297 entity, net.minecraft.class_2561 message) {
        if (entity instanceof net.minecraft.class_746 || entity instanceof net.minecraft.class_745) {
            net.minecraft.class_310.method_1551().field_1705.method_1743().method_1812(message);
        }
    }

    /** super.sendSystemMessage(message) reaching LocalPlayer's 1.21.1 body: the message added to the chat */
    public static void localPlayerSuperSystemMessage(net.minecraft.class_746 player, net.minecraft.class_2561 message) {
        net.minecraft.class_310.method_1551().field_1705.method_1743().method_1812(message);
    }

    /** super.sendSystemMessage(message) reaching RemotePlayer's 1.21.1 body: the message added to the chat */
    public static void remotePlayerSuperSystemMessage(net.minecraft.class_745 player, net.minecraft.class_2561 message) {
        net.minecraft.class_310.method_1551().field_1705.method_1743().method_1812(message);
    }

    /**
     * ClientSuggestionProvider.getRecipeNames(): 1.21.1 answered the recipes the server had sent the client.
     * A 1.21.2 server sends the client no recipes (ClientboundUpdateRecipesPacket carries property sets and
     * stonecutter entries only); in singleplayer the integrated server's recipes are the ones 1.21.1's
     * server sent. Connected to another server the ids exist only there.
     */
    public static java.util.stream.Stream<net.minecraft.class_2960> recipeNames(net.minecraft.class_2172 provider) {
        net.minecraft.server.MinecraftServer server = net.minecraft.class_310.method_1551().method_1576();
        if (server == null) {
            throw new UnsupportedOperationException("CenturyBridge: SharedSuggestionProvider.getRecipeNames() on a"
                + " client connected to a remote 1.21.2+ server: the server no longer sends the client its recipes,"
                + " so the client has no recipe ids (suggest them through the server instead)");
        }
        return CbRestCommands.recipeIds(server);
    }

    /**
     * RegistryDataCollector.collectGameRegistries(resources, registries, memoryConnection): 1.21.2 takes the
     * fallback registries frozen; 1.21.1 used them as given and froze its answer (freezing a frozen access is
     * the access itself), so the given access frozen first is the same.
     */
    public static net.minecraft.class_5455.class_6890 collectGameRegistries(net.minecraft.class_9173 collector,
            net.minecraft.class_5912 resources, net.minecraft.class_5455 registries, boolean memoryConnection) {
        return collector.method_56585(resources, registries.method_40316(), memoryConnection);
    }
}
