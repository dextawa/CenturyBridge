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

    // ------------------------------------------------------------------ colours

    /** packed RGB as 1.21.1's component vector (0..1 each) */
    public static net.minecraft.class_243 rgb(int color) {
        return new net.minecraft.class_243((color >> 16 & 255) / 255.0, (color >> 8 & 255) / 255.0, (color & 255) / 255.0);
    }

    /** a 1.21.1 component vector as the opaque packed colour 1.21.2 answers (each component rounded to 8 bits) */
    public static int rgbInt(net.minecraft.class_243 color) {
        return 0xFF000000 | channel(color.field_1352) << 16 | channel(color.field_1351) << 8 | channel(color.field_1350);
    }

    private static int channel(double c) {
        return Math.max(0, Math.min(255, (int) Math.round(c * 255.0)));
    }

    // ------------------------------------------------------------------ ClientInput (class_744) key flags

    /*
     * 1.21.1's Input kept the movement keys as six public booleans (up, down, left, right, jumping,
     * shiftKeyDown), written by KeyboardInput each tick and read by the player; 1.21.2 keeps them in the
     * keyPresses record (forward, backward, left, right, jump, shift, sprint). A read answers the record's
     * component; a write replaces the record with that component changed, which is what 1.21.2's player reads.
     */

    public static boolean inputUp(net.minecraft.class_744 input) {
        return input.field_54155.comp_3159();
    }

    public static boolean inputDown(net.minecraft.class_744 input) {
        return input.field_54155.comp_3160();
    }

    public static boolean inputLeft(net.minecraft.class_744 input) {
        return input.field_54155.comp_3161();
    }

    public static boolean inputRight(net.minecraft.class_744 input) {
        return input.field_54155.comp_3162();
    }

    public static boolean inputJumping(net.minecraft.class_744 input) {
        return input.field_54155.comp_3163();
    }

    public static boolean inputShiftKeyDown(net.minecraft.class_744 input) {
        return input.field_54155.comp_3164();
    }

    public static void inputUp(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(v, k.comp_3160(), k.comp_3161(), k.comp_3162(), k.comp_3163(), k.comp_3164(), k.comp_3165());
    }

    public static void inputDown(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(k.comp_3159(), v, k.comp_3161(), k.comp_3162(), k.comp_3163(), k.comp_3164(), k.comp_3165());
    }

    public static void inputLeft(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(k.comp_3159(), k.comp_3160(), v, k.comp_3162(), k.comp_3163(), k.comp_3164(), k.comp_3165());
    }

    public static void inputRight(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(k.comp_3159(), k.comp_3160(), k.comp_3161(), v, k.comp_3163(), k.comp_3164(), k.comp_3165());
    }

    public static void inputJumping(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(k.comp_3159(), k.comp_3160(), k.comp_3161(), k.comp_3162(), v, k.comp_3164(), k.comp_3165());
    }

    public static void inputShiftKeyDown(net.minecraft.class_744 input, boolean v) {
        net.minecraft.class_10185 k = input.field_54155;
        input.field_54155 = new net.minecraft.class_10185(k.comp_3159(), k.comp_3160(), k.comp_3161(), k.comp_3162(), k.comp_3163(), v, k.comp_3165());
    }

    // ------------------------------------------------------------------ players and faces

    /**
     * AbstractClientPlayer.getFieldOfViewModifier(): 1.21.1 read whether the camera is first person from the
     * options and left the FOV-effect scale to the game renderer; 1.21.2 takes both. The options' camera
     * type, and a scale of 1 (no scaling inside the modifier, as 1.21.1 computed it).
     */
    public static float fieldOfViewModifier(net.minecraft.class_742 player) {
        return player.method_3118(net.minecraft.class_310.method_1551().field_1690.method_31044().method_31034(), 1.0F);
    }

    /** frameDefaults of getFieldOfViewModifier(firstPerson, scale): whether the options' camera is first person */
    public static boolean firstPersonCamera() {
        return net.minecraft.class_310.method_1551().field_1690.method_31044().method_31034();
    }

    /** frameDefaults of getFieldOfViewModifier(firstPerson, scale): 1, no scaling inside the modifier (as 1.21.1) */
    public static float unscaledFovEffect() {
        return 1.0F;
    }

    /** PlayerFaceRenderer.draw(graphics, skin, x, y, size): 1.21.1's body, the hat layer drawn, upright, untinted */
    public static void method_44443(net.minecraft.class_332 graphics, net.minecraft.class_2960 skin, int x, int y, int size) {
        net.minecraft.class_7532.method_44445(graphics, skin, x, y, size, true, false, -1);
    }

    /** PlayerFaceRenderer.draw(graphics, skin, x, y, size, hat, upsideDown): untinted (1.21.2 added the tint) */
    public static void method_44445(net.minecraft.class_332 graphics, net.minecraft.class_2960 skin, int x, int y, int size,
            boolean hat, boolean upsideDown) {
        net.minecraft.class_7532.method_44445(graphics, skin, x, y, size, hat, upsideDown, -1);
    }
}
