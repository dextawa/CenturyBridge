package top.dext.centurybridge.step.v1_21_5.rt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import net.minecraft.class_9334;

/**
 * 1.21.4's Item.getBreakingSound() (and ItemStack.getBreakingSound(), which asked the item) is the BREAK_SOUND
 * component since 1.21.5: every item's default components carry SoundEvents.ITEM_BREAK (1.21.4 Item's answer),
 * wolf armor its own (1.21.4 AnimalArmorItem's override). LivingEntity.breakItem plays it.
 * <p>
 * Both members are restored (CbItemMembersRestoredMixin, CbItemStackMembersMixin), answering what 1.21.5 plays for
 * the item: its BREAK_SOUND (ITEM_BREAK when it has none -- 1.21.4 always had one). An item whose class answers
 * otherwise -- a converted 1.21.4 class overriding getBreakingSound -- is asked by LivingEntity.breakItem
 * (CbItemBreakSoundMixin), so its override decides the sound as on 1.21.4.
 */
public final class CbItemSounds {

    private CbItemSounds() {
    }

    /** 1.21.4's shape of Item.getBreakingSound, restored on Item */
    public interface ItemBreakingSound {
        class_3414 method_57336();
    }

    /** the restored Item.getBreakingSound(): the item's BREAK_SOUND, or ITEM_BREAK (1.21.4 Item's) without one */
    public static class_3414 defaultBreakingSound(class_1792 item) {
        class_6880<class_3414> sound = item.method_57347().method_58694(class_9334.field_56399);
        return sound != null ? sound.comp_349() : class_3417.field_15075.comp_349();
    }

    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            for (Class<?> k = c; k != null && k != class_1792.class; k = k.getSuperclass()) {
                for (Method m : k.getDeclaredMethods()) {
                    if (m.getName().equals("method_57336") && m.getParameterCount() == 0 && m.getReturnType() == class_3414.class
                            && !Modifier.isStatic(m.getModifiers())) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /** whether the stack's item answers getBreakingSound otherwise than the restored default (a 1.21.4 override) */
    public static boolean legacy(class_1799 stack) {
        return OVERRIDES.get(stack.method_7909().getClass());
    }

    /** the sound a 1.21.4 override answers */
    public static class_3414 legacySound(class_1799 stack) {
        return ((ItemBreakingSound) stack.method_7909()).method_57336();
    }
}
