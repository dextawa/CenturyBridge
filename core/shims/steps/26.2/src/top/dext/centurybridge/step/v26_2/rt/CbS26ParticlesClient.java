package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleResources;

/**
 * 26.1.2 Minecraft.particleResources on 26.2 (javap -c of both Minecraft.&lt;init&gt;): 26.1.2 kept the ParticleResources
 * it built in a field and handed it to new ParticleEngine(level, particleResources); 26.2 builds the same object into a
 * local and hands it to new ParticleEngine(level, resources), which keeps it as resourceManager (opened by the layer's
 * access widener). particleEngine is assigned once, in the constructor, so the engine's resources are the ones 26.1.2's
 * field held.
 */
@Environment(EnvType.CLIENT)
public final class CbS26ParticlesClient {
    private CbS26ParticlesClient() {}

    /** 26.1.2 minecraft.particleResources (instanceFieldGetters) */
    public static ParticleResources particleResources(Minecraft minecraft) {
        return minecraft.particleEngine.resourceManager;
    }
}
