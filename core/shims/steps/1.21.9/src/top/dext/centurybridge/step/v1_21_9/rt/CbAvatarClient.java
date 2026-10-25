package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11902;
import net.minecraft.class_243;
import net.minecraft.class_638;
import net.minecraft.class_742;

/**
 * AbstractClientPlayer members 1.21.9 moved into the player's ClientAvatarState (javap: AbstractClientPlayer.tick hands
 * the state the position and motion, which keeps the previous tick's motion and the walk distance pair the 1.21.8
 * player kept itself).
 */
@Environment(EnvType.CLIENT)
public final class CbAvatarClient {
    private CbAvatarClient() {
    }

    private static class_11902 state(class_742 p) {
        return p.method_74192();
    }

    /** AbstractClientPlayer.clientLevel: the player's level (a ClientLevel for a client's player) */
    public static class_638 field_17892(class_742 p) {
        return (class_638) p.method_73183();
    }

    /** AbstractClientPlayer.deltaMovementOnPreviousTick */
    public static class_243 field_42906(class_742 p) {
        return state(p).field_62568;
    }

    public static void field_42906(class_742 p, class_243 v) {
        state(p).field_62568 = v;
    }

    /** AbstractClientPlayer.walkDistO / walkDist (1.21.8's pair, the state's on 1.21.9) */
    public static float field_53038(class_742 p) {
        return state(p).field_62578;
    }

    public static void field_53038(class_742 p, float v) {
        state(p).field_62578 = v;
    }

    public static float field_53039(class_742 p) {
        return state(p).field_62577;
    }

    public static void field_53039(class_742 p, float v) {
        state(p).field_62577 = v;
    }

    /** AbstractClientPlayer.getDeltaMovementLerped(partialTick): 1.21.8's body over the state's previous motion */
    public static class_243 method_49339(class_742 p, float partialTick) {
        return state(p).field_62568.method_35590(p.method_18798(), partialTick);
    }
}
