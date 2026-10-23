package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelCalls;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * 1.21.1 PiglinModel.setupAttackAnimation(Mob, ageInTicks) (protected): 1.21.2's setupAttackAnimation(PiglinRenderState,
 * ageInTicks) on a state of the entity carrying this model's attackTime (CbRenderModelCalls.pose), as vanilla's own; and
 * that 1.21.2 method public for the override hook's fall-through (CbRenderModelFields.piglinAttack).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4840.class)
public abstract class CbRenderPiglinMixin implements CbRenderModelFields.PiglinAttack {

    @Shadow
    protected abstract void method_29355(net.minecraft.class_10054 state, float ageInTicks);

    @Override
    public void cb$w$piglinAttack(net.minecraft.class_10054 state, float ageInTicks) {
        this.method_29355(state, ageInTicks);
    }

    protected void method_29355(net.minecraft.class_1308 entity, float ageInTicks) {
        CbRenderModelCalls.pose(this, entity, net.minecraft.class_10054.class,
            s -> this.method_29355((net.minecraft.class_10054) s, ageInTicks));
    }
}
