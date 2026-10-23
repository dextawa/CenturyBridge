package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity;

/**
 * 1.21.1 AbstractZombieModel.isAggressive(T monster), abstract there, which its setupAnim asked for the raised arms.
 * 1.21.2 reads ZombieRenderState.isAggressive (extracted as mob.isAggressive()). Put back with the body of 1.21.1's
 * ZombieModel (every 1.21.2 vanilla subclass is one, but GiantZombieModel, whose own answer its mixin puts back); a
 * mod model implements its own. 1.21.2's AbstractZombieModel.setupAnim(state) poses with the model's answer for the
 * entity the state was extracted from (for vanilla's models the same as the extraction's), restored afterwards.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3968.class)
public abstract class CbRenderModelZombieMixin implements CbRenderModelFields.ZombieAggressive {

    @Unique
    private Boolean cb$extractedAggressive;

    @Inject(method = "method_17791", at = @At("HEAD"))
    private void cb$askModel(net.minecraft.class_10086 state, CallbackInfo ci) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1588 monster) {
            this.cb$extractedAggressive = state.field_53634;
            state.field_53634 = ((CbRenderModelFields.ZombieAggressive) this).method_17790(monster);
        }
    }

    @Inject(method = "method_17791", at = @At("RETURN"))
    private void cb$restore(net.minecraft.class_10086 state, CallbackInfo ci) {
        if (this.cb$extractedAggressive != null) {
            state.field_53634 = this.cb$extractedAggressive;
            this.cb$extractedAggressive = null;
        }
    }

    @Override
    public boolean method_17790(net.minecraft.class_1588 monster) {
        return monster.method_6510();
    }
}
