package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiCameraAccess;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiGameRendererAccess;

/**
 * 1.21.11's WorldExtractionContext members 26.1's LevelExtractionContext no longer carries, read where 26.1
 * keeps them. 1.21.11's Fabric API filled them from LevelRenderer.render's arguments (the frame's camera, its
 * view matrix and culling projection, the culling frustum built from them, the block-outline flag); 26.1
 * extracts the level from the camera, which holds the same: getCullFrustum(), getViewRotationMatrix(),
 * the culling projection it builds the frustum with, and GameRenderer.shouldRenderBlockOutline() -- the flag
 * GameRenderer passes to renderLevel. A mod's own context answers through its own methods (CbFapiOverrides).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiWorldContexts {
    private CbFapiWorldContexts() {
    }

    private static final ClassValue<MethodHandle[]> OVERRIDES = new ClassValue<>() {
        @Override
        protected MethodHandle[] computeValue(Class<?> type) {
            return new MethodHandle[] {
                CbFapiOverrides.find(type, "frustum", MethodType.methodType(Frustum.class, LevelExtractionContext.class)),
                CbFapiOverrides.find(type, "viewMatrix", MethodType.methodType(Matrix4fc.class, LevelExtractionContext.class)),
                CbFapiOverrides.find(type, "cullProjectionMatrix", MethodType.methodType(Matrix4fc.class, LevelExtractionContext.class)),
                CbFapiOverrides.find(type, "blockOutlines", MethodType.methodType(boolean.class, LevelExtractionContext.class)),
            };
        }
    };

    public static Frustum frustum(LevelExtractionContext context) {
        MethodHandle h = OVERRIDES.get(context.getClass())[0];
        if (h != null) {
            try {
                return (Frustum) h.invokeExact(context);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return context.camera().getCullFrustum();
    }

    public static Matrix4fc viewMatrix(LevelExtractionContext context) {
        MethodHandle h = OVERRIDES.get(context.getClass())[1];
        if (h != null) {
            try {
                return (Matrix4fc) h.invokeExact(context);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return context.camera().getViewRotationMatrix(new Matrix4f());
    }

    public static Matrix4fc cullProjectionMatrix(LevelExtractionContext context) {
        MethodHandle h = OVERRIDES.get(context.getClass())[2];
        if (h != null) {
            try {
                return (Matrix4fc) h.invokeExact(context);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return ((CbFapiCameraAccess) context.camera()).cb$cullProjectionMatrix();
    }

    public static boolean blockOutlines(LevelExtractionContext context) {
        MethodHandle h = OVERRIDES.get(context.getClass())[3];
        if (h != null) {
            try {
                return (boolean) h.invokeExact(context);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return ((CbFapiGameRendererAccess) context.gameRenderer()).cb$shouldRenderBlockOutline();
    }

    /**
     * LevelRenderContext.bufferSource() answering the BufferSource (26.1 made it abstract): a 1.21.11 context
     * implements consumers() -- renamed bufferSource(), answering a MultiBufferSource; 1.21.11's own context
     * answered the frame's BufferSource, which is what 26.1 asks for. Any other MultiBufferSource has no
     * BufferSource to give.
     */
    @SuppressWarnings("removal") // 26.1 declares the MultiBufferSource shape only on this impl super-interface
    public static MultiBufferSource.BufferSource bufferSource(LevelRenderContext context) {
        MultiBufferSource consumers = ((net.fabricmc.fabric.impl.client.rendering.LevelRenderContextBackwardsCompatHack) context).bufferSource();
        if (consumers instanceof MultiBufferSource.BufferSource bufferSource) {
            return bufferSource;
        }
        throw new IllegalStateException(context.getClass().getName() + ".consumers() answers " + consumers
            + ", not a MultiBufferSource.BufferSource, which 26.1's LevelRenderContext.bufferSource() must answer");
    }
}
