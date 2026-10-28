package top.dext.centurybridge.step.v1_21_6.rt;

import net.minecraft.class_5321;

/**
 * Fabric API 0.128 (1.21.5) tag datagen as the layer gives it back on 1.21.6 (facade/fapi/FabricTagBuilder,
 * CbFapiTagProviderRestoredMixin): what the builder facade asks of its provider.
 */
public final class CbFapiTags {
    private CbFapiTags() {
    }

    /** a FabricTagProvider, through CbFapiTagProviderRestoredMixin */
    public interface Provider {
        /** the provider's restored reverseLookup(T), called virtually (a 1.21.5 subclass's override answers) */
        class_5321<?> cb$v1_21_6$reverseLookup(Object element);
    }
}
