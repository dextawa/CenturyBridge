package cb.s9r.mixin;

import cb.s9r.WardenCheck;
import java.util.List;
import net.minecraft.class_630;
import net.minecraft.class_7280;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.8 mod's mixin reading WardenModel's private part lists at the constructor's RETURN */
@Mixin(class_7280.class)
public abstract class WardenListsMixin {
    @Shadow @Final private List<class_630> field_38451;
    @Shadow @Final private List<class_630> field_38452;
    @Shadow @Final private List<class_630> field_38453;
    @Shadow @Final private List<class_630> field_38454;
    @Shadow @Final protected class_630 field_38316;
    @Shadow @Final protected class_630 field_38317;
    @Shadow @Final protected class_630 field_38318;
    @Shadow @Final protected class_630 field_38319;
    @Shadow @Final protected class_630 field_38320;
    @Shadow @Final protected class_630 field_38321;
    @Shadow @Final protected class_630 field_38322;
    @Shadow @Final protected class_630 field_38323;

    @Inject(method = "<init>(Lnet/minecraft/class_630;)V", at = @At("RETURN"))
    private void cbs9r$lists(class_630 root, CallbackInfo ci) {
        boolean ok = List.of(this.field_38319, this.field_38318).equals(this.field_38451)
            && List.of(this.field_38316).equals(this.field_38452)
            && List.of(this.field_38317, this.field_38321, this.field_38322, this.field_38320, this.field_38323).equals(this.field_38453)
            && List.of(this.field_38316, this.field_38317, this.field_38321, this.field_38322, this.field_38320, this.field_38323)
                .equals(this.field_38454);
        WardenCheck.atInit = "lists at init: " + ok;
        WardenCheck.seen = ok ? List.of(this.field_38451, this.field_38452, this.field_38453, this.field_38454) : List.of();
    }
}
