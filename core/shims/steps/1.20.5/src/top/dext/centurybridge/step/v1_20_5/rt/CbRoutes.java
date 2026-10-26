package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Support for this layer's routes: the places 1.20.5's own code does what a restored 1.20.4 member did ask that
 * member again, only when a converted mod's mixin injects into it (CbMixedMembers, smixin3 (l)). Unmixed, 1.20.5's
 * path runs untouched, so native 1.20.5 mixins on it fire as without the layer. The keys are the restored members as
 * the layer declares them ("owner.name(desc)").
 * <p>
 * A route that calls a restored member whose body comes back into the routed 1.20.5 method marks the object for the
 * call ({@link #enter} / {@link #exit}); the routed method then runs as 1.20.5 has it.
 */
public final class CbRoutes {

    private CbRoutes() {
    }

    public static final String INVENTORY_HURT_ARMOR = "net/minecraft/class_1661.method_7375(Lnet/minecraft/class_1282;F[I)V";
    public static final String ARROW_SET_EFFECTS_FROM_ITEM = "net/minecraft/class_1667.method_7459(Lnet/minecraft/class_1799;)V";
    public static final String PIGLIN_MAKE_SOUND = "net/minecraft/class_4836.method_30086(Lnet/minecraft/class_3414;)V";
    public static final String LIVING_BROADCAST_BREAK_HAND = "net/minecraft/class_1309.method_20236(Lnet/minecraft/class_1268;)V";
    public static final String FIREBALL_GET_ITEM_RAW = "net/minecraft/class_3855.method_16938()Lnet/minecraft/class_1799;";
    public static final String THROWABLE_GET_ITEM_RAW = "net/minecraft/class_3857.method_16943()Lnet/minecraft/class_1799;";
    public static final String WALK_BOX_TYPES = "net/minecraft/class_14.method_64(Lnet/minecraft/class_1922;IIILjava/util/EnumSet;Lnet/minecraft/class_7;Lnet/minecraft/class_2338;)Lnet/minecraft/class_7;";
    public static final String WALK_EVALUATE = "net/minecraft/class_14.method_61(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_7;)Lnet/minecraft/class_7;";
    public static final String SWIM_DIAGONAL = "net/minecraft/class_12.method_38488(Lnet/minecraft/class_9;Lnet/minecraft/class_9;Lnet/minecraft/class_9;)Z";
    public static final String GOALS_RUNNING = "net/minecraft/class_1355.method_19048()Ljava/util/stream/Stream;";
    public static final String ARROW_DEFLECT = "net/minecraft/class_1665.method_55052()V";

    public static final String BUF_WRITE_ITEM = "net/minecraft/class_2540.method_10793(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2540;";
    public static final String BUF_READ_ITEM = "net/minecraft/class_2540.method_10819()Lnet/minecraft/class_1799;";

    private static final ThreadLocal<Set<Object>> IN_RESTORED =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    /** how deep this thread is inside restored FriendlyByteBuf members (their own stream codec calls run as 1.20.5's) */
    private static final ThreadLocal<int[]> IN_IO = ThreadLocal.withInitial(() -> new int[1]);

    /**
     * whether a 1.20.5 stream codec should ask the restored FriendlyByteBuf member now: mixed, and this thread is not
     * inside a restored buffer member (whose own codec calls, and the codec calls of values nested in what it writes,
     * run as 1.20.5 has them -- 1.20.4 wrote nested values as NBT, not through these members)
     */
    public static boolean routeIo(String key) {
        return CbMixedMembers.mixed(key) && IN_IO.get()[0] == 0;
    }

    public static void enterIo() {
        IN_IO.get()[0]++;
    }

    public static void exitIo() {
        IN_IO.get()[0]--;
    }

    /** whether the route should ask the restored member for this object now (mixed, and not already inside it) */
    public static boolean route(String key, Object self) {
        return CbMixedMembers.mixed(key) && !IN_RESTORED.get().contains(self);
    }

    public static boolean enter(Object self) {
        return IN_RESTORED.get().add(self);
    }

    public static void exit(Object self) {
        IN_RESTORED.get().remove(self);
    }

    /** the restored 1.20.4 Inventory.hurtArmor, for the Player route */
    public interface InventoryHurtArmor {
        void method_7375(net.minecraft.class_1282 source, float damage, int[] slots);
    }

    /** the restored 1.20.4 Arrow.setEffectsFromItem, for the Arrow route */
    public interface ArrowEffectsFromItem {
        void method_7459(net.minecraft.class_1799 stack);
    }

    /** the restored 1.20.4 LivingEntity.broadcastBreakEvent(hand), for the break route */
    public interface BreakHand {
        void method_20236(net.minecraft.class_1268 hand);
    }

    /** the restored 1.20.4 GoalSelector.getRunningGoals, for the PathfinderMob.isPanicking route */
    public interface RunningGoals {
        java.util.stream.Stream<net.minecraft.class_4135> method_19048();
    }

    /** the restored 1.20.4 AbstractArrow.deflect, for the Projectile.hitTargetOrDeflectSelf route */
    public interface ArrowDeflect {
        void method_55052();
    }

    /** the restored 1.20.4 FriendlyByteBuf members the stream codec routes ask (CbNetFriendlyByteBufBridge) */
    public interface LegacyBuf {
        net.minecraft.class_2540 method_10793(net.minecraft.class_1799 stack);

        net.minecraft.class_1799 method_10819();
    }

    /**
     * Priority of the route mixins: below a mod's (1000), so a route's cancellable HEAD callback runs before a native
     * 1.20.5 mod's HEAD callback on the same method. When the route takes over (mixed) the native callback then runs
     * once, inside the restored member's call back into the 1.20.5 method, not twice.
     */
    public static final int ROUTE_PRIORITY = 500;
}
