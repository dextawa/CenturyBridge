package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1512;
import net.minecraft.class_1521;
import net.minecraft.class_1657;
import net.minecraft.class_1688;
import net.minecraft.class_1696;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_4836;
import net.minecraft.class_7265;
import net.minecraft.class_9817;

/**
 * The hand-written half of the "threaded level" family (core/tools/gen_s7entity.py generates the rest, in
 * {@link CbEntityLevelsGen}): 1.21.2 threads the entity's ServerLevel through members that 1.21.1 ran on the
 * entity's own level. Here: the level that frameDefaults and the bridges hand to 1.21.2, and the client-level
 * halves of the 1.21.1 bodies that did more than a constant on a client level.
 */
public final class CbEntityLevels {

    /**
     * The level 1.21.1's body ran on, as the ServerLevel 1.21.2's takes. The 1.21.2 shapes threaded here all run
     * on the server, so an entity reaching one has a ServerLevel. A client level is answered with null: a 1.21.2
     * body reading it then fails, as 1.21.1's server-only code did when it cast a client level.
     */
    public static class_3218 serverLevel(class_1297 entity) {
        return entity.method_37908() instanceof class_3218 sl ? sl : null;
    }

    /** a dragon phase's level: its dragon's (1.21.1's phases read dragon.level()) */
    public static class_1937 dragonLevel(class_1521 phase) {
        if (phase instanceof class_1512 p) {
            return ((top.dext.centurybridge.step.v1_21_2.mixin.CbEntityDragonPhaseAccess) p).cb$dragon().method_37908();
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------ client halves
    /**
     * 1.21.1 AbstractMinecart.getMaxSpeed() on a client level: (isInWater ? 4 : 8) / 20; MinecartFurnace's
     * override (isInWater ? 3 : 4) / 20. 1.21.2 asks the minecart behaviour, which reads the server level's
     * minecart speed rule.
     */
    public static double method_7504OnClient(class_1688 cart) {
        if (cart instanceof class_1696) {
            return (cart.method_5799() ? 3.0 : 4.0) / 20.0;
        }
        return (cart.method_5799() ? 4.0 : 8.0) / 20.0;
    }

    /**
     * 1.21.1 ContainerEntity.chestVehicleDestroyed(source, level, entity) on a client level: the contents went
     * to Containers.dropContents, whose item entities a client level does not add (LevelWriter.addFreshEntity's
     * default answers false, ClientLevel does not override it), and the piglin anger was server-only. Nothing
     * observable happened.
     */
    public static void method_42283OnClient(class_7265 container, class_1282 source, class_1937 level, class_1297 entity) {
    }

    /**
     * 1.21.1 Leashable.tickLeash(entity) on a client level: Entity.baseTick ticked it on both sides; 1.21.2 ticks
     * the leash on the server only. The 1.21.1 body's restore from save needed a ServerLevel (it did nothing on a
     * client), its drop on a dead holder is dropLeash, and the distance behaviour is the Leashable's own methods.
     */
    public static void method_60965OnClient(class_1297 entity) {
        class_9817 leashable = (class_9817) entity;
        class_9817.class_9818 data = leashable.method_60955();
        if (data == null || data.field_52217 == null) {
            return;
        }
        if (!entity.method_5805() || !data.field_52217.method_5805()) {
            leashable.method_5932(true, true);
        }
        class_1297 holder = leashable.method_60952();
        if (holder != null && holder.method_37908() == entity.method_37908()) {
            float d = entity.method_5739(holder);
            if (!leashable.method_60956(holder, d)) {
                return;
            }
            if (d > 10.0) {
                leashable.method_60970();
            } else if (d > 6.0) {
                leashable.method_61162(holder, d);
                entity.method_45318();
            } else {
                leashable.method_60963(holder);
            }
        }
    }

    /**
     * 1.21.1 PiglinAi.mobInteract(piglin, player, hand): consume one of an admired item into the off hand (the
     * previous one spawned at the piglin, which a client level does not add), set ADMIRING_ITEM for 119 ticks,
     * stop walking and answer CONSUME; otherwise PASS. 1.21.1's Piglin.mobInteract called it on the server only,
     * but the body itself had no side test: the same on a client level, minus the spawned item.
     */
    public static class_1269 method_24728OnClient(class_4836 piglin, class_1657 player, class_1268 hand) {
        class_1799 stack = player.method_5998(hand);
        if (!top.dext.centurybridge.step.v1_21_2.mixin.CbEntityPiglinAiInvoker.cb$canAdmire(piglin, stack)) {
            return class_1269.field_5811;
        }
        class_1799 one = stack.method_60504(1, player);
        ((top.dext.centurybridge.step.v1_21_2.mixin.CbEntityPiglinInvoker) piglin).cb$holdInOffHand(one);
        piglin.method_18868().method_24525(net.minecraft.class_4140.field_22334, true, 119L);
        piglin.method_18868().method_18875(net.minecraft.class_4140.field_18445);
        piglin.method_5942().method_6340();
        return class_1269.field_21466;
    }

    private CbEntityLevels() {
    }
}
