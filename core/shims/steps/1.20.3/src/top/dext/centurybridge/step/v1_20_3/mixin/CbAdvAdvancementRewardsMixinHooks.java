package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;

/**
 * The injector into 1.20.3's own AdvancementRewards static initializer that goes with
 * {@link CbAdvAdvancementRewardsMixin} (its restored 1.20.2 members stay there, at {@code CbVanillaReads.PRIORITY}):
 * AdvancementRewards.CODEC goes through CbAdvJson.rewardsCodec, 1.20.2's way of writing rewards.
 */
@Mixin(net.minecraft.class_170.class)
public abstract class CbAdvAdvancementRewardsMixinHooks {

    @Shadow
    @Final
    @Mutable
    public static Codec<net.minecraft.class_170> field_47185;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$rewardsCodec(CallbackInfo ci) {
        field_47185 = CbAdvJson.rewardsCodec(field_47185);
    }
}
