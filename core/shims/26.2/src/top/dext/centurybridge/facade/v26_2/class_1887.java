package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

/**
 * Facade for 1.20.1 net/minecraft/class_1887 (Enchantment), which became a
 * final record in 26.2 (enchantments are now fully data-driven JSON, no
 * subclassing). 101 corpus classes extend it to declare custom enchantments.
 *
 * On 26.2 that pattern cannot function -- a subclass enchantment can never be
 * registered into the data-driven system. This facade exists only to keep
 * those classes LOADABLE (JVM refuses `extends <final>` with a VerifyError
 * before any code runs): it is a non-final abstract class mirroring the old
 * API surface, with vanilla-default method bodies. The mod's enchantment will
 * construct and its overrides compile/resolve, but it is never wired into
 * 26.2's enchantment registry -- a ledgered behavioural degrade, not a crash.
 *
 * Member names are the intermediary tokens the mod bytecode carries (classRename
 * rewrites the owning type, not member names).
 */
public abstract class class_1887 {

    protected final class_1886 field_9085;   // EnchantmentTarget target
    public final class_1888 field_9083;      // Rarity (note: 1.20.1 field order)
    private final EquipmentSlot[] field_9086; // slots
    protected String field_9084;             // descriptionId

    protected class_1887(class_1888 rarity, class_1886 target, EquipmentSlot[] slots) {
        this.field_9083 = rarity;
        this.field_9085 = target;
        this.field_9086 = slots;
    }

    /** was Enchantment.byRawId(int) -- no registry on the facade */
    public static class_1887 method_8191(int rawId) {
        return null;
    }

    /** getEquipment(LivingEntity): no slot items tracked */
    public java.util.Map<EquipmentSlot, ItemStack> method_8185(LivingEntity entity) {
        return java.util.Map.of();
    }

    /** getRarity() */
    public class_1888 method_8186() {
        return field_9083;
    }

    /** getMinLevel() */
    public int method_8187() {
        return 1;
    }

    /** getMaxLevel() */
    public int method_8183() {
        return 1;
    }

    /** getMinPower(int level) */
    public int method_8182(int level) {
        return 1 + level * 10;
    }

    /** getMaxPower(int level) */
    public int method_20742(int level) {
        return method_8182(level) + 5;
    }

    /** getProtectionAmount(int level, DamageSource source) */
    public int method_8181(int level, net.minecraft.world.damagesource.DamageSource source) {
        return 0;
    }

    /** getAttackDamage(int level, MobType group) -- MobType (class_1310) is gone
     *  in 26.2; the facade cannot name it, so this overload is declared with the
     *  erased Object parameter. A mod override keyed on the old class_1310 type no
     *  longer overrides this (orphaned) -- acceptable, since enchantment damage
     *  logic is dead on 26.2's data-driven system anyway. */
    public float method_8196(int level, Object group) {
        return 0.0F;
    }

    /** canCombine(Enchantment) */
    public final boolean method_8188(class_1887 other) {
        return this != other && method_8180(other) && (other == null || other.method_8180(this));
    }

    /** canAccept(Enchantment) */
    protected boolean method_8180(class_1887 other) {
        return true;
    }

    /** getOrCreateDescriptionId() */
    protected String method_8190() {
        if (field_9084 == null) {
            field_9084 = "enchantment.cb." + System.identityHashCode(this);
        }
        return field_9084;
    }

    /** getDescriptionId() */
    public String method_8184() {
        return method_8190();
    }

    /** getFullname(int level) */
    public Component method_8179(int level) {
        return Component.literal(method_8184() + " " + level);
    }

    /** isAcceptableItem(ItemStack) */
    public boolean method_8192(ItemStack stack) {
        return field_9085 != null && field_9085.method_8177(stack == null ? null : stack.getItem());
    }

    /** onTargetDamaged(LivingEntity user, Entity target, int level) */
    public void method_8189(LivingEntity user, Entity target, int level) {
    }

    /** onUserDamaged(LivingEntity user, Entity attacker, int level) */
    public void method_8178(LivingEntity user, Entity attacker, int level) {
    }

    /** isTreasure() */
    public boolean method_8193() {
        return false;
    }

    /** isCurse() */
    public boolean method_8195() {
        return false;
    }

    /** isTradeable() */
    public boolean method_25949() {
        return true;
    }

    /** isDiscoverable() */
    public boolean method_25950() {
        return true;
    }
}
