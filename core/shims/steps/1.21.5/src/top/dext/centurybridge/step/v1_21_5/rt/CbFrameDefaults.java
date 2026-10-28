package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.function.Consumer;
import net.minecraft.class_10774;
import net.minecraft.class_10776;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1311;
import net.minecraft.class_1293;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3765;
import net.minecraft.class_3767;
import net.minecraft.class_5483;
import net.minecraft.server.MinecraftServer;

/**
 * frameDefaults of step 1.21.4 -> 1.21.5: what a 1.21.5 argument stands for when a 1.21.4 override is entered by the
 * mod itself (no frame from 1.21.5's caller) and its super call is rewritten to 1.21.5's shape. Each answers the value
 * 1.21.4's own code used (javap -c -p of both releases, cited per member).
 */
public final class CbFrameDefaults {
    private CbFrameDefaults() {
    }

    /**
     * The ServerLevel 1.21.5 passes to LivingEntity.blockUsingItem / WaterAnimal and Axolotl handleAirSupply: the
     * entity's own level (1.21.5's callers pass (ServerLevel) entity.level(); 1.21.4's bodies read this.level()).
     */
    public static class_3218 ownLevel(class_1297 entity) {
        return (class_3218) entity.method_37908();
    }

    /** MobEffectInstance.tickServer(level, entity, onUpdate): the ticked entity's level (1.21.4's tick read entity.level()) */
    public static class_3218 tickLevel(class_1293 effect, class_1309 entity, Runnable onUpdate) {
        return (class_3218) entity.method_37908();
    }

    /**
     * The ServerLevel of a Raid: 1.21.4's Raid kept its level; 1.21.5's raids belong to their level's Raids
     * (ServerLevel.getRaids()), so it is the level whose Raids know this raid (Raids.getId).
     */
    public static class_3218 raidLevel(class_3765 raid) {
        MinecraftServer s = CbVariants.server();
        if (s != null) {
            for (class_3218 level : s.method_3738()) {
                if (level.method_19495().method_67378(raid).isPresent()) {
                    return level;
                }
            }
        }
        throw new IllegalStateException("no level holds raid " + raid);
    }

    /** The ServerLevel of a Raids (1.21.4 kept it in a field): the level whose getRaids() it is */
    public static class_3218 raidsLevel(class_3767 raids) {
        MinecraftServer s = CbVariants.server();
        if (s != null) {
            for (class_3218 level : s.method_3738()) {
                if (level.method_19495() == raids) {
                    return level;
                }
            }
        }
        throw new IllegalStateException("no level holds these raids");
    }

    /**
     * PlayerAdvancements.flushDirty(player, showAdvancements): 1.21.4 always let the client show the toasts (its update
     * packet had no such flag); 1.21.5's ServerPlayer.tick passes true (javap 1.21.5 class_3222: iconst_1 before
     * flushDirty).
     */
    public static boolean showAdvancements() {
        return true;
    }

    /**
     * Entity.onAboveBubbleColumn(downwards, pos): the column the entity is in (1.21.5's BubbleColumnBlock.entityInside
     * passes its own position, the block the entity is inside).
     */
    public static class_2338 bubbleColumnPos(class_1297 entity, boolean downwards) {
        return entity.method_24515();
    }

    /** GameTestHelper.getBlockEntity(pos, type): any block entity (1.21.4 answered whatever stood there) */
    public static Class<?> anyBlockEntity(Object helper, class_2338 pos) {
        return class_2586.class;
    }

    /** MobSpawnSettings.Builder.addSpawn(category, weight, data): the weight the 1.21.4 data carries */
    public static int spawnWeight(class_5483.class_5496 builder, class_1311 category, class_5483.class_1964 data) {
        return CbSpawnWeights.weightOf(data);
    }

    /**
     * BlockBehaviour.entityInside(state, level, pos, entity, effects): 1.21.4's bodies applied their effects to the
     * entity at once (fire, freezing, ... javap 1.21.4 class_4970 subclasses); 1.21.5 collects them per movement step.
     * The applier runs each effect, and what runs before / after it, immediately on the entity.
     */
    public static class_10774 immediate(class_2248 block, class_2680 state, class_1937 level, class_2338 pos, class_1297 entity) {
        return immediateFor(entity);
    }

    /** BlockState.entityInside(level, pos, entity, effects): as BlockBehaviour's */
    public static class_10774 immediateState(class_2680 state, class_1937 level, class_2338 pos, class_1297 entity) {
        return immediateFor(entity);
    }

    private static class_10774 immediateFor(class_1297 entity) {
        return new class_10774() {
            @Override
            public void method_67638(class_10776 type) {
                type.method_67646().accept(entity);
            }

            @Override
            public void method_67639(class_10776 type, Consumer<class_1297> before) {
                before.accept(entity);
                type.method_67646().accept(entity);
            }

            @Override
            public void method_67640(class_10776 type, Consumer<class_1297> after) {
                type.method_67646().accept(entity);
                after.accept(entity);
            }
        };
    }
}
