package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_2473;
import net.minecraft.class_8813;
import top.dext.centurybridge.step.v1_20_3.facade.AbstractTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.AcaciaTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.AzaleaTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.BirchTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.CherryTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.DarkOakTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.JungleTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.MangroveTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.OakTreeGrower;
import top.dext.centurybridge.step.v1_20_3.facade.SpruceTreeGrower;

/**
 * Saplings: 1.20.2's SaplingBlock grew through an AbstractTreeGrower object, whose subclasses chose the
 * feature in code; 1.20.3's takes a TreeGrower, a fixed table of feature keys. A sapling built through
 * the 1.20.2 constructor keeps its 1.20.2 grower (the constructor reshape's post step, {@link #attachGrower})
 * and grows through it (CbWorldSaplingMixin). The 1.20.3 field gets the TreeGrower of the same vanilla tree
 * for 1.20.2's own growers, and for any other one a registered TreeGrower naming no feature: the sapling
 * never asks it.
 */
public final class CbWorldTrees {

    private static final Map<Class<?>, class_8813> STAND_INS = new ConcurrentHashMap<>();

    public static class_8813 legacyGrower(AbstractTreeGrower grower) {
        if (grower == null) {
            return null;
        }
        // the exact class, not instanceof: a mod's subclass of OakTreeGrower may choose otherwise
        Class<?> c = grower.getClass();
        if (c == OakTreeGrower.class) {
            return class_8813.field_46511;
        }
        if (c == SpruceTreeGrower.class) {
            return class_8813.field_46512;
        }
        if (c == BirchTreeGrower.class) {
            return class_8813.field_46515;
        }
        if (c == JungleTreeGrower.class) {
            return class_8813.field_46516;
        }
        if (c == AcaciaTreeGrower.class) {
            return class_8813.field_46517;
        }
        if (c == CherryTreeGrower.class) {
            return class_8813.field_46518;
        }
        if (c == DarkOakTreeGrower.class) {
            return class_8813.field_46519;
        }
        if (c == AzaleaTreeGrower.class) {
            return class_8813.field_46514;
        }
        if (c == MangroveTreeGrower.class && ((MangroveTreeGrower) grower).field_38758 == 0.85F) {
            return class_8813.field_46513;
        }
        return STAND_INS.computeIfAbsent(c, k -> new class_8813("centurybridge_legacy_" + STAND_INS.size() + "_"
            + k.getName().replace('.', '_').replace('$', '_').toLowerCase(java.util.Locale.ROOT),
            Optional.empty(), Optional.empty(), Optional.empty()));
    }

    /** post step of SaplingBlock(grower, props): the sapling keeps the 1.20.2 grower */
    public static void attachGrower(class_2473 sapling, AbstractTreeGrower grower) {
        ((CbWorldSaplingAccess) sapling).cb$setLegacyGrower(grower);
    }

    private CbWorldTrees() {
    }
}
