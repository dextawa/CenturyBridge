package top.dext.centurybridge.step.v1_21_7.rt;

/**
 * 1.21.6's every ItemStackRenderState kept its model identity -- the elements its item models append
 * while it is updated -- readable through getModelIdentity() (an Object, the list) and emptied by
 * clearModelIdentity(). 1.21.7 keeps it only in TrackingItemStackRenderState, the state its GUI draws
 * items with; a plain state drops what is appended. The base class's two methods are given back by this
 * layer's mixin on ItemStackRenderState; this interface is how the layer's own code reaches the identity
 * that mixin keeps for a plain state ({@link CbItemRenderStates.View}).
 */
public interface CbModelIdentity {
    Object cb$modelIdentity();

    void cb$clearModelIdentity();
}
