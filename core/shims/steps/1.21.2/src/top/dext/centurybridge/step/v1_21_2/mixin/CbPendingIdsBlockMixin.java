package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_4970;
import net.minecraft.class_52;
import net.minecraft.class_5321;
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
 * BlockBehaviour: the constructor's two key-derived reads (Properties.effectiveDrops /
 * effectiveDescriptionId) under a placeholder when the properties carry no id, and the fields they fed,
 * for the backfill (CbPendingIds / CbPendingIdsItems).
 */
@Mixin(class_4970.class)
public abstract class CbPendingIdsBlockMixin implements CbPendingIdsItems.BlockFields {

    @Shadow
    @Final
    @Mutable
    protected Optional<class_5321<class_52>> field_23156;

    @Shadow
    @Final
    @Mutable
    protected String field_54005;

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4970$class_2251;method_63505()Ljava/util/Optional;"))
    private Optional<class_5321<class_52>> cb$drops(class_4970.class_2251 props) {
        CbPendingIdsItems.blockReading(this, (CbPendingIdsItems.BlockProps) props);
        return ((CbPendingIdsItems.BlockProps) props).cb$effectiveDrops();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4970$class_2251;method_63506()Ljava/lang/String;"))
    private String cb$descriptionId(class_4970.class_2251 props) {
        CbPendingIdsItems.blockReading(this, (CbPendingIdsItems.BlockProps) props);
        return ((CbPendingIdsItems.BlockProps) props).cb$effectiveDescriptionId();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$constructed(class_4970.class_2251 props, CallbackInfo ci) {
        CbPendingIdsItems.blockConstructed((class_4970) (Object) this, (CbPendingIdsItems.BlockProps) props);
    }

    @Override
    public void cb$setDrops(Optional<class_5321<class_52>> drops) {
        this.field_23156 = drops;
    }

    @Override
    public void cb$setDescriptionId(String id) {
        this.field_54005 = id;
    }
}
