package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 MapBanner.getName() and save(), which 1.20.5's MapBanner record dropped (its name() answers an
 * Optional), with 1.20.4's bodies: each reads the banner's own fields, so a mod's super call reaches
 * exactly what 1.20.4's did. (getPos() / getColor() are plain getters: the generated
 * CbRestRecMapBannerMixin.) A mod class overriding getName() is hooked so 1.20.5's name() reaches the
 * override (overrideHooks); a mod class overriding save() carries CbRestMaps.OwnSave (adapter hook),
 * through which the step's runtime calls it.
 */
@Mixin(value = net.minecraft.class_17.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestMapBannerMixin {

    @Shadow
    @Final
    private net.minecraft.class_2338 comp_2312;

    @Shadow
    @Final
    private net.minecraft.class_1767 comp_2313;

    @Shadow
    @Final
    private java.util.Optional<net.minecraft.class_2561> comp_2314;

    /** 1.20.4 MapBanner.getName(): the name, null for none */
    public net.minecraft.class_2561 method_68() {
        return this.comp_2314.orElse(null);
    }

    /** 1.20.4 MapBanner.save(): the banner as 1.20.4's NBT */
    public net.minecraft.class_2487 method_74() {
        return top.dext.centurybridge.step.v1_20_5.rt.CbRestMaps.saveBanner(this.comp_2312, this.comp_2313,
            this.comp_2314.orElse(null));
    }
}
