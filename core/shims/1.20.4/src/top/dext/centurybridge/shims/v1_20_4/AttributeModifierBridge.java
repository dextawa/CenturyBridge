package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_1322;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.3 removed EntityAttributeModifier.getName. Legacy callers mostly use
 * it for logging/dedup keys; the UUID string preserves uniqueness.
 */
@Mixin(class_1322.class)
public abstract class AttributeModifierBridge {

    public String method_6185() {
        return "centurybridge:" + ((class_1322) (Object) this).method_6189();
    }
}
