package top.dext.centurybridge.step.v26_1.rt;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.GoAndGiveItemsToTarget;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's GoAndGiveItemsToTarget.throwItem(thrower, stack, target) (class_7296.method_43393), which 26.1 replaced by
 * the behaviour's ItemThrower: 1.21.11's body -- BehaviorUtils.throwItem with the velocity (0.2, 0.3, 0.2) and
 * spread 0.2, then, on every seventh game tick and there with a random below 0.9 (nine times in ten), the allay's throw sound at one
 * of Allay.THROW_SOUND_PITCHES.
 * <p>
 * 1.21.11's GoAndGiveItemsToTarget(targetPositionGetter, speedModifier, timeoutDuration) did, after throwing, what
 * 26.1 hands to the ItemThrower it now takes: the throw sound (as above) for any thrower, and for an allay the
 * ALLAY_DROP_ITEM_ON_BLOCK trigger of its liked player at the block below the target. {@link #legacyThrower()} is
 * that thrower (26.1's own allay thrower does the same for allays).
 */
public final class CbWallGiveItems {

    private CbWallGiveItems() {
    }

    public static void throwItem(LivingEntity thrower, ItemStack stack, Vec3 target) {
        Vec3 velocity = new Vec3(0.2F, 0.3F, 0.2F);
        BehaviorUtils.throwItem(thrower, stack, target, velocity, 0.2F);
        Level level = thrower.level();
        if (level.getGameTime() % 7L == 0L && level.getRandom().nextDouble() < 0.9) {
            float pitch = Util.getRandom(Allay.THROW_SOUND_PITCHES, level.getRandom());
            level.playSound(null, thrower, SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 1.0F, pitch);
        }
    }

    /** the ItemThrower 1.21.11's GoAndGiveItemsToTarget was: its own after-throw sound and allay trigger */
    public static <E extends LivingEntity> GoAndGiveItemsToTarget.ItemThrower<E> legacyThrower() {
        return (level, thrower, stack, targetPos) -> {
            if (level.getGameTime() % 7L == 0L && level.getRandom().nextDouble() < 0.9) {
                float pitch = Util.getRandom(Allay.THROW_SOUND_PITCHES, level.getRandom());
                level.playSound(null, thrower, SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 1.0F, pitch);
            }
            if (thrower instanceof Allay) {
                AllayAi.getLikedPlayer(thrower).ifPresent(player -> CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, targetPos.below(), stack));
            }
        };
    }

    /** 1.21.11 new GoAndGiveItemsToTarget(targetPositionGetter, speedModifier, timeoutDuration) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static GoAndGiveItemsToTarget goAndGiveItemsToTarget(Function<LivingEntity, Optional<PositionTracker>> targetPositionGetter,
                                                                float speedModifier, int timeoutDuration) {
        return new GoAndGiveItemsToTarget(targetPositionGetter, speedModifier, timeoutDuration, legacyThrower());
    }
}
