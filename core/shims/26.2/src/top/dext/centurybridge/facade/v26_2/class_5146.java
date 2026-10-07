package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_5146 (a sound-instance control
 *  interface), removed on 26.2. Legacy implementors supply these; keeping the
 *  interface loadable removes the "implements <gone>" failure. Member names are
 *  intermediary tokens. */
public interface class_5146 {
    /** isStopped()/shouldStop() (method_6765) */
    boolean method_6765();

    /** setSource(SoundSource) (method_6576) */
    void method_6576(net.minecraft.sounds.SoundSource source);

    /** isRelative/looping (method_6725) */
    boolean method_6725();
}
