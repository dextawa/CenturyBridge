package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.4 MapBanner's getters and save(), which 1.20.5's MapBanner record dropped (its accessors are
 * pos(), color() and an Optional name()), with 1.20.4's bodies: each reads the banner's own field, so a
 * mod's super call reaches exactly what 1.20.4's did. A mod class overriding them is hooked so 1.20.5's
 * accessor calls reach the override (overrideHooks); the interface lets the step's runtime call a
 * banner's save() -- the mod's own where it overrides it.
 */
@Mixin(net.minecraft.class_17.class)
public abstract class CbRestMapBannerMixin implements top.dext.centurybridge.step.v1_20_5.rt.CbRestMaps.LegacyBanner {

    @Shadow
    @Final
    private net.minecraft.class_2338 comp_2312;

    @Shadow
    @Final
    private net.minecraft.class_1767 comp_2313;

    @Shadow
    @Final
    private java.util.Optional<net.minecraft.class_2561> comp_2314;

    /** 1.20.4 MapBanner.getPos() */
    @Override
    public net.minecraft.class_2338 method_70() {
        return this.comp_2312;
    }

    /** 1.20.4 MapBanner.getColor() */
    @Override
    public net.minecraft.class_1767 method_35502() {
        return this.comp_2313;
    }

    /** 1.20.4 MapBanner.getName(): the name, null for none */
    @Override
    public net.minecraft.class_2561 method_68() {
        return this.comp_2314.orElse(null);
    }

    /** 1.20.4 MapBanner.save(): the banner as 1.20.4's NBT */
    @Override
    public net.minecraft.class_2487 method_74() {
        return top.dext.centurybridge.step.v1_20_5.rt.CbRestMaps.saveBanner(this.comp_2312, this.comp_2313,
            this.comp_2314.orElse(null));
    }
}
