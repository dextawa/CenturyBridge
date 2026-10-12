package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1314;
import net.minecraft.class_1321;
import net.minecraft.class_1433;
import net.minecraft.class_1493;
import net.minecraft.class_1496;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_5762;
import net.minecraft.class_7689;
import net.minecraft.class_9817;

/**
 * Leashes: 1.20.6 kept them on Mob (leashHolder, delayedLeashInfo; tickLeash, canBeLeashed(Player), and in
 * PathfinderMob the distance behaviour around onLeashDistance(float)); 1.21 moved them to the Leashable interface
 * (its LeashData, the static Leashable.tickLeash called from Entity.baseTick, canBeLeashed() with
 * canHaveALeashAttachedToIt(), and the distance behaviour split into handleLeashAtDistance /
 * leashTooFarBehaviour / elasticRangeLeashBehaviour / closeRangeLeashBehaviour). The members that only moved are
 * renamed in s6world.json (getLeashHolder, isLeashed, setLeashedTo, setDelayedLeashHolderId, mayBeLeashed); the
 * ones whose shape changed land here. A 1.20.6 subclass of a mod that overrides one of them gets the interface
 * of that shape (adapterHooks / bridge hooks), so 1.21's calls reach it and 1.20.6's calls answer through it.
 */
public final class CbEntityLeash {

    // ------------------------------------------------------------------------------------------------ canBeLeashed
    /** the 1.20.6 canBeLeashed(Player) a 1.20.6 subclass of a mod overrides */
    public interface LegacyCanBeLeashed {
        boolean method_5931(class_1657 player);
    }

    /** the player of the interaction in progress on an entity (CbEntityLeashMixin, Entity.interact) */
    public interface InteractionContext {
        class_1657 cb$interactingPlayer();
    }

    /**
     * 1.20.6 mob.canBeLeashed(player). A 1.20.6 subclass of a mod answers itself. A vanilla class answers what its
     * 1.20.6 body answered: Mob's was {@code !isLeashed() && !(this instanceof Enemy)}, and every vanilla
     * override either kept the leash test (TamableAnimal, Wolf through it, Squid, Hoglin, Zoglin: 1.21 dropped
     * it from canBeLeashed() and asks it in canHaveALeashAttachedToIt()) or answered a constant (1.21's
     * canBeLeashed() answers the same one). 1.21's canHaveALeashAttachedToIt() -- canBeLeashed() &&
     * !isLeashed() -- is therefore the 1.20.6 answer, except for the two constants that were true however
     * leashed the mob already was: Dolphin and Axolotl (gen_s6world.py reads this split off 1.20.6's bytecode).
     */
    public static boolean canBeLeashed(class_1308 mob, class_1657 player) {
        if (mob instanceof LegacyCanBeLeashed legacy) {
            return legacy.method_5931(player);
        }
        if (mob instanceof class_1433 || mob instanceof class_5762) {
            return mob.method_5931();
        }
        return mob.method_60969();
    }

    /**
     * 1.21 asks canBeLeashed() of a 1.20.6 subclass that overrides canBeLeashed(Player): 1.20.6 asked it with the
     * player holding the lead, which 1.21 only has in Entity.interact -- the one place 1.21 asks (through
     * canHaveALeashAttachedToIt()), as 1.20.6 asked in Mob's interaction. Outside an interaction there is no
     * player to hand over (1.20.6 never asked without one).
     */
    public static boolean canBeLeashed(LegacyCanBeLeashed self) {
        class_1657 player = self instanceof InteractionContext context ? context.cb$interactingPlayer() : null;
        return self.method_5931(player);
    }

    /** a 1.20.6 super.canBeLeashed(player) landing on Mob's: !isLeashed() && !(this instanceof Enemy) */
    public static boolean superCanBeLeashed(class_1308 mob, class_1657 player) {
        return !mob.method_60953() && !(mob instanceof class_1569);
    }

    /** a 1.20.6 super.canBeLeashed(player) landing on TamableAnimal's: !isLeashed() */
    public static boolean superCanBeLeashed(class_1321 animal, class_1657 player) {
        return !animal.method_60953();
    }

