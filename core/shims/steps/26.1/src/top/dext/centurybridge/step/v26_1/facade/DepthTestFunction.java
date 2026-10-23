package top.dext.centurybridge.step.v26_1.facade;

/**
 * 1.21.11's com.mojang.blaze3d.platform.DepthTestFunction, gone in 26.1 (a pipeline's depth state is a
 * DepthStencilState holding a CompareOp, or none at all). Same constants in the same order, so values(),
 * valueOf and ordinal answer what 1.21.11 did; rt/CbWallPipeline maps them to and from 26.1's state.
 */
public enum DepthTestFunction {
    NO_DEPTH_TEST,
    EQUAL_DEPTH_TEST,
    LEQUAL_DEPTH_TEST,
    LESS_DEPTH_TEST,
    GREATER_DEPTH_TEST
}
