package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.class_2520;
import net.minecraft.class_32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** LevelStorageSource's data fixer and its private level.dat reader of the summary fields (CbWorldStorage) */
@Mixin(class_32.class)
public interface CbWorldLevelStorageSourceAccess {

    @Accessor("field_17668")
    DataFixer cb$fixerUpper();

    @Invoker("method_40035")
    static class_2520 cb$readLightweightData(Path file) throws IOException {
        throw new AssertionError();
    }
}
