package cb.s9.mixin;

import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_4209;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** a 1.21.8 invoker on DebugPackets' private getMemoryDescriptions(entity, gameTime) */
@Mixin(class_4209.class)
public interface DebugPacketsInvoker {
    @Invoker("method_36157")
    static List<String> cbs9$memories(class_1309 entity, long gameTime) {
        throw new AssertionError();
    }
}
