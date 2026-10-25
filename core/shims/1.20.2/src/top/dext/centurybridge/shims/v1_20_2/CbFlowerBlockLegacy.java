package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_2356;
import net.minecraft.class_7917;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbLegacySuspiciousEffectHolder;

/**
 * 1.20.1's FlowerBlock.getSuspiciousEffect / getEffectDuration back (s1misc): the flower's one
 * stew effect and its duration in ticks, read from the 1.20.2 entry list its constructor builds
 * from exactly those (the field, not the overridable getSuspiciousEffects, which a converted
 * subclass answers through these).
 */
@Mixin(value = class_2356.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbFlowerBlockLegacy implements CbLegacySuspiciousEffectHolder {

    @Shadow
    @Final
    private List<class_7917.class_8751> field_45775;

    @Override
    public class_1291 method_10188() {
        return this.field_45775.isEmpty() ? null : this.field_45775.get(0).comp_1838();
    }

    @Override
    public int method_10187() {
        return this.field_45775.isEmpty() ? 0 : this.field_45775.get(0).comp_1839();
    }
}
