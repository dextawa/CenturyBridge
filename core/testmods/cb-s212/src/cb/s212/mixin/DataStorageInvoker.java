package cb.s212.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.1 DimensionDataStorage.getDataFile(String) answered a File (1.21.2: a Path) */
@Mixin(net.minecraft.class_26.class)
public interface DataStorageInvoker {
    @Invoker(value = "method_17922", remap = false)
    java.io.File cbs212$dataFile(String id);
}
