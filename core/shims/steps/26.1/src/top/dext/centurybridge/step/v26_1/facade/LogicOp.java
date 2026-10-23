package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's com.mojang.blaze3d.platform.LogicOp, gone in 26.1 (its pipelines have no colour logic
 * operation). Same constants in the same order; a pipeline built with OR_REVERSE gets 1.21.11's GL
 * state back from mixin/CbWallGlPipelineStateMixin.
 */
public enum LogicOp {
    NONE,
    OR_REVERSE
}
