package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's EnergySwirlLayer for a 1.21.1 subclass: 1.21.1's layer drew the swirl when its entity (a PowerableMob)
 * isPowered(); 1.21.2's asks the subclass (abstract isPowered(state)), which a 1.21.1 subclass does not declare. It is
 * synthesized (adapterHooks) to answer as 1.21.1's render decided: the entity the state was extracted from, if it is
 * a PowerableMob (Creeper and WitherBoss implement it again, CbEntityPowerableMixin), and powered.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSwirl {

    /** a 1.21.1 EnergySwirlLayer subclass: its xOffset(tick) */
    public interface Legacy {
        float method_23202(float tick);
    }

    private CbRenderSwirl() {
    }

    /** the synthesized isPowered(state) of a 1.21.1 EnergySwirlLayer subclass */
    public static boolean powered(Legacy layer, net.minecraft.class_10017 state) {
        return CbRenderStateEntity.entityOf(state) instanceof top.dext.centurybridge.step.v1_21_2.facade.PowerableMob mob && mob.method_6872();
    }
}