    /** a 1.20.6 super.canBeLeashed(player) landing on Wolf's: !isAngry() && TamableAnimal's */
    public static boolean superCanBeLeashed(class_1493 wolf, class_1657 player) {
        return !wolf.method_29511() && !wolf.method_60953();
    }

    // The other 1.20.6 bodies, for a super call landing on them (gen_s6world.py checks each against 1.20.6's
    // bytecode): a constant, or Squid's, Zoglin's and Hoglin's !isLeashed(). 1.21's canBeLeashed() of these
    // classes is the same constant, or true where 1.20.6 tested the leash.

    /** AmbientCreature: false */
    public static boolean superCanBeLeashed(net.minecraft.class_1421 mob, class_1657 player) {
        return false;
    }

    /** Dolphin: true */
    public static boolean superCanBeLeashed(class_1433 mob, class_1657 player) {
        return true;
    }

    /** Panda: false */
    public static boolean superCanBeLeashed(net.minecraft.class_1440 mob, class_1657 player) {
        return false;
    }

    /** Squid: !isLeashed() */
    public static boolean superCanBeLeashed(net.minecraft.class_1477 mob, class_1657 player) {
        return !mob.method_60953();
    }

    /** Turtle: false */
    public static boolean superCanBeLeashed(net.minecraft.class_1481 mob, class_1657 player) {
        return false;
    }

    /** WaterAnimal: false */
    public static boolean superCanBeLeashed(net.minecraft.class_1480 mob, class_1657 player) {
        return false;
    }

    /** Axolotl: true */
    public static boolean superCanBeLeashed(class_5762 mob, class_1657 player) {
        return true;
    }

    /** Zoglin: !isLeashed() */
    public static boolean superCanBeLeashed(net.minecraft.class_5136 mob, class_1657 player) {
        return !mob.method_60953();
    }

    /** Hoglin: !isLeashed() */
    public static boolean superCanBeLeashed(net.minecraft.class_4760 mob, class_1657 player) {
        return !mob.method_60953();
    }

    /** AbstractVillager: false */
    public static boolean superCanBeLeashed(net.minecraft.class_3988 mob, class_1657 player) {
        return false;
    }

    // ------------------------------------------------------------------------------------------------ tickLeash
    /** the 1.20.6 tickLeash() a 1.20.6 subclass of a mod overrides */
    public interface LegacyTickLeash {
        void method_5995();
    }

    /**
     * Entity.baseTick's Leashable.tickLeash(entity) (CbEntityLeashMixin): a 1.20.6 subclass that overrides
     * tickLeash() ticks its leash itself, as 1.20.6's Mob.tick called this.tickLeash(); anything else 1.21's.
     */
    public static void baseTickLeash(class_1297 entity) {
        if (entity instanceof LegacyTickLeash legacy) {
            legacy.method_5995();
            return;
        }
        class_9817.method_60965((class_1297 & class_9817) entity);
    }

    /** 1.20.6 mob.tickLeash(), protected: the mob's own (a 1.20.6 override) or 1.20.6's vanilla body */
    public static void tickLeash(class_1308 mob) {
        if (mob instanceof LegacyTickLeash legacy) {
            legacy.method_5995();
        } else if (mob instanceof class_1314 pathfinder) {
            superTickLeash(pathfinder);
        } else {
            superTickLeash(mob);
        }
    }

    /**
     * 1.20.6 Mob.tickLeash(): restore a leash read from the save, drop it when either end is dead. Nothing more:
     * the distance behaviour was PathfinderMob's.
     */
    public static void superTickLeash(class_1308 mob) {
        class_9817.class_9818 data = mob.method_60955();
        if (data != null && data.field_52218 != null) {
            restoreLeashFromSave(mob, data);
        }
        class_1297 holder = mob.method_60952();
        if (holder != null && (!mob.method_5805() || !holder.method_5805())) {
            mob.method_5932(true, true);
        }
    }

