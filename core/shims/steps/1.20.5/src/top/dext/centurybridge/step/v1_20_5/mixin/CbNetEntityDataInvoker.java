package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2945;
import net.minecraft.class_9221;
import net.minecraft.class_9227;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * The package-private parts of 1.20.5 SynchedEntityData the entity-data bridge builds with: its
 * constructor over an item array, and the registry of ids allocated per entity class (what the
 * builder sizes its array by).
 */
@Mixin(class_2945.class)
public interface CbNetEntityDataInvoker {

    @Invoker("<init>")
    static class_2945 cb$new(class_9221 holder, class_2945.class_2946<?>[] items) {
        throw new AssertionError();
    }

    @Accessor("field_49021")
    static class_9227 cb$idRegistry() {
        throw new AssertionError();
    }
}
