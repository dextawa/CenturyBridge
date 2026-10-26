package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 AbstractZombieModel.isAggressive(T monster), abstract there, which its setupAnim asked for the raised arms.
 * 1.21.2 reads ZombieRenderState.isAggressive (extracted as mob.isAggressive()). Put back with the body of 1.21.1's
 * ZombieModel (every 1.21.2 vanilla subclass is one, but GiantZombieModel, whose own answer its mixin puts back); a
 * mod model implements its own. 1.21.2's AbstractZombieModel.setupAnim(state) poses with the model's answer for the
 * entity the state was extracted from (for vanilla's models the same as the extraction's), restored afterwards
 * (CbRenderModelZombieMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_3968.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelZombieMixin implements CbRenderModelFields.ZombieAggressive {

    @Override
    public boolean method_17790(net.minecraft.class_1588 monster) {
        return monster.method_6510();
    }
}
