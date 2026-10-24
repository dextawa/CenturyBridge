package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.util.CompilableString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CompilableString's private constructor: a 1.21.11 NbtContents path that does not compile (rt/CbWallComponents.nbtPath) */
@Mixin(CompilableString.class)
public interface CbWallCompilableStringAccess {

    @Invoker("<init>")
    static <T> CompilableString<T> cb$new(String source, T compiled) {
        throw new AssertionError();
    }
}
