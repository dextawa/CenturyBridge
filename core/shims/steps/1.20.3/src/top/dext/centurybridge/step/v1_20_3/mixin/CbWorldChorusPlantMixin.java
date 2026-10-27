package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1922;
import net.minecraft.class_2283;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldChorusPlantAccess;

/**
 * 1.20.2's ChorusPlantBlock.getStateForPlacement(BlockGetter, pos) was an instance method (1.20.3: the static
 * getStateWithConnections(level, pos, state)). It comes back with its 1.20.2 body (the connections around the
 * block's own default state) for the mods that call it and the subclasses that override it, and the plant's
 * placement asks it, as 1.20.2's did (CbWorldChorusPlantMixinHooks, default priority; CbWorldChorusFlowerMixin does
 * the same for the flower's growth).
 */
@Mixin(value = class_2283.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldChorusPlantMixin implements CbWorldChorusPlantAccess {

    public class_2680 method_9759(class_1922 level, class_2338 pos) {
        return class_2283.method_9759(level, pos, ((class_2283) (Object) this).method_9564());
    }

    @Override
    public class_2680 cb$getStateForPlacement(class_1922 level, class_2338 pos) {
        return this.method_9759(level, pos);
    }
}
