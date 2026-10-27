package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * BadOmenMobEffect (class_8631). 1.20.6's applyEffectTick (method_5572) started a raid straight from Bad Omen in worlds
 * without the update_1_21 feature, through the private method_58613(ServerPlayer, ServerLevel)Z: at the player's block,
 * when the difficulty is not peaceful and the block is a village, Raids.createOrExtendRaid(player, pos); the answer
 * "keep Bad Omen" is true unless a raid was made. 1.21 dropped the feature flag (FeatureFlags has no update_1_21) and
 * with it this branch: every world takes the feature path, where Bad Omen in a village turns into Raid Omen
 * (setRaidOmenPosition at the player's block), and Raid Omen's last tick (RaidOmenMobEffect class_9457.method_5572) calls
 * Raids.createOrExtendRaid(player, raidOmenPosition) -- the same call, the same argument types (javap -c -p of both).
 * This mixin restores method_58613 with 1.20.6's body (javap), at CbVanillaReads.PRIORITY so a mod's 1.20.6 injectors
 * anchor inside it; CbEntityRaidOmenRouteMixin runs it at Raid Omen's createOrExtendRaid call when a converted mod mixes
 * it (CbMixedMembers), so the raid starts when Raid Omen ends (1.21's timing), from the player's block as 1.20.6's body
 * computes it.
 */
@Mixin(targets = "net.minecraft.class_8631", priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityBadOmenRestoredMixin implements CbPrioritySplit.BadOmenRaid {

    @Override
    public boolean cb$v1_21$badOmenRaid(net.minecraft.class_3222 player, net.minecraft.class_3218 level) {
        return this.method_58613(player, level);
    }

    /** 1.20.6 BadOmenMobEffect: the raid at the player's block, true (keep Bad Omen) unless one was made */
    @CbVanillaReads("CbEntityRaidOmenRouteMixin: Raid Omen's createOrExtendRaid call (class_9457.method_5572) runs this method in "
        + "its place when a converted mod mixes it; body 1.20.6's")
    private boolean method_58613(net.minecraft.class_3222 player, net.minecraft.class_3218 level) {
        net.minecraft.class_2338 pos = player.method_24515();
        if (level.method_8407() != net.minecraft.class_1267.field_5801 && level.method_19500(pos)) {
            return level.method_19495().method_16540(player, pos) == null;
        }
        return true;
    }
}
