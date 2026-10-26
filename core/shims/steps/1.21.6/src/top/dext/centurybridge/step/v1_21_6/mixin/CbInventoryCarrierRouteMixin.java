package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_1277;
import net.minecraft.class_6067;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbSaveData;
import top.dext.centurybridge.step.v1_21_6.rt.CbValueIo;

/**
 * Routes 1.21.6's InventoryCarrier.writeInventoryToTag / readInventoryFromTag (Allay, villagers, piglins ...) past the
 * restored SimpleContainer.createTag / fromTag (CbContainerSaveRestoredMixin), unchanged unless a converted mod changed
 * them. 1.21.5's put getInventory().createTag(provider) as "Inventory" and, when the tag had an "Inventory" list, read
 * it with fromTag(list, provider); 1.21.6 writes and reads the same list through ItemStack.CODEC typed lists (javap).
 * 1.21.6's default methods run as they are; after each, at TAIL, the restored member is asked only when a converted mod
 * mixed it (CbMixedMembers) or the container is a subclass (CbSaveData.routeCreateTag / routeFromTag).
 */
@Mixin(class_6067.class)
public interface CbInventoryCarrierRouteMixin {

    @Inject(method = "method_46399(Lnet/minecraft/class_11372;)V", at = @At("TAIL"))
    private void cb$legacyInventoryWrite(class_11372 output, CallbackInfo ci) {
        class_1277 container = ((class_6067) (Object) this).method_35199();
        if (CbSaveData.routeCreateTag(container)) {
            CbValueIo.tag(output).method_10566("Inventory",
                ((CbSaveData.LegacyContainer) (Object) container).method_7660(CbValueIo.lookup(output)));
        }
    }

    @Inject(method = "method_46400(Lnet/minecraft/class_11368;)V", at = @At("TAIL"))
    private void cb$legacyInventoryRead(class_11368 input, CallbackInfo ci) {
        class_1277 container = ((class_6067) (Object) this).method_35199();
        if (CbSaveData.routeFromTag(container)) {
            CbValueIo.tag(input).method_10554("Inventory").ifPresent(
                list -> ((CbSaveData.LegacyContainer) (Object) container).method_7659(list, CbValueIo.lookup(input)));
        }
    }
}
