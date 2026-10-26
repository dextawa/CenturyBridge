package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityAttributes;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * AttributeInstance.removePermanentModifier(UUID) of 1.20.6 (a cover; 1.21 has no permanent-only removal).
 * 1.20.6's body, on 1.21's maps: the modifier comes off only when it is permanent -- out of the permanent
 * map, the per-operation map and the by-id map, then setDirty -- and a transient one with that uuid stays.
 * It touches the maps directly, as 1.20.6 did, so it reaches no overridable method of a subclass.
 */
@Mixin(value = class_1324.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityAttributeInstanceMixin {

    @Shadow
    @Final
    private Map<class_2960, class_1322> field_23702;

    @Shadow
    @Final
    private Map<class_2960, class_1322> field_23703;

    @Shadow
    abstract Map<class_2960, class_1322> method_6193(class_1322.class_1323 operation);

    @Shadow
    protected abstract void method_26838();

    /** 1.20.6 removePermanentModifier(UUID) */
    public boolean method_27304(UUID uuid) {
        class_2960 id = CbEntityAttributes.idOf(uuid);
        class_1322 modifier = this.field_23703.remove(id);
        if (modifier == null) {
            return false;
        }
        this.method_6193(modifier.comp_2450()).remove(modifier.comp_2447());
        this.field_23702.remove(id);
        this.method_26838();
        return true;
    }
}
