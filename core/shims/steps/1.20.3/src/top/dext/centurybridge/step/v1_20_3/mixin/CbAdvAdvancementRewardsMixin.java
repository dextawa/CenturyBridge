package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * AdvancementRewards' 1.20.2 serializeToJson() and getRecipes() back on the class, which became a
 * record in 1.20.3 (covers: calls, overrides and super calls bind to them again), and 1.20.2's way
 * of writing rewards: through the instance's serializeToJson (see CbAdvDisplayInfoMixin --
 * AdvancementRewards.CODEC goes through CbAdvJson.rewardsCodec the same way; CbAdvAdvancementRewardsMixinHooks,
 * default priority).
 */
@Mixin(value = net.minecraft.class_170.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvAdvancementRewardsMixin implements CbAdvJson.RewardsJson {

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
