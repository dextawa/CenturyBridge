package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * 1.21.11 enchantment effect codecs took the loot context parameter set their requirements were checked against
 * while decoding (ConditionalEffect.conditionCodec(set): LootItemCondition.DIRECT_CODEC validated by a
 * ValidationContext over a ProblemReporter.Collector, the collected report as the error); 26.1's codecs take only
 * the effect codec and check the requirements later. These are 26.1's codecs (the same fields and record) with
 * 1.21.11's decode-time check of the requirements put back.
 */
public final class CbWallEnchantCodecs {

    private CbWallEnchantCodecs() {
    }

    private static DataResult<LootItemCondition> check(ContextKeySet set, LootItemCondition condition) {
        ProblemReporter.Collector reporter = new ProblemReporter.Collector();
        condition.validate(new ValidationContext(reporter, set));
        return reporter.isEmpty() ? DataResult.success(condition) : DataResult.error(reporter::getReport);
    }

    private static <E> DataResult<E> checkRequirements(ContextKeySet set, E effect, Optional<LootItemCondition> requirements) {
        return requirements.isEmpty() ? DataResult.success(effect) : check(set, requirements.get()).map(c -> effect);
    }

    /** 1.21.11 ConditionalEffect.conditionCodec(set) */
    public static Codec<LootItemCondition> conditionCodec(ContextKeySet set) {
        return LootItemCondition.DIRECT_CODEC.validate(condition -> check(set, condition));
    }

    /** 1.21.11 ConditionalEffect.codec(effectCodec, set) */
    public static <T> Codec<ConditionalEffect<T>> codec(Codec<T> effectCodec, ContextKeySet set) {
        return ConditionalEffect.codec(effectCodec).validate(e -> checkRequirements(set, e, e.requirements()));
    }

    /** 1.21.11 TargetedConditionalEffect.codec(effectCodec, set) */
    public static <S> Codec<TargetedConditionalEffect<S>> targetedCodec(Codec<S> effectCodec, ContextKeySet set) {
        return TargetedConditionalEffect.codec(effectCodec).validate(e -> checkRequirements(set, e, e.requirements()));
    }

    /** 1.21.11 TargetedConditionalEffect.equipmentDropsCodec(effectCodec, set) */
    public static <S> Codec<TargetedConditionalEffect<S>> equipmentDropsCodec(Codec<S> effectCodec, ContextKeySet set) {
        return TargetedConditionalEffect.equipmentDropsCodec(effectCodec).validate(e -> checkRequirements(set, e, e.requirements()));
    }
}
