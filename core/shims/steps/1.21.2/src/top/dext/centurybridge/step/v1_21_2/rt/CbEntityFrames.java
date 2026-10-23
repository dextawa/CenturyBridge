package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1521;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_3218;
import net.minecraft.class_3419;
import net.minecraft.class_5147;
import net.minecraft.class_5354;

/**
 * frameDefaults of the s7entity override hooks whose missing argument has no entity receiver to read it off, or
 * is not a level (s7entity.json): what 1.21.1 used where 1.21.2 now passes an argument. Each is the value a mod's
 * own super call -- outside a call vanilla made through the hooked new shape -- had in 1.21.1.
 */
public final class CbEntityFrames {

    /** NeutralMob.isAngryAt(target) / playerDied(player): 1.21.1 read the other entity's level */
    public static class_3218 levelOfTarget(class_5354 self, class_1309 target) {
        return CbEntityLevels.serverLevel(target);
    }

    public static class_3218 levelOfPlayer(class_5354 self, class_1657 player) {
        return CbEntityLevels.serverLevel(player);
    }

    /** Shearable.shear(source): the sheared entity's level (every vanilla Shearable is an entity) */
    public static class_3218 levelOfShearable(class_5147 self) {
        return self instanceof class_1297 e ? CbEntityLevels.serverLevel(e) : null;
    }

    /**
     * Shearable.shear(source): 1.21.2's tool. 1.21.1's shear had none; every 1.21.1 caller was a shears use
     * (ShearsItem.interactLivingEntity, the dispenser's ShearsDispenseItemBehavior), and 1.21.2's shearing loot
     * tables read the tool only through match_tool conditions, which vanilla's do not have: a stack of shears is
     * what 1.21.1's own callers held.
     */
    public static class_1799 shears(class_5147 self) {
        return new class_1799(class_1802.field_8868);
    }

    /** DragonPhaseInstance.doServerTick(): the phase's dragon's level */
    public static class_3218 levelOfPhase(class_1521 self) {
        return CbEntityLevels.dragonLevel(self) instanceof class_3218 sl ? sl : null;
    }

    /** NearestVisibleLivingEntitySensor.isMatchingEntity(entity, target): the sensing entity's level */
    public static class_3218 levelOfSensing(net.minecraft.class_6045 self, class_1309 entity, class_1309 target) {
        return CbEntityLevels.serverLevel(entity);
    }

    /**
     * Mob.canReplaceCurrentItem(candidate, existing): 1.21.2's slot. 1.21.1's compared attack damage and armor of
     * the two stacks for the slot the candidate goes to (equipItemIfPossible's getEquipmentSlotForItem): that slot.
     */
    public static class_1304 slotOf(class_1308 self, class_1799 candidate, class_1799 existing) {
        return self.method_32326(candidate);
    }

    /**
     * Player.disableShield(): 1.21.2's stack is the shield being blocked with (blockUsingShield passes
     * getItemBlockingWith()), whose item gets the cooldown. 1.21.1 put the cooldown on Items.SHIELD: a stack of it.
     */
    public static class_1799 shield() {
        return new class_1799(class_1802.field_8255);
    }

    /**
     * Entity.teleportTo(level, x, y, z, relatives, yRot, xRot): 1.21.2's last argument resets a teleported
     * ServerPlayer's camera to itself (TeleportCommand and SpreadPlayersCommand pass true, ServerPlayer's own
     * calls false). 1.21.1's teleportTo never touched the camera: false.
     */
    public static boolean keepCamera() {
        return false;
    }

    /**
     * EntityType.create(level): 1.21.2's spawn reason. 1.21.2's EntityType.create(level, reason) does not read
     * it (it is handed to nothing: the factory takes the type and the level); 1.21.1 had none. TRIGGERED is what
     * 1.21.2's own callers of that kind pass (entities created by game logic: SkeletonTrapGoal, ThrownEgg,
     * golem construction, the infested and oozing effects).
     */
    public static net.minecraft.class_3730 createReason() {
        return net.minecraft.class_3730.field_16461;
    }

    /**
     * EntityType.create(tag, level) / loadEntityRecursive / loadEntitiesRecursive: 1.21.2's spawn reason for an
     * entity read from saved data. LOAD is what 1.21.2 passes wherever 1.21.1 called these to load an entity
     * (ChunkStatusTasks, Player's shoulder entities, the beehive occupants).
     */
    public static net.minecraft.class_3730 loadReason() {
        return net.minecraft.class_3730.field_52444;
    }

    /** a mod's call of 1.21.1 Mob.canReplaceCurrentItem(candidate, existing): 1.21.2's for the candidate's slot */
    public static boolean canReplaceCurrentItem(class_1308 mob, class_1799 candidate, class_1799 existing) {
        return ((top.dext.centurybridge.step.v1_21_2.mixin.CbEntityMobInvoker) mob).cb$canReplaceCurrentItem(candidate, existing,
            slotOf(mob, candidate, existing));
    }

    private CbEntityFrames() {
    }
}
