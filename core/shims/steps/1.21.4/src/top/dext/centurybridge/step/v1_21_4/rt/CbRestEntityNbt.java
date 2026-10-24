package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1540;
import net.minecraft.class_1697;
import net.minecraft.class_1699;

/**
 * 1.21.3's Entity.onlyOpCanSetNbt(): an overridable per-entity answer (Entity false; FallingBlockEntity,
 * MinecartCommandBlock, MinecartSpawner true), gone in 1.21.4, which asks EntityType.onlyOpCanSetNbt()
 * (a fixed set of types holding those three). A mod call answers what 1.21.3's virtual call did: a 1.21.3
 * subclass's own override when it has one ({@link OnlyOp}, adapterHooks), otherwise the type's answer,
 * which for vanilla's entities is 1.21.3's. A mod's super call is 1.21.3's body of that class. 1.21.3's
 * EntityType.updateCustomEntityTag asked the entity; CbRestEntityNbtMixin asks a mod override there again.
 */
public final class CbRestEntityNbt {

    private CbRestEntityNbt() {
    }

    public interface OnlyOp {
        boolean method_5833();
    }

    public static boolean method_5833(class_1297 entity) {
        return entity instanceof OnlyOp a ? a.method_5833() : entity.method_5864().method_65893();
    }

    public static boolean method_5833(class_1540 entity) {
        return method_5833((class_1297) entity);
    }

    public static boolean method_5833(class_1697 entity) {
        return method_5833((class_1297) entity);
    }

    public static boolean method_5833(class_1699 entity) {
        return method_5833((class_1297) entity);
    }

    /** Entity.onlyOpCanSetNbt()'s 1.21.3 body */
    public static boolean superOnlyOp(class_1297 entity) {
        return false;
    }

    /** FallingBlockEntity / MinecartCommandBlock / MinecartSpawner.onlyOpCanSetNbt()'s 1.21.3 body */
    public static boolean superOnlyOp(class_1540 entity) {
        return true;
    }

    public static boolean superOnlyOp(class_1697 entity) {
        return true;
    }

    public static boolean superOnlyOp(class_1699 entity) {
        return true;
    }
}
