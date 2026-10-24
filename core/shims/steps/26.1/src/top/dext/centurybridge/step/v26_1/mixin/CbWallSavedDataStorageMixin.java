package top.dext.centurybridge.step.v26_1.mixin;

import java.nio.file.Path;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallSavedData;

/** a SavedDataType a 1.21.11 mod named by a string keeps its data in the file 1.21.11 used (rt/CbWallSavedData) */
@Mixin(SavedDataStorage.class)
public abstract class CbWallSavedDataStorageMixin {

    @Shadow
    @Final
    private Path dataFolder;

    @Inject(method = "getDataFile(Lnet/minecraft/resources/Identifier;)Ljava/nio/file/Path;", at = @At("HEAD"), cancellable = true)
    private void cb$legacyFile(Identifier id, CallbackInfoReturnable<Path> cir) {
        String legacy = CbWallSavedData.legacyName(id);
        if (legacy != null) {
            cir.setReturnValue(CbWallSavedData.legacyFile(this.dataFolder, legacy));
        }
    }
}
