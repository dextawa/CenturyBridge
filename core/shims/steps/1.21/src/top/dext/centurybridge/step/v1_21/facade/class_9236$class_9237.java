package top.dext.centurybridge.step.v1_21.facade;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1927;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3610;
import net.minecraft.class_5362;

/**
 * 1.20.6's AbstractWindCharge.WindChargeDamageCalculator, which 1.21 replaced with a SimpleExplosionDamageCalculator
 * field: an explosion calculator that damages no entity and lets no block tagged blocks_wind_charge_explosions
 * break (resistance 3600000). The step's classRenames put a 1.20.6 mod's references to the class here; it is a
 * 1.21 ExplosionDamageCalculator, so 1.21's explosions take it as one.
 */
public class class_9236$class_9237 extends class_5362 {

    public class_9236$class_9237() {
        super();
    }

    @Override
    public boolean method_55504(class_1927 explosion, class_1297 entity) {
        return false;
    }

    @Override
    public Optional<Float> method_29555(class_1927 explosion, class_1922 level, class_2338 pos, class_2680 state, class_3610 fluid) {
        return state.method_26164(class_3481.field_49051) ? Optional.of(3600000.0F) : Optional.empty();
    }
}
