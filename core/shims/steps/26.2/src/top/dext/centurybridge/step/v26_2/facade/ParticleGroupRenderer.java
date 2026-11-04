package top.dext.centurybridge.step.v26_2.facade;

/**
 * 26.1.2's net.minecraft.client.renderer.SubmitNodeCollector.ParticleGroupRenderer, which 26.2 deleted (javap: no class
 * of the 26.2 client jar or Fabric API 0.158 names it; QuadParticleRenderState no longer implements it). Only isEmpty()
 * is given back: prepare(ParticleBufferCache, boolean) / render(PreparedBuffers, ParticleBufferCache, RenderPass,
 * TextureManager) take and answer types 26.2 deleted too (facadeGaps in smixin14-particles.json). classRenames there
 * send references here, so a 26.1.2 override of submitParticleGroup(ParticleGroupRenderer) keeps a type that loads
 * and the override hook can hand it 26.2's group (rt/CbS26OrderedSubmit#legacyGroup).
 */
public interface ParticleGroupRenderer {
    boolean isEmpty();
}
