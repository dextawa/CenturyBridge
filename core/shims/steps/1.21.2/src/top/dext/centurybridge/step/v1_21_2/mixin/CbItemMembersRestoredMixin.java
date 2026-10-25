package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's overridable Item members that 1.21.2 dropped, restored on Item (CbItemMembers): a 1.21.1 class's override
 * overrides them, and 1.21.2's reads go through them (each member's CbVanillaReads names the mixin). Priority below
 * every mod's so a mod's mixin may anchor anywhere in them (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1792.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemMembersRestoredMixin implements CbItemMembers.ItemEnchantmentValue, CbItemMembers.ItemRepair,
        CbItemMembers.ItemDescriptionId, CbItemMembers.ItemEnchantable,
        CbItemMembers.ItemDescription, CbItemMembers.ItemSounds, CbItemMembers.ItemOrCreateDescriptionId {

    @org.spongepowered.asm.mixin.Shadow
    @org.spongepowered.asm.mixin.Final
    protected String field_8014;

    /**
     * 1.21.1's Item.getEnchantmentValue(), restored: what the item's built components say (0 without ENCHANTABLE).
     * A 1.21.1 class's own getEnchantmentValue overrides it; 1.21.2's EnchantmentHelper reads ENCHANTABLE through
     * it, so a mod's 1.21.1 mixin on it changes enchanting as it did on 1.21.1.
     */
    @CbVanillaReads("CbItemEnchantableReadsMixin: EnchantmentHelper.getEnchantmentCost / selectEnchantment take the "
        + "ENCHANTABLE they read from Item.getEnchantmentValue() unless the stack sets its own")
    @Override
    public int method_7837() {
        return CbItemMembers.super_method_7837((class_1792) (Object) this);
    }

    /** 1.21.1's Item.isValidRepairItem(stack, ingredient), restored: the REPAIRABLE the item was built with (CbItemMembers) */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isValidRepairItem (AnvilMenu's repair check) answers from it")
    @Override
    public boolean method_7878(class_1799 stack, class_1799 ingredient) {
        return CbItemMembers.super_method_7878((class_1792) (Object) this, stack, ingredient);
    }

    /** 1.21.1's Item.getDescriptionId(stack), restored: the item's description id */
    @CbVanillaReads("CbItemMembersMixin: Item.getName(stack) and (CbItemNameReadsMixin) the overrides of "
        + "CompassItem / PotionItem / TippedArrowItem / ShieldItem name the stack from it where it answers otherwise "
        + "than its body")
    @Override
    public String method_7866(class_1799 stack) {
        return CbItemMembers.super_method_7866((class_1792) (Object) this, stack);
    }

    /** 1.21.1's Item.isEnchantable(stack), restored with its body: a stack of one with MAX_DAMAGE */
    @CbVanillaReads("CbItemStackMembersMixin: ItemStack.isEnchantable() answers from it where it answers otherwise than its body")
    @Override
    public boolean method_7870(class_1799 stack) {
        return stack.method_7914() == 1 && stack.method_57826(net.minecraft.class_9334.field_50072);
    }

    /** 1.21.1's Item.getDescription(), restored with its body: translatable(getDescriptionId()) */
    @CbVanillaReads("CbItemMembersMixin: Item.getName() (1.21.2's, final) answers from it where it answers otherwise than its body")
    @Override
    public net.minecraft.class_2561 method_7848() {
        return net.minecraft.class_2561.method_43471(((class_1792) (Object) this).method_7876());
    }

    /** 1.21.1's Item.getEatingSound(), restored with its body: GENERIC_EAT */
    @CbVanillaReads("CbItemConsumeSoundMixin: Consumable.emitParticlesAndSounds plays ItemStack.getEatingSound() (CbItemStackSoundsMixin, "
        + "which asks this) where it answers otherwise than its body")
    @Override
    public net.minecraft.class_3414 method_21830() {
        return net.minecraft.class_3417.field_20614.comp_349();
    }

    /** 1.21.1's Item.getDrinkingSound(), restored with its body: GENERIC_DRINK */
    @CbVanillaReads("CbItemConsumeSoundMixin: Consumable.emitParticlesAndSounds plays ItemStack.getDrinkingSound() (CbItemStackSoundsMixin, "
        + "which asks this) where it answers otherwise than its body")
    @Override
    public net.minecraft.class_3414 method_21831() {
        return net.minecraft.class_3417.field_20613.comp_349();
    }

    /**
     * 1.21.1's Item.getOrCreateDescriptionId(), restored with its body: the description id field, else "item." and the
     * registry id. 1.21.1 cached the made id in the field; 1.21.2's field is final and set once the item has its id
     * (at construction, or at registration for a converted item, CbPendingIdsItems), so the made id is not stored.
     */
    @CbVanillaReads("CbItemMembersMixin: Item.getDescriptionId() (1.21.2's, final) answers from it where it answers otherwise than the field")
    @Override
    public String method_7869() {
        if (this.field_8014 == null) {
            return net.minecraft.class_156.method_646("item", net.minecraft.class_7923.field_41178.method_10221((class_1792) (Object) this));
        }
        return this.field_8014;
    }
}
