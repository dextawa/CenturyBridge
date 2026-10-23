package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMemberComponents;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbPendingIdsItems;

/**
 * A block item whose block is a converted 1.21.1 Equipable: its EQUIPPABLE is answered by the block's members
 * (CbItemEquipable). Item's constructor decides on the components view before BlockItem has set its block, so the
 * decision is taken again once the block is known.
 */
@Mixin(class_1747.class)
public abstract class CbItemEquipableBlockItemMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$equipableBlock(class_2248 block, class_1792.class_1793 props, CallbackInfo ci) {
        class_1792 self = (class_1792) (Object) this;
        if (!(self.method_57347() instanceof CbItemMemberComponents) && CbItemMemberComponents.needed(self)) {
            ((CbPendingIdsItems.ItemFields) self).cb$setComponents(new CbItemMemberComponents(self, CbItemMembers.built(self)));
        }
    }
}
