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
 * The injectors into 1.21.2's own AbstractZombieModel.setupAnim that go with {@link CbRenderModelZombieMixin} (its
 * restored 1.21.1 isAggressive stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor
 * inside it): the model poses with its own answer for the entity, the state's restored afterwards.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_3968.class)
public abstract class CbRenderModelZombieMixinHooks {

    @Unique
    private Boolean cb$extractedAggressive;

    @Inject(method = "method_17791", at = @At("HEAD"))
    private void cb$askModel(net.minecraft.class_10086 state, CallbackInfo ci) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1588 monster) {
            this.cb$extractedAggressive = state.field_53634;
            state.field_53634 = ((CbRenderModelFields.ZombieAggressive) (Object) this).method_17790(monster);
        }
    }

    @Inject(method = "method_17791", at = @At("RETURN"))
    private void cb$restore(net.minecraft.class_10086 state, CallbackInfo ci) {
        if (this.cb$extractedAggressive != null) {
            state.field_53634 = this.cb$extractedAggressive;
            this.cb$extractedAggressive = null;
        }
    }
}
