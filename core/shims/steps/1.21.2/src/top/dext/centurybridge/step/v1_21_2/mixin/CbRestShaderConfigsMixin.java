package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestPostEffect;

/**
 * Which post effects 1.21.2's ShaderManager (class_10151) has: the configs apply (method_62945) builds the compilation
 * cache from -- the only place after the constructor (empty configs) that replaces it (javap). 1.21.2's getPostChain
 * hands an unknown id to the resource pack recovery (with only the vanilla pack selected, a crash), where 1.21.1's
 * loadEffect logged a missing effect and went on; CbRestPostEffectRestored's loadEffect asks here first.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10151.class)
public abstract class CbRestShaderConfigsMixin implements CbRestPostEffect.Configs {

    @Unique
    private volatile net.minecraft.class_10151.class_10153 cb$v1_21_2$configs = net.minecraft.class_10151.class_10153.field_53948;

    @Inject(method = "method_62945", at = @At("HEAD"))
    private void cb$v1_21_2$configs(net.minecraft.class_10151.class_10153 configs, net.minecraft.class_3300 resourceManager,
                                    net.minecraft.class_3695 profiler, CallbackInfo ci) {
        this.cb$v1_21_2$configs = configs;
    }

    @Override
    public boolean cb$v1_21_2$hasPostEffect(net.minecraft.class_2960 id) {
        return this.cb$v1_21_2$configs.comp_3107().containsKey(id);
    }
}
