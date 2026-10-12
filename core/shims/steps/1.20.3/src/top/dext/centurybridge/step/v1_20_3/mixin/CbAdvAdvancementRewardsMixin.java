package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
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
 * AdvancementRewards' 1.20.2 serializeToJson() and getRecipes() back on the class, which became a
 * record in 1.20.3 (covers: calls, overrides and super calls bind to them again), and 1.20.2's way
 * of writing rewards: through the instance's serializeToJson (see CbAdvDisplayInfoMixin --
 * AdvancementRewards.CODEC goes through CbAdvJson.rewardsCodec the same way).
 */
@Mixin(net.minecraft.class_170.class)
public abstract class CbAdvAdvancementRewardsMixin implements CbAdvJson.RewardsJson {

    @Shadow
    @Final
    @Mutable
    public static Codec<net.minecraft.class_170> field_47185;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$rewardsCodec(CallbackInfo ci) {
        field_47185 = CbAdvJson.rewardsCodec(field_47185);
    }

    /** serializeToJson() */
    @Override
    public JsonElement method_747() {
        return CbAdvJson.rewardsToJson((net.minecraft.class_170) (Object) this);
    }

    /** getRecipes() */
    @Override
    public net.minecraft.class_2960[] method_34898() {
        return CbAdvJson.recipes((net.minecraft.class_170) (Object) this);
    }
}
