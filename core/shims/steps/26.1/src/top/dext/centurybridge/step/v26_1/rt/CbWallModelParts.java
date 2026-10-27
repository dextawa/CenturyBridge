package top.dext.centurybridge.step.v26_1.rt;

import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.TextureSlots;

/**
 * 1.21.11 model/texture/block-entity members 26.1 reshaped (javap of both):
 * <ul>
 * <li>ResolvedModel.resolveParticleSprite(slots, baker[, name]) answered the particle slot's sprite; 26.1's
 * resolveParticleMaterial answers the slot's baked material, whose sprite() it is;</li>
 * <li>TextureSlots.Data.Builder.addTexture(name, material) took 1.21.11's Material (an atlas and a texture,
 * 26.1's SpriteId); 26.1's takes the model Material, which names the sprite by its texture alone (its atlas is
 * the one the model's sprites are baked from) -- the texture of the SpriteId, not forced translucent;</li>
 * <li>SpriteContents.getUniqueFrames() answered an IntStream; 26.1's answers the same frames as an IntList;</li>
 * <li>BlockEntityRenderDispatcher.prepare(camera) kept the camera's position; 26.1's prepare takes the position.</li>
 * </ul>
 * Each: a mod's call reaches a 1.21.11 override when its class declares one (adapterHooks), else 1.21.11's
 * body on 26.1's member; a 1.21.11 override's super call is 1.21.11's body.
 */
@Environment(EnvType.CLIENT)
public final class CbWallModelParts {
    private CbWallModelParts() {
    }

    // ---- ResolvedModel.resolveParticleSprite ----

    /** 1.21.11's ResolvedModel.resolveParticleSprite(slots, baker), declared by a mod's model (adapterHooks) */
    public interface LegacyParticleSprite {
        TextureAtlasSprite resolveParticleSprite(TextureSlots slots, ModelBaker baker);
    }

    /** 1.21.11 static ResolvedModel.resolveParticleSprite(slots, baker, name) */
    public static TextureAtlasSprite resolveParticleSprite(TextureSlots slots, ModelBaker baker, ModelDebugName name) {
        return ResolvedModel.resolveParticleMaterial(slots, baker, name).sprite();
    }

    /** a mod's call of model.resolveParticleSprite(slots, baker) */
    public static TextureAtlasSprite resolveParticleSprite(ResolvedModel model, TextureSlots slots, ModelBaker baker) {
        if (model instanceof LegacyParticleSprite legacy) {
            return legacy.resolveParticleSprite(slots, baker);
        }
        return superResolveParticleSprite(model, slots, baker);
    }

    /** 1.21.11's default body: the static one, the model as its name */
    public static TextureAtlasSprite superResolveParticleSprite(ResolvedModel model, TextureSlots slots, ModelBaker baker) {
        return resolveParticleSprite(slots, baker, model);
    }

    // ---- TextureSlots.Data.Builder.addTexture ----

    /** 1.21.11's Builder.addTexture(name, material), declared by a mod's builder subclass (adapterHooks) */
    public interface LegacyAddTexture {
        TextureSlots.Data.Builder addTexture(String name, SpriteId material);
    }

    /** a mod's call of builder.addTexture(name, material) */
    public static TextureSlots.Data.Builder addTexture(TextureSlots.Data.Builder builder, String name, SpriteId material) {
        if (builder instanceof LegacyAddTexture legacy) {
            return legacy.addTexture(name, material);
        }
        return superAddTexture(builder, name, material);
    }

    /** 1.21.11's body: the slot holds the material (26.1's, naming the SpriteId's texture) */
    public static TextureSlots.Data.Builder superAddTexture(TextureSlots.Data.Builder builder, String name, SpriteId material) {
        return builder.addTexture(name, new Material(material.texture()));
    }

    // ---- SpriteContents.getUniqueFrames ----

    /** 1.21.11's getUniqueFrames(), declared by a mod's sprite contents subclass (adapterHooks) */
    public interface LegacyUniqueFrames {
        IntStream getUniqueFrames();
    }

    /** a mod's call of contents.getUniqueFrames() */
    public static IntStream getUniqueFrames(SpriteContents contents) {
        if (contents instanceof LegacyUniqueFrames legacy) {
            return legacy.getUniqueFrames();
        }
        return superGetUniqueFrames(contents);
    }

    /** 1.21.11's body: the animation's unique frames, else frame 0 alone -- 26.1's list as a stream */
    public static IntStream superGetUniqueFrames(SpriteContents contents) {
        return contents.getUniqueFrames().intStream();
    }

    // ---- BlockEntityRenderDispatcher.prepare ----

    /** 1.21.11's prepare(camera), declared by a mod's dispatcher subclass (adapterHooks) */
    public interface LegacyPrepare {
        void prepare(Camera camera);
    }

    /** a mod's call of dispatcher.prepare(camera) */
    public static void prepare(BlockEntityRenderDispatcher dispatcher, Camera camera) {
        if (dispatcher instanceof LegacyPrepare legacy) {
            legacy.prepare(camera);
            return;
        }
        superPrepare(dispatcher, camera);
    }

    /** 1.21.11's body: the camera's position kept for rendering */
    public static void superPrepare(BlockEntityRenderDispatcher dispatcher, Camera camera) {
        dispatcher.prepare(camera.position());
    }
}
