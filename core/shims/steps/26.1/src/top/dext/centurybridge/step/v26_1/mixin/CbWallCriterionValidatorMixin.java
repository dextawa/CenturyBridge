package top.dext.centurybridge.step.v26_1.mixin;

import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11's CriterionValidator, which 26.1 renamed ValidationContextSource and reduced to handing out
 * validation contexts: its validate members given back (covers), 1.21.11's bodies -- each predicate
 * validated in a context of the reporter's child for the field (and index) named, the key set given (the
 * advancement entity's by default) and the loot data.
 */
@Mixin(value = ValidationContextSource.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCriterionValidatorMixin {

    @Shadow
    @Final
    private ProblemReporter reporter;

    @Shadow
    @Final
    private HolderGetter.Provider lootData;

    public void validateEntity(Optional<ContextAwarePredicate> predicate, String name) {
        predicate.ifPresent(p -> this.validateEntity(p, name));
    }

    public void validateEntities(List<ContextAwarePredicate> predicates, String name) {
        this.validate(predicates, LootContextParamSets.ADVANCEMENT_ENTITY, name);
    }

    public void validateEntity(ContextAwarePredicate predicate, String name) {
        this.validate(predicate, LootContextParamSets.ADVANCEMENT_ENTITY, name);
    }

    public void validate(ContextAwarePredicate predicate, ContextKeySet keys, String name) {
        predicate.validate(new ValidationContext(this.reporter.forChild(new ProblemReporter.FieldPathElement(name)), keys, this.lootData));
    }

    public void validate(List<ContextAwarePredicate> predicates, ContextKeySet keys, String name) {
        for (int i = 0; i < predicates.size(); i++) {
            ContextAwarePredicate predicate = predicates.get(i);
            predicate.validate(new ValidationContext(this.reporter.forChild(new ProblemReporter.IndexedFieldPathElement(name, i)), keys, this.lootData));
        }
    }
}
