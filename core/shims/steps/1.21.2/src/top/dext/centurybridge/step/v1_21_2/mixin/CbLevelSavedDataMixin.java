package top.dext.centurybridge.step.v1_21_2.mixin;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_18;
import net.minecraft.class_26;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelMisc;

/**
 * DimensionDataStorage's save, per loaded data: 1.21.1 asked every loaded data save(file, registries); 1.21.2 takes
 * the tag of each dirty one and writes it. A data with a 1.21.1 override of save(file, registries) is asked as 1.21.1
 * asked it -- with its file, dirty or not -- and left out of 1.21.2's writing (CbLevelMisc).
 */
@Mixin(class_26.class)
public abstract class CbLevelSavedDataMixin {

    @Shadow
    @Final
    private class_7225.class_7874 field_48926;

    @Shadow
    private Path method_17922(String id) {
        throw new AssertionError();
    }

    @Inject(method = "method_61879", at = @At("HEAD"), cancellable = true)
    private void cb$legacySave(Map<Path, ?> tags, String id, Optional<class_18> data, CallbackInfo ci) {
        if (data.isPresent() && data.get() instanceof CbLevelMisc.LegacySaveFile legacy) {
            legacy.method_17919(this.method_17922(id).toFile(), this.field_48926);
            ci.cancel();
        }
    }
}
