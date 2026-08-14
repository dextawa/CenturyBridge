package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Entity.getHeightOffset (ride offset) was removed in the
 * vehicle-position rework. The vanilla default was 0.0; legacy overrides keep
 * their bodies (dormant for vanilla, still callable by mod code). Positional
 * approximation, noted in the report.
 */
@Mixin(class_1297.class)
public abstract class EntityBridge {

    public double method_5678() {
        return 0.0D;
    }
}
