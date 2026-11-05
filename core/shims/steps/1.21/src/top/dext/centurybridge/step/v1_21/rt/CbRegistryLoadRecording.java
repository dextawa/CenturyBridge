package top.dext.centurybridge.step.v1_21.rt;

/** this layer's world-registry recording as a {@link CbRegistryLoads.Hook}: CbLegacyRegistries.onServerLoaded / onClientLoaded */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class CbRegistryLoadRecording implements CbRegistryLoads.Hook {
    @Override
    public void cb$serverLoaded(net.minecraft.class_5321 key, net.minecraft.class_2385 registry, net.minecraft.class_6903.class_7863 lookup) {
        CbLegacyRegistries.onServerLoaded(key, registry, lookup);
    }

    @Override
    public void cb$clientLoaded(net.minecraft.class_5321 key, net.minecraft.class_2385 registry) {
        CbLegacyRegistries.onClientLoaded(key, registry);
    }
}
