package top.dext.centurybridge.step.v1_21_6.rt;

import com.mojang.blaze3d.textures.GpuTexture;

/**
 * 1.21.5 GPU textures on 1.21.6. 1.21.5's textures were two-dimensional (no layer count) and every operation was
 * allowed on every texture (javap of GlCommandEncoder / GlRenderPass: no usage checks). 1.21.6 creates a texture with
 * usage bits and a depth / layer count and checks the bits: writeToTexture needs COPY_DST, a copy's source COPY_SRC,
 * binding to a sampler TEXTURE_BINDING, a render pass attachment RENDER_ATTACHMENT; CUBEMAP_COMPATIBLE needs six
 * layers (javap). So a texture a 1.21.5 call or subclass creates gets every bit but the cube-map one, and one layer.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbTextures {

    private CbTextures() {
    }

    /** the usage of a texture 1.21.5 code creates: everything 1.21.5 allowed on any texture */
    public static int legacyUsage() {
        return GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_COPY_SRC | GpuTexture.USAGE_TEXTURE_BINDING
            | GpuTexture.USAGE_RENDER_ATTACHMENT;
    }

    /** the depth / layer count of a texture 1.21.5 code creates: 1.21.5's were two-dimensional */
    public static int oneLayer() {
        return 1;
    }

    /** the layer a 1.21.5 writeToTexture writes: its textures had one layer, 1.21.6 numbers them from 0 */
    public static int firstLayer() {
        return 0;
    }

    /**
     * Each texture's full view, made on first use and closed when the texture is (CbTextureViewsMixin). 1.21.6 renders
     * into and presents views (javap of CommandEncoder); a GlTexture counts its open views and deletes its GL texture
     * when it is closed and the count is zero, so the view closes with the texture and the texture still goes away.
     */
    private static final java.util.Map<GpuTexture, com.mojang.blaze3d.textures.GpuTextureView> VIEWS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static final java.util.function.Supplier<String> PASS_LABEL = () -> "CenturyBridge: 1.21.5 render pass";

    /** the full view 1.21.6 takes where 1.21.5 took the texture (null for null: 1.21.5's depth texture was optional) */
    public static com.mojang.blaze3d.textures.GpuTextureView view(GpuTexture texture) {
        if (texture == null) {
            return null;
        }
        synchronized (VIEWS) {
            com.mojang.blaze3d.textures.GpuTextureView view = VIEWS.get(texture);
            if (view == null || view.isClosed()) {
                view = com.mojang.blaze3d.systems.RenderSystem.getDevice().createTextureView(texture);
                VIEWS.put(texture, view);
            }
            return view;
        }
    }

    /** CbTextureViewsMixin: a texture closed; its view goes with it */
    public static void closed(GpuTexture texture) {
        com.mojang.blaze3d.textures.GpuTextureView view = VIEWS.remove(texture);
        if (view != null) {
            view.close();
        }
    }

    /**
     * CommandEncoder.createRenderPass(GpuTexture, OptionalInt): 1.21.5 rendered into the texture (no label); 1.21.6
     * renders into a view and takes a debug label.
     */
    public static com.mojang.blaze3d.systems.RenderPass createRenderPass(com.mojang.blaze3d.systems.CommandEncoder encoder,
            GpuTexture color, java.util.OptionalInt clearColor) {
        return encoder.createRenderPass(PASS_LABEL, view(color), clearColor);
    }

    /** CommandEncoder.createRenderPass(GpuTexture, OptionalInt, GpuTexture depth, OptionalDouble) */
    public static com.mojang.blaze3d.systems.RenderPass createRenderPass(com.mojang.blaze3d.systems.CommandEncoder encoder,
            GpuTexture color, java.util.OptionalInt clearColor, GpuTexture depth, java.util.OptionalDouble clearDepth) {
        return encoder.createRenderPass(PASS_LABEL, view(color), clearColor, view(depth), clearDepth);
    }

    /** CommandEncoder.presentTexture(GpuTexture): 1.21.6 presents a view */
    public static void presentTexture(com.mojang.blaze3d.systems.CommandEncoder encoder, GpuTexture texture) {
        encoder.presentTexture(view(texture));
    }

    /**
     * the same three, called on the GL command encoder itself.
     * The cast goes through Object so javac keeps it: 26.1's GL classes are backends, no longer the GpuDevice /
     * CommandEncoder / RenderPass they implement here, and an elided upcast fails verification of the whole class
     * once climbed to 26.1 (VerifyError, the climbed client's first GUI frame).
     */
    public static com.mojang.blaze3d.systems.RenderPass createRenderPass(net.minecraft.class_10860 encoder, GpuTexture color,
            java.util.OptionalInt clearColor) {
        return createRenderPass((com.mojang.blaze3d.systems.CommandEncoder) (Object) encoder, color, clearColor);
    }

    public static com.mojang.blaze3d.systems.RenderPass createRenderPass(net.minecraft.class_10860 encoder, GpuTexture color,
            java.util.OptionalInt clearColor, GpuTexture depth, java.util.OptionalDouble clearDepth) {
        return createRenderPass((com.mojang.blaze3d.systems.CommandEncoder) (Object) encoder, color, clearColor, depth, clearDepth);
    }

    public static void presentTexture(net.minecraft.class_10860 encoder, GpuTexture texture) {
        presentTexture((com.mojang.blaze3d.systems.CommandEncoder) (Object) encoder, texture);
    }
}
