package top.dext.centurybridge.step.v1_21_2.mixin;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.class_5762;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.LerpingModel;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Axolotl is 1.21.1's LerpingModel again: 1.21.1's modelRotationValues map (empty at construction, filled by whoever
 * models the axolotl) and getModelRotationValues() answering it. 1.21.2's own model no longer uses it.
 */
@Mixin(value = class_5762.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityLerpingAxolotlMixin implements LerpingModel {

    @Unique
    private final Map<String, Vector3f> cb$modelRotationValues = Maps.newHashMap();

    @Override
    public Map<String, Vector3f> method_36976() {
        return this.cb$modelRotationValues;
    }
}
