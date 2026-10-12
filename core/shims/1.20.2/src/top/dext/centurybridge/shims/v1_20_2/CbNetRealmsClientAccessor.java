package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_4341;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * RealmsClient.ENVIRONMENT, written by 1.20.1's switchToStage / switchToProd / switchToLocal
 * (CbNetRealms.method_21001 ...): 1.20.1 kept the environment in a mutable static. The layer's
 * access widener makes the field writable for mods (whose currentEnvironment writes are renamed to
 * it); this setter is the bridge's own way in.
 */
@Mixin(class_4341.class)
public interface CbNetRealmsClientAccessor {

    @Accessor("field_45232")
    @Mutable
    static void cb$setEnvironment(class_4341.class_4343 environment) {
        throw new AssertionError();
    }
}
