package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1741;
import net.minecraft.class_1792;
import net.minecraft.class_3414;
import net.minecraft.class_4059;
import net.minecraft.class_6880;
import net.minecraft.class_8051;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.facade.Equipable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemArmor;

/**
 * 1.21.2's AnimalArmorItem keeps the material and body type it was built with, and whether it has 1.21.1's dyed
 * overlay texture: 1.21.1's AnimalArmorItem was an ArmorItem of type BODY, and its getMaterial / getDefense /
 * getTexture / getOverlayTexture / getBodyType read them (CbItemArmor).
 */
@Mixin(class_4059.class)
public abstract class CbItemAnimalArmorMixin implements CbItemArmor.ArmorFields, CbItemArmor.AnimalFields, Equipable {

    @Unique
    private class_1741 cb$material;

    @Unique
    private class_4059.class_9076 cb$bodyType;

    @Unique
    private Boolean cb$overlay;

    @Inject(method = "<init>(Lnet/minecraft/class_1741;Lnet/minecraft/class_4059$class_9076;Lnet/minecraft/class_1792$class_1793;)V",
        at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_4059.class_9076 bodyType, class_1792.class_1793 props, CallbackInfo ci) {
        this.cb$material = material;
        this.cb$bodyType = bodyType;
    }

    @Inject(method = "<init>(Lnet/minecraft/class_1741;Lnet/minecraft/class_4059$class_9076;Lnet/minecraft/class_6880;ZLnet/minecraft/class_1792$class_1793;)V",
        at = @At("RETURN"))
    private void cb$keep(class_1741 material, class_4059.class_9076 bodyType, class_6880<class_3414> equipSound, boolean damageOnHurt,
                         class_1792.class_1793 props, CallbackInfo ci) {
        this.cb$material = material;
        this.cb$bodyType = bodyType;
    }

    @Override
    public class_1741 cb$armorMaterial() {
        return cb$material;
    }

    @Override
    public class_8051 cb$armorType() {
        return class_8051.field_48838;
    }

    @Override
    public class_4059.class_9076 cb$bodyType() {
        return cb$bodyType;
    }

    @Override
    public boolean cb$overlay() {
        return cb$overlay != null ? cb$overlay : CbItemArmor.vanillaOverlay(cb$material);
    }

    @Override
    public void cb$setOverlay(boolean overlay) {
        this.cb$overlay = overlay;
    }

    /** Equipable.getEquipmentSlot(), as 1.21.1's ArmorItem answered it (CbItemArmor) */
    @Override
    public net.minecraft.class_1304 method_7685() {
        return CbItemArmor.method_7685((class_4059) (Object) this);
    }

    /** Equipable.getEquipSound(), as 1.21.1's ArmorItem answered it (CbItemArmor) */
    @Override
    public net.minecraft.class_6880<net.minecraft.class_3414> method_31570() {
        return CbItemArmor.method_31570((class_4059) (Object) this);
    }
}
