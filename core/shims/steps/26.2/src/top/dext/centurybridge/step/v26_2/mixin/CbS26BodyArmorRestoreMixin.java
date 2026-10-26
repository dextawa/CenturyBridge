package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.rt.CbS26BodyArmor;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * Mob.setBodyArmorItem(ItemStack), which 26.2 dropped (its two callers make the call it made themselves), restored
 * with 26.1.2's body (javap: GETSTATIC EquipmentSlot.BODY, setItemSlotAndDropWhenKilled(BODY, stack)). See
 * rt/CbS26BodyArmor.
 */
@Mixin(value = Mob.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26BodyArmorRestoreMixin implements CbS26BodyArmor.Legacy {

    @Shadow
    protected abstract void setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack);

    /** 26.1.2 Mob.setBodyArmorItem */
    @CbVanillaReads("CbS26BodyArmorHorseRouteMixin (AbstractHorse.equipBodyArmor) and CbS26BodyArmorWolfRouteMixin (Wolf.mobInteract), where 26.1.2 called it, ask it in place of 26.2's setItemSlotAndDropWhenKilled(BODY, stack) when a converted mod mixed it (or the animal's class overrides it)")
    @Override
    public void setBodyArmorItem(ItemStack stack) {
        this.setItemSlotAndDropWhenKilled(EquipmentSlot.BODY, stack);
    }

    @Override
    public void cbS26$setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack) {
        this.setItemSlotAndDropWhenKilled(slot, stack);
    }
}
