package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 1.21.11's two-step entity interaction on 26.1's one-step one.
 * <p>
 * 1.21.11: a right click on an entity was first {@code entity.interactAt(player, location, hand)} (its own
 * packet, INTERACT_AT; Entity's answered PASS, ArmorStand's placed and took equipment), and only when that
 * did not consume the action {@code player.interactOn(entity, hand)} (packet INTERACT), which asked
 * {@code entity.interact(player, hand)} and then the held item. 26.1 sends one packet and calls
 * {@code player.interactOn(entity, hand, location)} -> {@code entity.interact(player, hand, location)};
 * ArmorStand's interact is its old interactAt whose PASS answers fall through to LivingEntity's interact.
 * <p>
 * For an entity of a 1.21.11 class (one declaring the old interactAt or interact below Entity), 26.1's
 * interactOn first asks its interactAt and returns that answer when it consumes the action
 * (mixin/CbWallInteractPlayerMixin), and an ArmorStand of such a class answers interact as 1.21.11's
 * ArmorStand did -- LivingEntity's -- since the armor stand step already ran as interactAt
 * (mixin/CbWallInteractArmorStandMixin). Entities of 26.1's own classes keep 26.1's behaviour.
 * <p>
 * A mod's own call of the old {@code entity.interact(player, hand)} or {@code player.interactOn(entity, hand)}
 * is 1.21.11's second step alone: no interactAt, and an armor stand answers LivingEntity's interact.
 */
public final class CbWallInteract {

    private CbWallInteract() {
    }

    /** the entity a mod's old interact / interactOn call is running for on this thread (1.21.11's second step alone) */
    private static final ThreadLocal<Object> SECOND_STEP_ONLY = new ThreadLocal<>();
    /** the entity whose interactAt already ran on this thread for the interactOn running now (client, see CbWallInteractClient) */
    private static final ThreadLocal<Object> AT_DONE = new ThreadLocal<>();

    private static final ClassValue<Boolean> OLD_CLASS = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != Entity.class; c = c.getSuperclass()) {
                if (c == ArmorStand.class) {
                    continue; // the layer's own interactAt (1.21.11's ArmorStand)
                }
                if (declares(c, "interactAt", Player.class, Vec3.class, InteractionHand.class)
                    || declares(c, "interact", Player.class, InteractionHand.class)) {
                    return true;
                }
            }
            return false;
        }
    };

    private static boolean declares(Class<?> c, String name, Class<?>... params) {
        try {
            c.getDeclaredMethod(name, params);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /** an entity of a 1.21.11 class: it declares the old interactAt or interact somewhere below Entity */
    public static boolean oldClass(Entity entity) {
        return OLD_CLASS.get(entity.getClass());
    }

    /** ArmorStand.interact: answer 1.21.11's -- LivingEntity's -- because the armor stand step was (or is not) interactAt */
    public static boolean armorStandSecondStep(ArmorStand stand) {
        return SECOND_STEP_ONLY.get() == stand || oldClass(stand);
    }

    /**
     * Player.interactOn(entity, hand, location) entered: 1.21.11's interactAt step for an entity of a 1.21.11
     * class, or null to go on. A spectator's interactAt ran on the server (1.21.11's client sent INTERACT_AT
     * and answered PASS itself, so INTERACT followed whatever the server's said); a client never asked it.
     */
    public static InteractionResult firstStep(Player player, Entity entity, InteractionHand hand, Vec3 location) {
        if (SECOND_STEP_ONLY.get() == entity || !oldClass(entity)) {
            return null;
        }
        if (AT_DONE.get() == entity) {
            AT_DONE.remove();
            return null;
        }
        if (player.isSpectator()) {
            if (!player.level().isClientSide()) {
                ((CbWallInteractAt) entity).interactAt(player, location, hand);
            }
            return null;
        }
        InteractionResult result = ((CbWallInteractAt) entity).interactAt(player, location, hand);
        return result.consumesAction() ? result : null;
    }

    /** the client's own interactAt step already ran for this entity: the interactOn that follows skips it */
    public static void atDone(Entity entity) {
        AT_DONE.set(entity);
    }

    /** the client's interactOn is over: whatever it did not consume is stale */
    public static void atDoneClear() {
        AT_DONE.remove();
    }

    /** a mod's call of 1.21.11's entity.interact(player, hand) */
    public static InteractionResult interact(Entity entity, Player player, InteractionHand hand) {
        Object outer = SECOND_STEP_ONLY.get();
        SECOND_STEP_ONLY.set(entity);
        try {
            return entity.interact(player, hand, location(entity, player, hand));
        } finally {
            SECOND_STEP_ONLY.set(outer);
        }
    }

    /** a mod's call of 1.21.11's player.interactOn(entity, hand) */
    public static InteractionResult interactOn(Player player, Entity entity, InteractionHand hand) {
        Object outer = SECOND_STEP_ONLY.get();
        SECOND_STEP_ONLY.set(entity);
        try {
            return player.interactOn(entity, hand, location(entity, player, hand));
        } finally {
            SECOND_STEP_ONLY.set(outer);
        }
    }

    /**
     * Where on the entity the player interacts, relative to the entity's position -- what 26.1's client puts
     * in the packet (the hit location minus the entity's position) -- for a call that has no hit: the point
     * where the player's view meets the entity's pick box, else the box point nearest the player's eyes
     * (the eyes themselves inside it). Also the frameDefaults of the old interact hook (the receiver and
     * the old arguments).
     */
    public static Vec3 location(Entity entity, Player player, InteractionHand hand) {
        AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
        Vec3 eye = player.getEyePosition();
        Vec3 hit;
        if (box.contains(eye)) {
            hit = eye;
        } else {
            double reach = eye.distanceTo(box.getCenter()) + box.getSize();
            hit = box.clip(eye, eye.add(player.getViewVector(1.0F).scale(reach)))
                .orElseGet(() -> new Vec3(Mth.clamp(eye.x, box.minX, box.maxX), Mth.clamp(eye.y, box.minY, box.maxY),
                    Mth.clamp(eye.z, box.minZ, box.maxZ)));
        }
        return hit.subtract(entity.getX(), entity.getY(), entity.getZ());
    }

    /** frameDefaults of the old Player.interactOn hook: {@link #location} of the entity as this player sees it */
    public static Vec3 targetLocation(Player player, Entity entity, InteractionHand hand) {
        return location(entity, player, hand);
    }
}
