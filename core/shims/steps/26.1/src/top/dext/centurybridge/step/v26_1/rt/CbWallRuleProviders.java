package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

/**
 * 1.21.11's RuleBasedBlockStateProvider -- a record of a fallback provider and rules, not itself a provider --
 * is 26.1's RuleBasedStateProvider, a BlockStateProvider of the same fallback and rules (classRenames; its Rule
 * is 26.1's Rule). 26.1 asks its rules first and then the fallback, as 1.21.11's getState did; the members
 * 26.1 no longer declares are these.
 */
public final class CbWallRuleProviders {

    private CbWallRuleProviders() {
    }

    /** 1.21.11 RuleBasedBlockStateProvider.simple(provider): the provider alone, no rules */
    public static RuleBasedStateProvider simple(BlockStateProvider provider) {
        return new RuleBasedStateProvider(provider, List.of());
    }

    /** 1.21.11 RuleBasedBlockStateProvider.simple(block) */
    public static RuleBasedStateProvider simple(Block block) {
        return simple(BlockStateProvider.simple(block));
    }

    /** 1.21.11's rule-based provider handed where 26.1 takes any provider: it is one (26.1's RuleBasedStateProvider) */
    public static BlockStateProvider provider(RuleBasedStateProvider provider) {
        return provider;
    }

    /** fallback() */
    public static BlockStateProvider fallback(RuleBasedStateProvider provider) {
        return provider.fallback;
    }

    /** rules() */
    public static List<RuleBasedStateProvider.Rule> rules(RuleBasedStateProvider provider) {
        return provider.rules;
    }

    /** 1.21.11 RuleBasedBlockStateProvider.CODEC: the record's codec, 26.1's map codec as a codec */
    public static Codec<RuleBasedStateProvider> field_38870() {
        return RuleBasedStateProvider.CODEC.codec();
    }

    /**
     * DiskConfiguration.stateProvider(): 26.1 holds any provider where 1.21.11 held a rule-based one; a provider
     * that is not rule-based answers as one with no rules (the same states)
     */
    public static RuleBasedStateProvider ruleBased(BlockStateProvider provider) {
        return provider == null || provider instanceof RuleBasedStateProvider ? (RuleBasedStateProvider) provider : simple(provider);
    }
}
