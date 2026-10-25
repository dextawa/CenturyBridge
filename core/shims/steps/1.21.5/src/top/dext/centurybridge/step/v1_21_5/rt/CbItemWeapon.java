package top.dext.centurybridge.step.v1_21_5.rt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import top.dext.centurybridge.step.v1_21_5.facade.DiggerItem;
import top.dext.centurybridge.step.v1_21_5.facade.SwordItem;

/**
 * 1.21.4's Item.hurtEnemy(stack, target, attacker) answered whether the hit counted as a use of the item as a weapon:
 * true made ItemStack.hurtEnemy award the item-used stat and let the attack go on to postHurtEnemy. 1.21.5 made
 * hurtEnemy void and takes that answer from the stack's WEAPON component (ItemStack.hurtEnemy), and its vanilla
 * weapons carry one where their 1.21.4 classes answered true (SwordItem, DiggerItem, MaceItem, TridentItem).
 * <p>
 * The 1.21.4 member is restored on Item (CbItemHurtEnemyRestoredMixin): 1.21.4's contract, answering what 1.21.5's
 * path decides -- the 1.21.5 hurtEnemy, then whether the stack has WEAPON. A class that answers otherwise -- a
 * converted 1.21.4 class overriding it, or the SwordItem / DiggerItem facades, which answer true as 1.21.4's bodies
 * did without carrying WEAPON (CbItemTools) -- is asked through it by ItemStack.hurtEnemy
 * (CbItemStackHurtEnemyMixin), so its answer decides the stat and postHurtEnemy as on 1.21.4.
 */
public final class CbItemWeapon {

    private CbItemWeapon() {
    }

    /** 1.21.4's shape of Item.hurtEnemy, restored on Item */
    public interface ItemHurtEnemy {
        boolean method_7873(class_1799 stack, class_1309 target, class_1309 attacker);
    }

    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            for (Class<?> k = c; k != null && k != class_1792.class; k = k.getSuperclass()) {
                for (Method m : k.getDeclaredMethods()) {
                    if (m.getName().equals("method_7873") && m.getReturnType() == boolean.class && !Modifier.isStatic(m.getModifiers())
                            && m.getParameterCount() == 3 && m.getParameterTypes()[0] == class_1799.class
                            && m.getParameterTypes()[1] == class_1309.class && m.getParameterTypes()[2] == class_1309.class) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /** whether the item answers 1.21.4's hurtEnemy otherwise than the restored default (1.21.5's path) */
    public static boolean legacy(class_1792 item) {
        return item instanceof SwordItem || item instanceof DiggerItem || OVERRIDES.get(item.getClass());
    }

    /** the restored default: true for the facades (1.21.4's SwordItem / DiggerItem bodies) */
    public static boolean facadeAnswer(class_1792 item) {
        return item instanceof SwordItem || item instanceof DiggerItem;
    }
}
