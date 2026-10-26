package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/** CompoundTag: 1.20.4's protected entries() -- an unmodifiable view of the tag's map -- which 1.20.5 replaced with entrySet() */
@Mixin(value = net.minecraft.class_2487.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFinCompoundTagMixin {

    @Shadow
    @Final
    private Map<String, net.minecraft.class_2520> field_11515;

    /** was entries() */
    protected Map<String, net.minecraft.class_2520> method_29143() {
        return java.util.Collections.unmodifiableMap(this.field_11515);
    }
}
