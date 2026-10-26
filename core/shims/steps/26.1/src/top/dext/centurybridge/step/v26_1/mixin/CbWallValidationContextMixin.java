package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11's ValidationContext.setContextKeySet(keys), given back (cover): the same context -- reporter,
 * resolver and visited elements -- under another key set, 1.21.11's body.
 */
@Mixin(value = ValidationContext.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallValidationContextMixin {

    @Shadow
    @Final
    private ProblemReporter reporter;

    @Shadow
    @Final
    private Optional<HolderGetter.Provider> resolver;

    @Shadow
    @Final
    private Set<ResourceKey<?>> visitedElements;

    public ValidationContext setContextKeySet(ContextKeySet keys) {
        return CbWallValidationContextAccess.cb$new(this.reporter, keys, this.resolver, this.visitedElements);
    }
}
