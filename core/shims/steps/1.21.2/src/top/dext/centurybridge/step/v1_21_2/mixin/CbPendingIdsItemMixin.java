package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_9323;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPendingIdsItems;

/**
 * Item: the constructor's two key-derived reads (Properties.effectiveDescriptionId / effectiveModel)
 * under a placeholder when the properties carry no id, and the fields they fed, for the backfill
 * (CbPendingIds / CbPendingIdsItems). Redirects: a constructor takes an injection only at RETURN.
 */
@Mixin(class_1792.class)
public abstract class CbPendingIdsItemMixin implements CbPendingIdsItems.ItemFields {

    @Shadow
    @Final
    @Mutable
    protected String field_8014;

    @Shadow
    @Final
    @Mutable
    private class_9323 field_49263;

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_63689()Ljava/lang/String;"))
    private String cb$descriptionId(class_1792.class_1793 props) {
        CbPendingIdsItems.itemReading(this, (CbPendingIdsItems.ItemProps) props);
        return ((CbPendingIdsItems.ItemProps) props).cb$effectiveDescriptionId();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1792$class_1793;method_63691()Lnet/minecraft/class_2960;"))
    private class_2960 cb$model(class_1792.class_1793 props) {
        CbPendingIdsItems.itemReading(this, (CbPendingIdsItems.ItemProps) props);
        return ((CbPendingIdsItems.ItemProps) props).cb$effectiveModel();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$constructed(class_1792.class_1793 props, CallbackInfo ci) {
        CbPendingIdsItems.itemConstructed((class_1792) (Object) this, (CbPendingIdsItems.ItemProps) props);
    }

    @Override
    public void cb$setDescriptionId(String id) {
        this.field_8014 = id;
    }

    @Override
    public void cb$setComponents(class_9323 components) {
        this.field_49263 = components;
    }
}
