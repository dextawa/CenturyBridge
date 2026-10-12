package cb.mixintest.mixin;

import java.nio.file.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Util.safeReplaceOrMoveFile returns a boolean in 1.20.3 (step 2 callReshapes, ret pop): a static invoker */
@Mixin(net.minecraft.class_156.class)
public interface UtilAccess {
    @Invoker("safeReplaceOrMoveFile")
    static void cbtest$replace(Path current, Path latest, Path backup, boolean restore) {
        throw new AssertionError();
    }
}
