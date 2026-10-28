package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbEntityRest;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * FollowOwnerGoal (class_1350) and its teleport helpers. 1.20.6 kept them as private members of the goal: teleportToOwner
 * (method_23345), maybeTeleportTo (method_23343(III)Z), canTeleportTo (method_23344(BlockPos)Z), unableToMove
 * (method_49121()Z) and randomIntInclusive (method_23342(II)I). 1.21 moved them to TamableAnimal (class_1321:
 * tryToTeleportToOwner method_60713 -> teleportToAroundBlockPos method_60712 -> maybeToTeleportTo method_60711 ->
 * canTeleportTo method_60717, unableToMoveToOwner method_60715), where the goal's tick / canUse / canContinueToUse call them.
 * This mixin restores the five 1.20.6 members on the goal with 1.20.6's bodies (javap -c -p of both versions), and the
 * goal fields 1.21 dropped that those bodies and mods' accessors read: level (field_6445, set from the animal's level()
 * as 1.20.6's constructor did), canFly (field_21078) and the three teleport distances (field_30206..8, 2 / 3 / 1, the
 * values 1.20.6's javac inlined). canFly: a goal 1.21 code builds (no canFly argument) holds what the animal's
 * canFlyToOwner() answers, which is what 1.21's TamableAnimal.canTeleportTo reads (Parrot true, the rest false, as
 * 1.20.6's vanilla goals were built); a converted mod's 1.20.6 constructor call hands its argument over through the
 * step's constructor factory (CbEntityRest.setFollowCanFly, cb$setCanFly).
 * 1.21's calls go through them only when a converted mod's mixin injects into one of them (CbMixedMembers, smixin3 (l));
 * otherwise the goal runs 1.21's path untouched. This mixin holds only the restored members and fields, at
 * CbVanillaReads.PRIORITY so a mod's 1.20.6 injectors can anchor inside them; the routes in 1.21's own methods and the
 * constructor's field set are CbEntityFollowOwnerRestoredMixinHooks (default priority), which reaches them through
 * CbPrioritySplit.FollowOwner.
 */
