package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

/**
 * Soul speed on the client (CbEntitySoulSpeed): 1.20.6's LocalPlayer.canSpawnSoulSpeedParticle(), for a 1.20.6
 * super call landing on it. Client only.
 */
@Environment(EnvType.CLIENT)
public final class CbEntitySoulSpeedClient {

    /** LocalPlayer.canSpawnSoulSpeedParticle(): not while flying, else LivingEntity's */
    public static boolean superCanSpawnSoulSpeedParticle(class_746 player) {
        return !player.method_31549().field_7479 && CbEntitySoulSpeed.superCanSpawnSoulSpeedParticle(player);
    }

    private CbEntitySoulSpeedClient() {
    }
}
