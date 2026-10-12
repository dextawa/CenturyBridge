package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 1.20.4 MapItemSavedData.save wrote each banner marker as the banner's own save() -- a mod's MapBanner
 * class overriding save() put its additional keys there. 1.20.5 encodes the banners with
 * MapBanner.CODEC (position, color and name, read through the accessors). The banner list handed to the
 * "banners" put gets, for every banner whose class overrides 1.20.4's save(), the keys that override
 * wrote besides 1.20.4's own Pos / Color / Name (CbRestMaps.withBannerSaves).
 *
 * Also hands the private tracking flags to 1.20.4's scaled(n) (CbRestMaps.method_32364).
 */
@Mixin(net.minecraft.class_22.class)
public abstract class CbRestMapItemSavedDataMixin implements top.dext.centurybridge.step.v1_20_5.rt.CbRestMaps.Tracking {

    @Shadow
    @Final
    private java.util.Map<String, net.minecraft.class_17> field_123;

    @Shadow
    @Final
    private boolean field_114;

    @Shadow
    @Final
    private boolean field_113;

    @Override
    public boolean cbRest$trackingPosition() {
        return this.field_114;
    }

    @Override
    public boolean cbRest$unlimitedTracking() {
        return this.field_113;
    }

    /** the first CompoundTag.put of save() is the "banners" list (the dimension goes in through a lambda) */
    @ModifyArg(method = "method_75", index = 1, at = @At(value = "INVOKE", ordinal = 0,
        target = "Lnet/minecraft/class_2487;method_10566(Ljava/lang/String;Lnet/minecraft/class_2520;)Lnet/minecraft/class_2520;"))
    private net.minecraft.class_2520 cbRest$bannerSaves(String key, net.minecraft.class_2520 banners) {
        return "banners".equals(key)
            ? top.dext.centurybridge.step.v1_20_5.rt.CbRestMaps.withBannerSaves(banners, this.field_123.values())
            : banners;
    }
}