    /**
     * 1.20.6 Mob.restoreLeashFromSave(), on 1.21's LeashData: the holder saved by uuid (found in the level) or the
     * fence knot saved by position, leashed with setLeashedTo -- the mob's own, a 1.20.6 override included, as
     * 1.20.6 called it; after 100 ticks without a holder the lead drops and the saved leash is forgotten.
     */
    private static void restoreLeashFromSave(class_1308 mob, class_9817.class_9818 data) {
        if (!(mob.method_37908() instanceof net.minecraft.class_3218 level)) {
            return;
        }
        java.util.Optional<java.util.UUID> uuid = data.field_52218.left();
        java.util.Optional<net.minecraft.class_2338> pos = data.field_52218.right();
        if (uuid.isPresent()) {
            class_1297 holder = level.method_14190(uuid.get());
            if (holder != null) {
                mob.method_60964(holder, true);
                return;
            }
        } else if (pos.isPresent()) {
            mob.method_60964(net.minecraft.class_1532.method_6932(level, pos.get()), true);
            return;
        }
        if (mob.field_6012 > 100) {
            mob.method_5706(net.minecraft.class_1802.field_8719);
            mob.method_60960(null);
        }
    }

    /**
     * 1.20.6 PathfinderMob.tickLeash(): Mob's, then the distance behaviour -- which is what 1.21's
     * Leashable.tickLeash does for a pathfinder mob, the 1.20.6 steps in the same order: restrictTo the
     * holder, the sitting tamable's stop, onLeashDistance (handleLeashAtDistance), beyond 10 drop and release
     * the move goals (leashTooFarBehaviour), beyond 6 the elastic pull, else follow (closeRangeLeashBehaviour).
     */
    public static void superTickLeash(class_1314 mob) {
        class_9817.method_60965(mob);
    }

    // ------------------------------------------------------------------------------------------------ onLeashDistance
    /** the 1.20.6 onLeashDistance(float) a 1.20.6 subclass of a mod overrides */
    public interface LegacyLeashDistance {
        void method_6142(float distance);
    }

    /**
     * 1.21 asks handleLeashAtDistance(holder, distance) of a 1.20.6 subclass that overrides
     * onLeashDistance(distance). 1.20.6's PathfinderMob.tickLeash ran, in this order: restrictTo the holder's
     * position within 5; a sitting tamable dropped the leash beyond 10 and went no further; onLeashDistance;
     * then the distance behaviour. That is this answer: true goes on to the distance behaviour, as 1.21 reads
     * it. The vanilla onLeashDistance bodies the override replaced (AbstractHorse stops eating, the camel stands
     * up -- 1.21 put them in their handleLeashAtDistance) run where the override calls super.
     */
    public static boolean handleLeashAtDistance(LegacyLeashDistance self, class_1297 holder, float distance) {
        class_1314 mob = (class_1314) self;
        mob.method_18408(holder.method_24515(), 5);
        if (mob instanceof class_1321 tamable && tamable.method_6172()) {
            if (distance > 10.0F) {
                mob.method_5932(true, true);
            }
            return false;
        }
        self.method_6142(distance);
        return true;
    }

    /** 1.20.6 mob.onLeashDistance(distance), protected: the mob's own (a 1.20.6 override) or 1.20.6's vanilla body */
    public static void onLeashDistance(class_1314 mob, float distance) {
        if (mob instanceof LegacyLeashDistance legacy) {
            legacy.method_6142(distance);
        } else if (mob instanceof class_7689 camel) {
            superOnLeashDistance(camel, distance);
        } else if (mob instanceof class_1496 horse) {
            superOnLeashDistance(horse, distance);
        }
    }

    /** a 1.20.6 super.onLeashDistance(distance) landing on PathfinderMob's: nothing */
    public static void superOnLeashDistance(class_1314 mob, float distance) {
    }

    /** a 1.20.6 super.onLeashDistance(distance) landing on AbstractHorse's: stop eating beyond 6 */
    public static void superOnLeashDistance(class_1496 horse, float distance) {
        if (distance > 6.0F && horse.method_6724()) {
            horse.method_6740(false);
        }
    }

    /** a 1.20.6 super.onLeashDistance(distance) landing on Camel's: a sitting camel stands up beyond 6 */
    public static void superOnLeashDistance(class_7689 camel, float distance) {
        if (distance > 6.0F && camel.method_45350() && !camel.method_45351() && camel.method_52548()) {
            camel.method_45353();
        }
    }

    private CbEntityLeash() {
    }
}
