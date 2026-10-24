package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ValidationContext's private full constructor (CbWallValidationContextMixin) */
@Mixin(ValidationContext.class)
public interface CbWallValidationContextAccess {

    @Invoker("<init>")
    static ValidationContext cb$new(ProblemReporter reporter, ContextKeySet keys, Optional<HolderGetter.Provider> resolver,
        Set<ResourceKey<?>> visitedElements) {
        throw new AssertionError();
    }
}
