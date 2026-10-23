package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.renderer.QuadSpriteBaker;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * 1.21.11's MutableQuadView / QuadEmitter.spriteBake(sprite, bakeFlags), gone in 26.1 (whose materialBake
 * also sets the quad's chunk layer, item render type and animation from a Material.Baked, which 1.21.11's
 * call never did): 1.21.11's body -- the UVs baked by Fabric API's QuadSpriteBaker (26.1 keeps the same
 * baker, instruction for instruction) and the quad's atlas set from the sprite's (the block atlas when it is
 * neither). QuadEmitter.uvUnitSquare() answers a MutableQuadView in 26.1: the same emitter.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiQuads {
    private CbFapiQuads() {
    }

    private static final ClassValue<MethodHandle[]> OVERRIDES = new ClassValue<>() {
        @Override
        protected MethodHandle[] computeValue(Class<?> type) {
            return new MethodHandle[] {
                CbFapiOverrides.find(type, "spriteBake", MethodType.methodType(MutableQuadView.class, MutableQuadView.class, TextureAtlasSprite.class, int.class)),
                CbFapiOverrides.find(type, "spriteBake", MethodType.methodType(QuadEmitter.class, QuadEmitter.class, TextureAtlasSprite.class, int.class)),
            };
        }
    };

    public static MutableQuadView spriteBake(MutableQuadView quad, TextureAtlasSprite sprite, int bakeFlags) {
        MethodHandle h = OVERRIDES.get(quad.getClass())[0];
        if (h != null) {
            try {
                return (MutableQuadView) h.invokeExact(quad, sprite, bakeFlags);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return superSpriteBake(quad, sprite, bakeFlags);
    }

    public static QuadEmitter spriteBake(QuadEmitter emitter, TextureAtlasSprite sprite, int bakeFlags) {
        MethodHandle h = OVERRIDES.get(emitter.getClass())[1];
        if (h != null) {
            try {
                return (QuadEmitter) h.invokeExact(emitter, sprite, bakeFlags);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return superSpriteBake(emitter, sprite, bakeFlags);
    }

    /** 1.21.11's MutableQuadView.spriteBake default */
    public static MutableQuadView superSpriteBake(MutableQuadView quad, TextureAtlasSprite sprite, int bakeFlags) {
        QuadSpriteBaker.bakeSprite(quad, sprite, bakeFlags);
        QuadAtlas atlas = QuadAtlas.ofLocation(sprite.atlasLocation());
        quad.atlas(atlas == null ? QuadAtlas.BLOCK : atlas);
        return quad;
    }

    /** 1.21.11's QuadEmitter.spriteBake default (MutableQuadView's, answering the emitter) */
    public static QuadEmitter superSpriteBake(QuadEmitter emitter, TextureAtlasSprite sprite, int bakeFlags) {
        superSpriteBake((MutableQuadView) emitter, sprite, bakeFlags);
        return emitter;
    }

    /** QuadEmitter.uvUnitSquare()'s answer as 1.21.11 typed it: 26.1 answers the emitter as a MutableQuadView */
    public static QuadEmitter emitter(MutableQuadView quad) {
        return (QuadEmitter) quad;
    }

    /** a 1.21.11 emitter's own uvUnitSquare() answer, as 26.1's shape types it */
    public static MutableQuadView view(QuadEmitter emitter) {
        return emitter;
    }
}
