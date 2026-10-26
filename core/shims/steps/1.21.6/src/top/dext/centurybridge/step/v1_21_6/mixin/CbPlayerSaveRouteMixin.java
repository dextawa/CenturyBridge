package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_1656;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1730;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_5134;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbSaveData;
import top.dext.centurybridge.step.v1_21_6.rt.CbValueIo;

/**
 * Routes 1.21.6's Player save and load past the restored 1.21.5 tag forms of the inventory, abilities and ender chest
 * (CbInventorySaveRestoredMixin, CbAbilitiesSaveRestoredMixin, CbEnderSaveRestoredMixin), unchanged unless a converted
 * mod changed them. 1.21.5's Player stored inventory.save(new ListTag()) as "Inventory", abilities.addSaveData(tag),
 * enderChestInventory.createTag(registries) as "EnderItems", and read them back with inventory.load(list),
 * abilities.loadSaveData(tag) (then the movement speed base from the walking speed) and enderChestInventory.fromTag;
 * 1.21.6 writes and reads the same keys through typed lists and Abilities.Packed (javap). 1.21.6's paths run as they
 * are (a native 1.21.6 mod's mixins on them still apply); after each, at TAIL, a restored member is asked only when a
 * converted mod mixed it (CbMixedMembers) or the object is a subclass (CbSaveData.route*): its 1.21.5 form replaces
 * the key 1.21.6 wrote, or it reads the saved data again as 1.21.5 did. Otherwise nothing more runs.
 */
@Mixin(class_1657.class)
public abstract class CbPlayerSaveRouteMixin {

    @Shadow
    @Final
    class_1661 field_7514;

    @Shadow
    @Final
    private class_1656 field_7503;

    @Shadow
    protected class_1730 field_7486;

    @Inject(method = "method_5652(Lnet/minecraft/class_11372;)V", at = @At("TAIL"))
    private void cb$legacyPlayerDataSave(class_11372 output, CallbackInfo ci) {
        class_2487 tag = null;
        if (CbSaveData.routeInventorySave(this.field_7514)) {
            tag = CbValueIo.tag(output);
            tag.method_10566("Inventory", ((CbSaveData.LegacyInventory) (Object) this.field_7514).method_7384(new class_2499()));
        }
        if (CbSaveData.routeAbilitiesSave(this.field_7503)) {
            tag = tag != null ? tag : CbValueIo.tag(output);
            ((CbSaveData.LegacyAbilities) (Object) this.field_7503).method_7251(tag);
        }
        if (CbSaveData.routeCreateTag(this.field_7486)) {
            tag = tag != null ? tag : CbValueIo.tag(output);
            tag.method_10566("EnderItems", ((CbSaveData.LegacyContainer) (Object) this.field_7486)
                .method_7660(((class_1657) (Object) this).method_56673()));
        }
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_11368;)V", at = @At("TAIL"))
    private void cb$legacyPlayerDataLoad(class_11368 input, CallbackInfo ci) {
        class_1657 self = (class_1657) (Object) this;
        class_2487 tag = null;
        if (CbSaveData.routeInventoryLoad(this.field_7514)) {
            tag = CbValueIo.tag(input);
            ((CbSaveData.LegacyInventory) (Object) this.field_7514).method_7397(tag.method_68569("Inventory"));
        }
        if (CbSaveData.routeAbilitiesLoad(this.field_7503)) {
            tag = tag != null ? tag : CbValueIo.tag(input);
            ((CbSaveData.LegacyAbilities) (Object) this.field_7503).method_7249(tag);
            self.method_5996(class_5134.field_23719).method_6192(this.field_7503.method_7253());
        }
        if (CbSaveData.routeFromTag(this.field_7486)) {
            tag = tag != null ? tag : CbValueIo.tag(input);
            ((CbSaveData.LegacyContainer) (Object) this.field_7486).method_7659(tag.method_68569("EnderItems"), self.method_56673());
        }
    }
}
