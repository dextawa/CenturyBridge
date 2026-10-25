package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_9323;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMemberComponents;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;

/**
 * Item for a converted 1.21.1 item class overriding members 1.21.2 turned into components (CbItemMembers):
 * its default components answer ENCHANTABLE / ATTRIBUTE_MODIFIERS by its overrides, and getName(stack) --
 * 1.21.1's translatable(getDescriptionId(stack)) when the stack has no ITEM_NAME of its own -- asks its
 * getDescriptionId(stack) override. The 1.21.1 members it restores are in CbItemMembersRestoredMixin.
 */
@Mixin(class_1792.class)
public abstract class CbItemMembersMixin {

    @Shadow
    @Final
    @Mutable
    private class_9323 field_49263;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$memberComponents(class_1792.class_1793 props, CallbackInfo ci) {
        class_1792 self = (class_1792) (Object) this;
        if (CbItemMemberComponents.needed(self)) {
            field_49263 = new CbItemMemberComponents(self, field_49263);
        }
    }

    /**
     * getDescriptionId(), final in 1.21.2: a 1.21.1 class's override (renamed cb$getDescriptionId by the
     * converter) or getOrCreateDescriptionId() override answers it, as 1.21.1's virtual call reached them. A call
     * made from inside that override -- its super call into a facade class that inherits this final method
     * (ItemNameBlockItem) -- is the superclass's body: the description id field, as 1.21.1's Item and
     * ItemNameBlockItem answered (getOrCreateDescriptionId).
     */
    @Inject(method = "method_7876", at = @At("HEAD"), cancellable = true)
    private void cb$descriptionId(CallbackInfoReturnable<String> cir) {
        Object self = this;
        if (!(self instanceof CbItemMembers.LegacyDescriptionIdOverride)
                && !(self instanceof CbItemMembers.LegacyOrCreateDescriptionId)) {
            // 1.21.1's getDescriptionId() answered getOrCreateDescriptionId(): the restored member where a mod's mixin
            // makes it answer otherwise than the field
            String read = CbItemMembers.orCreateDescriptionIdRead((class_1792) self);
            if (read != null) {
                cir.setReturnValue(read);
            }
            return;
        }
        if (!CbItemMembers.enterDescriptionId(self)) {
            return;
        }
        try {
            if (self instanceof CbItemMembers.LegacyDescriptionIdOverride l) {
                cir.setReturnValue(l.cb$getDescriptionId());
            } else {
                cir.setReturnValue(((CbItemMembers.LegacyOrCreateDescriptionId) self).method_7869());
            }
        } finally {
            CbItemMembers.exitDescriptionId(self);
        }
    }

    /**
     * getName(stack): 1.21.1's translatable(getDescriptionId(stack)) where a 1.21.1 class's override or a mod's
     * mixin on the restored member answers (CbItemMembers.nameRead); otherwise 1.21.2's ITEM_NAME stands
     */
    @Inject(method = "method_7864", at = @At("HEAD"), cancellable = true)
    private void cb$name(class_1799 stack, CallbackInfoReturnable<class_2561> cir) {
        class_2561 name = CbItemMembers.nameRead((class_1792) (Object) this, stack);
        if (name != null) {
            cir.setReturnValue(name);
        }
    }

    /**
     * getName(): 1.21.1's getDescription() where a 1.21.1 class overrides it or a mod's mixin on the restored member
     * answers otherwise than its body (CbItemMembers.descriptionRead); otherwise 1.21.2's ITEM_NAME stands
     */
    @Inject(method = "method_63680", at = @At("HEAD"), cancellable = true)
    private void cb$description(CallbackInfoReturnable<class_2561> cir) {
        class_2561 name = CbItemMembers.descriptionRead((class_1792) (Object) this);
        if (name != null) {
            cir.setReturnValue(name);
        }
    }
}
