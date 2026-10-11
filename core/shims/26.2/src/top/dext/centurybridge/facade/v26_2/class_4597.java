package top.dext.centurybridge.facade.v26_2;

/** Facade for 1.20.1 net/minecraft/class_4597 (MultiBufferSource), an interface
 *  mods implement to supply a VertexConsumer per RenderType. Removed on 26.2's
 *  reworked render pipeline; the single abstract getBuffer and its types survive,
 *  so an interface facade keeps implementors loadable. getBuffer is unobfuscated
 *  in 1.20.1 (used verbatim by the corpus). */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface class_4597 {
    com.mojang.blaze3d.vertex.VertexConsumer getBuffer(
            net.minecraft.client.renderer.rendertype.RenderType renderType);
}