@Mixin(value = net.minecraft.class_1350.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityFollowOwnerRestoredMixin implements CbEntityRest.LegacyFollowCanFly, CbPrioritySplit.FollowOwner {

    @Shadow
    @Final
    private net.minecraft.class_1321 field_6448;

    @Shadow
    private net.minecraft.class_1309 field_6444;

    @Shadow
    @Final
    private net.minecraft.class_1408 field_6446;

    /** 1.20.6 MIN_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING (mojang-1.20.1.txt; maybeTeleportTo's 2.0) */
    private static final int field_30206 = 2;
    /** 1.20.6 MAX_HORIZONTAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING (teleportToOwner's -3..3) */
    private static final int field_30207 = 3;
    /** 1.20.6 MAX_VERTICAL_DISTANCE_FROM_PLAYER_WHEN_TELEPORTING (teleportToOwner's -1..1) */
    private static final int field_30208 = 1;
    /** 1.20.6 FollowOwnerGoal.level (final there; set once, at construction) */
    private net.minecraft.class_4538 field_6445;
    /** 1.20.6 FollowOwnerGoal.canFly (final there; set at construction, see the class note) */
    private boolean field_21078;

    @Override
    public void cb$setCanFly(boolean canFly) {
        this.field_21078 = canFly;
    }

    @Override
    public void cb$v1_21$constructed(net.minecraft.class_4538 level, boolean canFly) {
        this.field_6445 = level;
        this.field_21078 = canFly;
    }

    @Override
    public void cb$v1_21$teleportToOwner() {
        this.method_23345();
    }

    @Override
    public boolean cb$v1_21$unableToMove() {
        return this.method_49121();
    }

    /** 1.20.6 FollowOwnerGoal.teleportToOwner: ten tries around the owner's block */
    @CbVanillaReads("CbEntityFollowOwnerRestoredMixinHooks: tick (method_6268) teleports through this method instead of 1.21's "
        + "TamableAnimal.tryToTeleportToOwner when a converted mod mixes it or the helpers it calls (cb$v1_21$teleport); body 1.20.6's")
    private void method_23345() {
        net.minecraft.class_2338 pos = this.field_6444.method_24515();
        for (int i = 0; i < 10; i++) {
            int x = this.method_23342(-3, 3);
            int y = this.method_23342(-1, 1);
            int z = this.method_23342(-3, 3);
            boolean done = this.method_23343(pos.method_10263() + x, pos.method_10264() + y, pos.method_10260() + z);
            if (done) {
                return;
            }
        }
    }

    /** 1.20.6 FollowOwnerGoal.maybeTeleportTo: not within two blocks of the owner, then canTeleportTo, then move */
    @CbVanillaReads("CbEntityFollowOwnerRestoredMixinHooks: reached from the restored teleportToOwner (method_23345), which tick "
        + "takes when a converted mod mixes this chain; body 1.20.6's")
    private boolean method_23343(int x, int y, int z) {
        if (Math.abs((double) x - this.field_6444.method_23317()) < 2.0 && Math.abs((double) z - this.field_6444.method_23321()) < 2.0) {
            return false;
        }
        if (!this.method_23344(new net.minecraft.class_2338(x, y, z))) {
            return false;
        }
        this.field_6448.method_5808((double) x + 0.5, (double) y, (double) z + 0.5, this.field_6448.method_36454(), this.field_6448.method_36455());
        this.field_6446.method_6340();
        return true;
    }

    /** 1.20.6 FollowOwnerGoal.canTeleportTo: a walkable spot, not onto leaves unless the animal flies, no collision */
    @CbVanillaReads("CbEntityFollowOwnerRestoredMixinHooks: reached from the restored maybeTeleportTo (method_23343) in the chain "
        + "tick takes when a converted mod mixes it; body 1.20.6's")
    private boolean method_23344(net.minecraft.class_2338 pos) {
        net.minecraft.class_7 type = net.minecraft.class_14.method_57625(this.field_6448, pos);
        if (type != net.minecraft.class_7.field_12) {
            return false;
        }
        net.minecraft.class_2680 below = this.field_6445.method_8320(pos.method_10074());
        if (!this.field_21078 && below.method_26204() instanceof net.minecraft.class_2397) {
            return false;
        }
        net.minecraft.class_2338 offset = pos.method_10059(this.field_6448.method_24515());
        // written as 1.20.6's bytecode has it (ifne / iconst_0 / iconst_1): mods' CONSTANT anchors count those returns
        if (!this.field_6445.method_8587(this.field_6448, this.field_6448.method_5829().method_996(offset))) {
            return false;
        }
        return true;
    }

    /** 1.20.6 FollowOwnerGoal.unableToMove: sitting, riding or leashed */
    @CbVanillaReads("CbEntityFollowOwnerRestoredMixinHooks: canUse (method_6264) and canContinueToUse (method_6266) ask this method "
        + "instead of 1.21's TamableAnimal.unableToMoveToOwner when a converted mod mixes it (cb$v1_21$unable); body 1.20.6's")
    private boolean method_49121() {
        return this.field_6448.method_24345() || this.field_6448.method_5765() || this.field_6448.method_60954();
    }

    /** 1.20.6 FollowOwnerGoal.randomIntInclusive */
    @CbVanillaReads("CbEntityFollowOwnerRestoredMixinHooks: reached from the restored teleportToOwner (method_23345); body 1.20.6's")
    private int method_23342(int min, int max) {
        // 1.20.6's LivingEntity.getRandom (method_6051) is Entity.getRandom (method_59922) in 1.21, same field
        return this.field_6448.method_59922().method_43048(max - min + 1) + min;
    }
}
