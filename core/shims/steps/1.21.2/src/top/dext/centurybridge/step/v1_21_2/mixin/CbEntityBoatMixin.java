package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.function.Supplier;
import net.minecraft.class_10255;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.BoatType;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityBoats;

/**
 * AbstractBoat: 1.21.1's per-boat variant for a boat whose type has no wood of its own (a mod's type), saved and
 * loaded as 1.21.1's Boat did ("Type"); the drop supplier a 1.21.1 constructor sets; the final getPickResult, which
 * hands the call to a mod class's own 1.21.1 override (CbEntityBoats).
 */
@Mixin(class_10255.class)
public abstract class CbEntityBoatMixin implements CbEntityBoats.BoatFields {

    @Shadow
    @Final
    @Mutable
    private Supplier<class_1792> field_54442;

    @Unique
    private BoatType cb$variant;

    @Override
    public BoatType cb$variant() {
        return this.cb$variant;
    }

    @Override
    public void cb$setVariant(BoatType variant) {
        this.cb$variant = variant;
    }

    @Override
    public void cb$setDropItem(Supplier<class_1792> drop) {
        this.field_54442 = drop;
    }

    @Inject(method = "method_5652", at = @At("TAIL"))
    private void cb$saveVariant(class_2487 tag, CallbackInfo ci) {
        if (this.cb$variant != null) {
            tag.method_10582("Type", this.cb$variant.method_15434());
        }
    }

    @Inject(method = "method_5749", at = @At("TAIL"))
    private void cb$loadVariant(class_2487 tag, CallbackInfo ci) {
        // only a boat whose type has no wood of its own kept the variant (CbEntityBoats.setVariant)
        if (tag.method_10573("Type", 8) && BoatType.of(((class_10255) (Object) this).method_5864()) == null) {
            this.cb$variant = BoatType.method_7561(tag.method_10558("Type"));
        }
    }

    @Inject(method = "method_31480", at = @At("HEAD"), cancellable = true)
    private void cb$pickResult(CallbackInfoReturnable<class_1799> cir) {
        class_1799 r = CbEntityBoats.pickResult((class_10255) (Object) this);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
