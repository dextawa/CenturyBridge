package top.dext.centurybridge.step.v26_2.rt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/**
 * 26.1.2's Mob.setBodyArmorItem(stack) on 26.2 (javap of both). 26.1.2: {@code setItemSlotAndDropWhenKilled(BODY,
 * stack)}, called by AbstractHorse.equipBodyArmor and Wolf.mobInteract when a player puts body armor on the animal.
 * 26.2 dropped the method and makes the same setItemSlotAndDropWhenKilled(BODY, stack) call at both places itself.
 *
 * <p>The member is restored on Mob with 26.1.2's body (mixin/CbS26BodyArmorRestoreMixin): a 26.1.2 call or super call
 * links to it, a 26.1.2 class's override overrides it. At 26.2's two calls (mixin/CbS26BodyArmorRouteMixin) the
 * restored member is asked instead, as 26.1.2 asked it there, only for an animal whose class overrides it below 26.2's
 * and the layer's classes, or when a converted mod mixed it ({@link CbMixedMembers}); every other animal takes 26.2's
 * call as it is.
 */
public final class CbS26BodyArmor {
    private CbS26BodyArmor() {}

    /** the restored member's key in the conversion-time "mixed" signal */
    public static final String MEMBER = "net/minecraft/world/entity/Mob.setBodyArmorItem(Lnet/minecraft/world/item/ItemStack;)V";

    /** 26.1.2's Mob.setBodyArmorItem, restored on Mob */
    public interface Legacy {
        void setBodyArmorItem(ItemStack stack);

        /** 26.2's own call at the routed sites, setItemSlotAndDropWhenKilled(slot, stack), made from Mob */
        void cbS26$setItemSlotAndDropWhenKilled(net.minecraft.world.entity.EquipmentSlot slot, ItemStack stack);
    }

    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            for (Class<?> k = c; k != null && !k.getName().startsWith("net.minecraft.")
                    && !k.getName().startsWith("top.dext.centurybridge."); k = k.getSuperclass()) {
                for (Method m : k.getDeclaredMethods()) {
                    if (m.getName().equals("setBodyArmorItem") && m.getReturnType() == void.class
                            && !Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 1
                            && m.getParameterTypes()[0] == ItemStack.class) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /**
     * mixin/CbS26BodyArmorRouteMixin's @Redirect of 26.2's setItemSlotAndDropWhenKilled(BODY, stack) in
     * AbstractHorse.equipBodyArmor / Wolf.mobInteract: the restored member when routed, else 26.2's call unchanged
     */
    public static void bodyArmorCall(Mob mob, net.minecraft.world.entity.EquipmentSlot slot, ItemStack stack) {
        if (route(mob)) {
            ((Legacy) mob).setBodyArmorItem(stack);
        } else {
            ((Legacy) mob).cbS26$setItemSlotAndDropWhenKilled(slot, stack);
        }
    }

    /** whether 26.2's body-armor call on this mob asks the restored member (see the class comment) */
    public static boolean route(Mob mob) {
        return OVERRIDES.get(mob.getClass()) || CbMixedMembers.mixed(MEMBER);
    }
}
