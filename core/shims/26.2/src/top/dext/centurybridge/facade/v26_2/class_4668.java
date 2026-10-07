package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_4668 (RenderStateShard), deleted on
 *  26.2 (render pipeline reworked). Legacy mods extend it to define render
 *  phases with setup/clear GL callbacks. Standalone reimplementation keeps those
 *  classes loadable and runs the callbacks; whether 26.2's pipeline consumes the
 *  shard is a separate (ledgered) concern. Member names are intermediary tokens. */
public abstract class class_4668 {
    private final String name;
    private final Runnable setupState;
    private final Runnable clearState;

    public class_4668(String name, Runnable setupState, Runnable clearState) {
        this.name = name;
        this.setupState = setupState;
        this.clearState = clearState;
    }

    /** setupRenderState (method_23516) */
    public void method_23516() {
        this.setupState.run();
    }

    /** clearRenderState (method_23518) */
    public void method_23518() {
        this.clearState.run();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
