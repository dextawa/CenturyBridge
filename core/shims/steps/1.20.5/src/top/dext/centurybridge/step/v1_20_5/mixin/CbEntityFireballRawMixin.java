package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Fireball: 1.20.4's protected getItemRaw() -- the synched item stack itself, empty when unset -- which 1.20.5 dropped
 * (the getItem route: CbEntityFireballRawMixinHooks, default priority)
 */
@Mixin(value = net.minecraft.class_3855.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityFireballRawMixin implements CbPrioritySplit.ItemRaw {

    @Shadow
    @Final
    private static net.minecraft.class_2940<net.minecraft.class_1799> field_17081;

    /** was getItemRaw() */
    @CbVanillaReads("CbEntityFireballRawMixinHooks (cbRoute$getItem): getItem answers from it as 1.20.4's did (empty -> the default item) when a converted mod mixes it")
    protected net.minecraft.class_1799 method_16938() {
        return ((net.minecraft.class_1297) (Object) this).method_5841().method_12789(field_17081);
    }

    /** 1.20.4's getItemRaw called virtually, for the getItem route (CbEntityFireballRawMixinHooks) */
    @Override
    public net.minecraft.class_1799 cb$v1_20_5$itemRaw() {
        return this.method_16938();
    }
}
