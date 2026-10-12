package top.dext.centurybridge.step.v1_21.facade;

import java.util.Map;
import top.dext.centurybridge.step.v1_21.rt.CbEnchVanilla;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantmentHelper;

/**
 * 1.20.6's ThornsEnchantment (net/minecraft/class_1906): when the wearer is hurt, 15% chance per level to
 * hurt the attacker (1-4, or level - 10 above level 10) and wear a random thorns item by 2, with 1.20.6's
 * body.
 */
public class class_1906 extends class_1887 {

    /** 1.20.6 ThornsEnchantment.CHANCE_PER_LEVEL */
    public static final float field_30950 = 0.15F;

    public class_1906(class_1887$class_9427 definition) {
        super(definition);
    }

    /** doPostHurt(LivingEntity user, Entity attacker, int level) */
    @Override
    public void method_8178(net.minecraft.class_1309 user, net.minecraft.class_1297 attacker, int level) {
        net.minecraft.class_5819 random = user.method_59922();
        Map.Entry<net.minecraft.class_1304, net.minecraft.class_1799> item = CbEnchantmentHelper.method_8204(CbEnchVanilla.field_9097(), user);
        if (method_8243(level, random)) {
            if (attacker != null) {
                attacker.method_5643(user.method_48923().method_48818(user), (float) method_8242(level, random));
            }
            if (item != null) {
                item.getValue().method_7970(2, user, item.getKey());
            }
        }
    }

    /** shouldHit(int, RandomSource) */
    public static boolean method_8243(int level, net.minecraft.class_5819 random) {
        if (level <= 0) {
            return false;
        }
        return random.method_43057() < 0.15F * (float) level;
    }

    /** getDamage(int, RandomSource) */
    public static int method_8242(int level, net.minecraft.class_5819 random) {
        return level > 10 ? level - 10 : 1 + random.method_43048(4);
    }
}
