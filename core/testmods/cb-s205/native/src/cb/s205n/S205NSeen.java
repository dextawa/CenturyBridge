package cb.s205n;

/** what cb.s205n.mixin.SetPostEffectMixin saw (outside the mixin package); cb-s205's climbed driver reads these */
public final class S205NSeen {
    /** the id of each setPostEffect call checkEntityPostEffect made, seen by the @WrapOperation around it */
    public static final java.util.List<String> WRAPPED = new java.util.ArrayList<>();
    /** the id of each setPostEffect call (HEAD) */
    public static final java.util.List<String> SET = new java.util.ArrayList<>();

    private S205NSeen() {
    }
}
