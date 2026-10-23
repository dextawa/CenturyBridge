package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestPath;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * 1.20.4 NodeEvaluator's BlockGetter-shaped path type queries, which 1.20.5 asks with a
 * PathfindingContext. Each cover asks 1.20.5's query with the context of that getter (CbRestPath) -- a
 * mod evaluator's 1.20.4 override answers it through its override hook (guarded: the override's super
 * call comes back here and goes on to 1.20.5's implementation). The fields and protected members
 * 1.20.4 code reached are handed to CbRestPath.
 */
@Mixin(net.minecraft.class_8.class)
public abstract class CbRestNodeEvaluatorMixin implements CbRestPath.Evaluator, CbRestPath.LegacyGetter {

    @Shadow
    protected net.minecraft.class_9316 field_49416;

    @Shadow
    protected net.minecraft.class_1308 field_33;

    @Shadow
    protected int field_31;

    @Shadow
    protected abstract net.minecraft.class_9 method_13(int x, int y, int z);

    @Shadow
    public abstract net.minecraft.class_7 method_17(net.minecraft.class_9316 context, int x, int y, int z);

    @Shadow
    public abstract net.minecraft.class_7 method_25(net.minecraft.class_9316 context, int x, int y, int z, net.minecraft.class_1308 mob);

    /** 1.20.4 getBlockPathType(level, x, y, z): the evaluator's mob as the context's */
    @Override
    public net.minecraft.class_7 method_17(net.minecraft.class_1922 level, int x, int y, int z) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_17(CbRestPath.context(level, this.field_33), x, y, z);
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 getBlockPathType(level, x, y, z, mob) */
    @Override
    public net.minecraft.class_7 method_25(net.minecraft.class_1922 level, int x, int y, int z, net.minecraft.class_1308 mob) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_25(CbRestPath.context(level, mob), x, y, z, mob);
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    @Override
    public net.minecraft.class_9316 cbRest$context() {
        return this.field_49416;
    }

    @Override
    public void cbRest$setContext(net.minecraft.class_9316 context) {
        this.field_49416 = context;
    }

    @Override
    public net.minecraft.class_1308 cbRest$mob() {
        return this.field_33;
    }

    @Override
    public net.minecraft.class_9 cbRest$node(int x, int y, int z) {
        return this.method_13(x, y, z);
    }

    @Override
    public int cbRest$entityWidth() {
        return this.field_31;
    }

    /** 1.20.4 getTargetFromNode(node): the node's target */
    protected net.minecraft.class_4459 method_43568(net.minecraft.class_9 node) {
        return new net.minecraft.class_4459(node);
    }
}
