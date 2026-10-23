package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1688;
import net.minecraft.class_1693;
import net.minecraft.class_1694;
import net.minecraft.class_1695;
import net.minecraft.class_1696;
import net.minecraft.class_1697;
import net.minecraft.class_1699;
import net.minecraft.class_1700;
import net.minecraft.class_1701;
import net.minecraft.class_1799;
import net.minecraft.class_3218;
import top.dext.centurybridge.step.v1_21_2.facade.MinecartType;

/**
 * 1.21.1's minecart kinds and constructors on 1.21.2.
 *
 * <p>1.21.1 named a minecart's kind by AbstractMinecart.Type (facade/MinecartType): getMinecartType(), abstract,
 * each vanilla class answering its own constant, and createMinecart(level, x, y, z, type, stack, player)
 * constructing the class of that kind. 1.21.2 dropped the enum; the kind is the EntityType, which every kind
 * already had. 1.21.1's (level, x, y, z) constructors of the vanilla minecarts were
 * super(EntityType.&lt;KIND&gt;_MINECART, x, y, z, level), whose AbstractMinecart body was this(type, level) then
 * setPos and xo/yo/zo -- 1.21.2's setInitialPos, same body.
 */
public final class CbEntityMinecarts {

    /** a mod minecart class's own 1.21.1 getMinecartType (adapter hook) */
    public interface LegacyType {
        MinecartType method_7518();
    }

    /** 1.21.1 AbstractMinecart.getMinecartType(), called: the mod class's own, else the vanilla class's constant */
    public static MinecartType getMinecartType(class_1688 cart) {
        if (cart instanceof LegacyType legacy) {
            return legacy.method_7518();
        }
        if (cart instanceof class_1694) {
            return MinecartType.CHEST;
        }
        if (cart instanceof class_1696) {
            return MinecartType.FURNACE;
        }
        if (cart instanceof class_1701) {
            return MinecartType.TNT;
        }
        if (cart instanceof class_1699) {
            return MinecartType.SPAWNER;
        }
        if (cart instanceof class_1700) {
            return MinecartType.HOPPER;
        }
        if (cart instanceof class_1697) {
            return MinecartType.COMMAND_BLOCK;
        }
        if (cart instanceof class_1695) {
            return MinecartType.RIDEABLE;
        }
        // a 1.21.2 minecart class of another mod: 1.21.1 had no constant for it
        return null;
    }

    /** 1.21.1 AbstractMinecart.createMinecart(level, x, y, z, type, stack, player) */
    public static class_1688 createMinecart(class_3218 level, double x, double y, double z, MinecartType type,
                                            class_1799 stack, class_1657 player) {
        return class_1688.method_7523(level, x, y, z, type.entityType(), CbEntityFrames.createReason(), stack, player);
    }

    // ------------------------------------------------------------------------------------------ constructors

    public static class_1299<class_1695> minecart() {
        return class_1299.field_6096;
    }

    public static class_1299<class_1694> chest() {
        return class_1299.field_6126;
    }

    public static class_1299<class_1696> furnace() {
        return class_1299.field_6080;
    }

    public static class_1299<class_1701> tnt() {
        return class_1299.field_6053;
    }

    public static class_1299<class_1699> spawner() {
        return class_1299.field_6142;
    }

    public static class_1299<class_1700> hopper() {
        return class_1299.field_6058;
    }

    public static class_1299<class_1697> commandBlock() {
        return class_1299.field_6136;
    }

    /** after the (type, level) constructor: 1.21.1's setPos(x, y, z) and xo/yo/zo, 1.21.2's setInitialPos */
    public static void at(class_1695 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1694 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1696 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1701 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1699 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1700 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1697 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    public static void at(class_1693 cart, double x, double y, double z) {
        cart.method_64275(x, y, z);
    }

    private CbEntityMinecarts() {
    }
}
