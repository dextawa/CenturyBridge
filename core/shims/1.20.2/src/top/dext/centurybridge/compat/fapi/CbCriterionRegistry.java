package top.dext.centurybridge.compat.fapi;

/**
 * Fabric API's CriterionRegistry (object-builder v1, gone with 1.20.2): register a
 * trigger under the id its 1.20.1 getId() names -- what the removed helper did.
 * Every reference to the old class is renamed here (classRenames).
 */
public final class CbCriterionRegistry {

    @SuppressWarnings("unchecked")
    public static <T extends net.minecraft.class_179<?>> T register(T criterion) {
        return (T) top.dext.centurybridge.rt.v1_20_2.Statics.method_767(criterion);
    }

    private CbCriterionRegistry() {
    }
}
