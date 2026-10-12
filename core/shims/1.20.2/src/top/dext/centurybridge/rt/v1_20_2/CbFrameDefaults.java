package top.dext.centurybridge.rt.v1_20_2;

/**
 * frameDefaults of this layer's carrying override hooks (common side): what a 1.20.1 override's
 * rewritten super call passes for an argument the old shape has no place for when the override was
 * entered directly -- the mod's own call, so no frame holds what vanilla passed. Each answers what
 * 1.20.1's body used where 1.20.2 takes an argument.
 */
public final class CbFrameDefaults {

    /**
     * CommandSourceStack.withSigningContext(context) -> withSigningContext(context, chainer): 1.20.1's
     * body built the new source with the receiver's own chatMessageChainer (field_39834), which 1.20.2
     * answers through getChatMessageChainer()
     */
    public static net.minecraft.class_7620 chatMessageChainer(net.minecraft.class_2168 self) {
        return self.method_44909();
    }

    /**
     * PlayerList.placeNewPlayer(connection, player) -> (.., cookie): the client data of a player joining
     * with defaults and its own profile -- what the call side passes too (CbServer.method_14570); 1.20.1's
     * play listener started at latency 0 with default client options until the client sent its own
     */
    public static net.minecraft.class_8792 listenerCookie(net.minecraft.class_3324 self, net.minecraft.class_2535 connection,
            net.minecraft.class_3222 player) {
        return net.minecraft.class_8792.method_53824(player.method_7334());
    }

    /**
     * PlayerList.getPlayerForLogin(profile) -> (profile, clientInformation): default client options, as a
     * 1.20.1 ServerPlayer was constructed with (CbServer.method_14613 passes the same)
     */
    public static net.minecraft.class_8791 clientInformation() {
        return net.minecraft.class_8791.method_53821();
    }

    /**
     * Slot.setByPlayer(stack) -> setByPlayer(stack, previous): 1.20.1's armor slots took the stack being
     * replaced from getItem() before setting it; 1.20.2's callers pass that same getItem(). Evaluated before
     * the super call, while the slot still holds it.
     */
    public static net.minecraft.class_1799 slotItem(net.minecraft.class_1735 self) {
        return self.method_7677();
    }

    private CbFrameDefaults() {
    }
}
