package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1682;
import net.minecraft.class_1799;
import net.minecraft.class_3857;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityProjectileInvoker;

/**
 * 1.21.1's projectile constructors on 1.21.2.
 *
 * <p>ThrowableItemProjectile and its vanilla subclasses (Snowball, ThrownEgg, ThrownExperienceBottle,
 * ThrownEnderpearl, ThrownPotion) take the thrown stack in 1.21.2's constructors and setItem it. 1.21.1's took
 * none: the ITEM data kept defineSynchedData's value, new ItemStack(getDefaultItem()), until the caller's own
 * setItem. The reshaped call passes EMPTY ({@link #noItem}) and {@link #defaultItem} puts that value back right
 * after the constructor (getDefaultItem is the object's own, so a mod subclass answers as in 1.21.1).
 *
 * <p>ThrowableProjectile(type, owner, level) is gone: its 1.21.1 body was this(type, owner.getX(),
 * owner.getEyeY() - 0.1F, owner.getZ(), level) then setOwner(owner), which the reshape makes of the
 * (type, x, y, z, level) constructor that remains.
 *
 * <p>FishingHook(player, level, luck, lure) gained the rod stack, which 1.21.2's constructor never reads.
 */
public final class CbEntityProjectiles {

    /** the stack argument 1.21.1's constructor did not have; its value is replaced by {@link #defaultItem} */
    public static class_1799 noItem() {
        return class_1799.field_8037;
    }

    /** after the constructor: the item 1.21.1's left in place, new ItemStack(getDefaultItem()) */
    public static void defaultItem(class_3857 projectile) {
        projectile.method_16940(new class_1799(((CbEntityProjectileInvoker) projectile).cb$getDefaultItem()));
    }

    /** the same, typed as the class constructed (a constructor's post call takes exactly it) */
    public static void defaultItem(net.minecraft.class_1680 projectile) {
        defaultItem((class_3857) projectile);
    }

    public static void defaultItem(net.minecraft.class_1681 projectile) {
        defaultItem((class_3857) projectile);
    }

    public static void defaultItem(net.minecraft.class_1683 projectile) {
        defaultItem((class_3857) projectile);
    }

    public static void defaultItem(net.minecraft.class_1684 projectile) {
        defaultItem((class_3857) projectile);
    }

    public static void defaultItem(net.minecraft.class_1686 projectile) {
        defaultItem((class_3857) projectile);
    }

    /** FishingHook's rod stack: 1.21.2's constructor never reads it */
    public static class_1799 unreadRod() {
        return class_1799.field_8037;
    }

    public static double ownerX(class_1309 owner) {
        return owner.method_23317();
    }

    /** owner.getEyeY() - 0.1F, the float constant widened as 1.21.1's bytecode did */
    public static double ownerEyeY(class_1309 owner) {
        return owner.method_23320() - (double) 0.1F;
    }

    public static double ownerZ(class_1309 owner) {
        return owner.method_23321();
    }

    /** after ThrowableProjectile(type, x, y, z, level): 1.21.1's setOwner(owner) */
    public static void owner(class_1682 projectile, class_1309 owner) {
        projectile.method_7432((class_1297) owner);
    }

    private CbEntityProjectiles() {
    }
}
