package top.dext.centurybridge.step.v26_1.rt;

import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Members 26.1 newly demands of 1.21.11 implementors of NeutralMob, BehaviorControl and RecipeBuilder (abstractFills
 * and an override hook). Each answers what 1.21.11 made of the situation:
 * <ul>
 * <li>NeutralMob.getTargetUnchecked(): 26.1's Mob answers its target field; a NeutralMob outside Mob (which keeps
 * Mob's own) had only getTarget(), which 1.21.11's NeutralMob code read.</li>
 * <li>BehaviorControl.getRequiredMemories(): 26.1's Brain registers the memories a behaviour reports when its
 * activity is added; 1.21.11 registered none from behaviours (a brain had the memory types it was made with), so a
 * 1.21.11 behaviour reports none.</li>
 * <li>RecipeBuilder.defaultId(): 1.21.11's save(output) saved under getDefaultRecipeId(getResult()), the result
 * item's registry id; the builder's own getResult() is reached through the adapter its hook adds.</li>
 * </ul>
 */
public final class CbWallAiFills {

    private CbWallAiFills() {
    }

    /** 1.21.11 has no getTargetUnchecked: its NeutralMob code asked getTarget() */
    public static LivingEntity getTargetUnchecked(NeutralMob mob) {
        return mob.getTarget();
    }

    /** 1.21.11 behaviours registered no memories of their own */
    public static Set<MemoryModuleType<?>> getRequiredMemories(BehaviorControl<?> behavior) {
        return Set.of();
    }

    /** a 1.21.11 recipe builder: its getResult() (renamed so past the wall) */
    public interface LegacyRecipeBuilder {
        Item getResult();
    }

    /** 1.21.11 RecipeBuilder.getDefaultRecipeId(getResult()) as a recipe key */
    public static ResourceKey<Recipe<?>> defaultId(LegacyRecipeBuilder builder) {
        return ResourceKey.create(Registries.RECIPE, BuiltInRegistries.ITEM.getKey(builder.getResult()));
    }
}
