package top.dext.centurybridge.step.v26_1.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11 SpawnEggItem members on 26.1's, which made the per-egg queries static (they read the stack's entity
 * data, as 1.21.11's instance methods did) and answers byId with the item's holder.
 */
public final class CbWallSpawnEggs {

    private CbWallSpawnEggs() {
    }

    /** 1.21.11 spawnsEntity(stack, type) (an instance method reading only the stack) */
    public static boolean spawnsEntity(SpawnEggItem egg, ItemStack stack, EntityType<?> type) {
        return SpawnEggItem.spawnsEntity(stack, type);
    }

    /** 1.21.11 getType(stack) */
    public static EntityType<?> getType(SpawnEggItem egg, ItemStack stack) {
        return SpawnEggItem.getType(stack);
    }

    /** 1.21.11 spawnOffspringFromSpawnEgg(player, mob, type, level, pos, stack) */
    public static Optional<Mob> spawnOffspringFromSpawnEgg(SpawnEggItem egg, Player player, Mob mob, EntityType<? extends Mob> type, ServerLevel level,
        Vec3 pos, ItemStack stack) {
        return SpawnEggItem.spawnOffspringFromSpawnEgg(player, mob, type, level, pos, stack);
    }

    /** 1.21.11 byId(type): the spawn egg of the entity type, or null */
    public static SpawnEggItem byId(EntityType<?> type) {
        return SpawnEggItem.byId(type).map(h -> h.value() instanceof SpawnEggItem egg ? egg : null).orElse(null);
    }

    /** 1.21.11 eggs(): every spawn egg item (1.21.11's were each recorded by entity type at construction) */
    public static Iterable<SpawnEggItem> eggs() {
        List<SpawnEggItem> eggs = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof SpawnEggItem egg) {
                eggs.add(egg);
            }
        }
        return Collections.unmodifiableList(eggs);
    }

    /** a 1.21.11 spawn egg subclass's own requiredFeatures() (26.1's Item made it final; overrideRenames + CbWallItemFeaturesMixin) */
    public interface LegacyRequiredFeatures {
        FeatureFlagSet cb$legacyRequiredFeatures();
    }
}
