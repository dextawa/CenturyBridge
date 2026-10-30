package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1657;

/**
 * 1.21.4's Level / LevelAccessor sound and event members took the excepted player (Player, null for none); 1.21.5
 * widened that parameter to Entity (playSound, playSeededSound, levelEvent, mayInteract; javap -p 1.21.4 / 1.21.5
 * class_1937, class_1936, class_3218, class_638, class_3233).
 *
 * 1.21.5's own implementations read the Entity as "the Player, or none": ServerLevel.playSeededSound / levelEvent
 * pass {@code entity instanceof Player p ? p : null} to PlayerList.broadcast (javap -c 1.21.5 class_3218 method_8465,
 * method_8444), and 1.21.4's Entity overload playSound(Entity, BlockPos, ..) (method_45445, gone in 1.21.5) did the
 * same before calling playSound(Player, ..) (javap -c 1.21.4 class_1937 method_45445). So where 1.21.5 vanilla calls
 * the widened shape on a 1.21.4 override, the override is handed {@link #player} of the entity: what a 1.21.4 caller
 * passed for that sound.
 */
public final class CbLevelSounds {
    private CbLevelSounds() {
    }

    /** the excepted player a 1.21.4 override expects: the entity when it is a player, otherwise none */
    public static class_1657 player(class_1297 entity) {
        return entity instanceof class_1657 p ? p : null;
    }
}
