package cb.s26v;

/**
 * What the control's native 26.2 mixins saw (mixin/S26VRestituteMixin), read by cb-s26 (reflectively, when both mods
 * are loaded) to prove that the step layer's routes leave a native mixin on the 26.2 method firing.
 */
public final class S26VNative {
    private S26VNative() {}

    /** calls of Entity.restituteMovementAfterCollisions that reached its RETURN */
    public static int restituteReturns;
}
