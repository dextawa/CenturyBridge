package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.EnumSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPath;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 WalkNodeEvaluator's path type composition, which 1.20.5 folded into getPathTypeWithinMobBB /
 * getCachedPathType: getBlockPathTypes, evaluateBlockPathType, getBlockPathType(mob, pos),
 * getCachedBlockType(mob, x, y, z) and the four-node isDiagonalValid come back with 1.20.4's bodies
 * (the cache is 1.20.5's own, which held the same values), each calling the others virtually so a mod's
 * override of one is reached the way 1.20.4 reached it. 1.20.5's own callers reach mod overrides through
 * the override and adapter hooks (CbRestPath); getBlockPathType(mob, pos), which 1.20.5 dropped for
 * getCachedPathType in canStartAt / getStartNode, is called there again for a class that overrides it
 * (CbRestWalkNodeEvaluatorMixinHooks, default priority).
 */
@Mixin(value = net.minecraft.class_14.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestWalkNodeEvaluatorMixin extends net.minecraft.class_8 implements CbRestPath.LegacyWalk {

    @Shadow
    @Final
    private it.unimi.dsi.fastutil.longs.Long2ObjectMap<net.minecraft.class_7> field_49156;

    @Shadow
    protected abstract net.minecraft.class_7 method_31932(int x, int y, int z);

    @Override
    public net.minecraft.class_7 cbRest$cachedPathType(int x, int y, int z) {
        return this.method_31932(x, y, z);
    }

    /** 1.20.4 getBlockPathTypes(level, x, y, z, types, current, mobPos): every cell of the mob's box, evaluated */
    @Override
    @CbVanillaReads("CbRouteWalkTypesMixin: getPathTypeWithinMobBB collects the mob box's types through it, as 1.20.4's getBlockPathType(level, x, y, z, mob) did, when a converted mod mixes it or evaluateBlockPathType")
    public net.minecraft.class_7 method_64(net.minecraft.class_1922 level, int x, int y, int z, EnumSet<net.minecraft.class_7> types,
                                           net.minecraft.class_7 current, net.minecraft.class_2338 mobPos) {
        for (int i = 0; i < this.field_31; i++) {
            for (int j = 0; j < this.field_30; j++) {
                for (int k = 0; k < this.field_28; k++) {
                    net.minecraft.class_7 type = ((CbRestPath.LegacyGetter) this).method_17(level, i + x, j + y, k + z);
                    type = this.method_61(level, mobPos, type);
                    if (i == 0 && j == 0 && k == 0) {
                        current = type;
                    }
                    types.add(type);
                }
            }
        }
        return current;
    }

    /** 1.20.4 evaluateBlockPathType(level, pos, type): doors as the evaluator may pass them, rails only on rails */
    @Override
    @CbVanillaReads("CbRouteWalkTypesMixin: getPathTypeWithinMobBB evaluates each cell of the mob's box through it (via getBlockPathTypes), as 1.20.4's did, when a converted mod mixes it or getBlockPathTypes")
    public net.minecraft.class_7 method_61(net.minecraft.class_1922 level, net.minecraft.class_2338 pos, net.minecraft.class_7 type) {
        boolean canPassDoors = this.method_23();
        if (type == net.minecraft.class_7.field_23 && this.method_24() && canPassDoors) {
            type = net.minecraft.class_7.field_26446;
        }
        if (type == net.minecraft.class_7.field_15 && !canPassDoors) {
            type = net.minecraft.class_7.field_22;
        }
        if (type == net.minecraft.class_7.field_21 && !(level.method_8320(pos).method_26204() instanceof net.minecraft.class_2241)
                && !(level.method_8320(pos.method_10074()).method_26204() instanceof net.minecraft.class_2241)) {
            type = net.minecraft.class_7.field_25418;
        }
        return type;
    }

    /** 1.20.4 getBlockPathType(mob, pos) */
    @Override
    public net.minecraft.class_7 method_63(net.minecraft.class_1308 mob, net.minecraft.class_2338 pos) {
        return this.method_29303(mob, pos.method_10263(), pos.method_10264(), pos.method_10260());
    }

    /** 1.20.4 getCachedBlockType(mob, x, y, z): the mob's path type there, cached per position */
    @Override
    public net.minecraft.class_7 method_29303(net.minecraft.class_1308 mob, int x, int y, int z) {
        return this.field_49156.computeIfAbsent(net.minecraft.class_2338.method_10064(x, y, z),
            k -> this.method_25(this.field_49416, x, y, z, mob));
    }

    /** 1.20.4 isDiagonalValid(node, a, b, diagonal) */
    @Override
    public boolean method_29579(net.minecraft.class_9 node, net.minecraft.class_9 a, net.minecraft.class_9 b, net.minecraft.class_9 diagonal) {
        if (diagonal == null || b == null || a == null) {
            return false;
        }
        if (diagonal.field_42) {
            return false;
        }
        if (b.field_39 > node.field_39 || a.field_39 > node.field_39) {
            return false;
        }
        if (a.field_41 == net.minecraft.class_7.field_26446 || b.field_41 == net.minecraft.class_7.field_26446
                || diagonal.field_41 == net.minecraft.class_7.field_26446) {
            return false;
        }
        boolean thinFence = b.field_41 == net.minecraft.class_7.field_10 && a.field_41 == net.minecraft.class_7.field_10
            && this.field_33.method_17681() < 0.5D;
        return diagonal.field_43 >= 0.0F
            && (b.field_39 < node.field_39 || b.field_43 >= 0.0F || thinFence)
            && (a.field_39 < node.field_39 || a.field_43 >= 0.0F || thinFence);
    }
}
