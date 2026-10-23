package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.client.renderer.v1.sprite.FabricTextureAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.sprite.SpriteFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;

/**
 * 1.21.11 Fabric renderer API helpers 26.1's Fabric API dropped, with 1.21.11's bodies on 26.1's API:
 * SpriteFinder.get(atlas) (the atlas's finder: 1.21.11's FabricSpriteAtlasTexture.spriteFinder(), 26.1's
 * FabricTextureAtlas.spriteFinder() -- both injected into TextureAtlas), SpriteFinder.find(quad, int) (the
 * int was already ignored: find(quad)), ModelHelper.toQuadLists(mesh) (the mesh's quads baked with their
 * block-atlas sprites, one list per cull face and one for none).
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRendererHelpers {
    private CbFapiRendererHelpers() {
    }

    /** SpriteFinder.get(TextureAtlas) */
    public static SpriteFinder get(TextureAtlas atlas) {
        return ((FabricTextureAtlas) atlas).spriteFinder();
    }

    private static final ClassValue<MethodHandle> FIND = new ClassValue<>() {
        @Override
        protected MethodHandle computeValue(Class<?> type) {
            return CbFapiOverrides.find(type, "find", MethodType.methodType(TextureAtlasSprite.class, SpriteFinder.class, QuadView.class, int.class));
        }
    };

    /** SpriteFinder.find(quad, textureIndex), on any finder: a finder's own override answers */
    public static TextureAtlasSprite find(SpriteFinder finder, QuadView quad, int textureIndex) {
        MethodHandle h = FIND.get(finder.getClass());
        if (h != null) {
            try {
                return (TextureAtlasSprite) h.invokeExact(finder, quad, textureIndex);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        return superFind(finder, quad, textureIndex);
    }

    /** 1.21.11's default SpriteFinder.find(quad, textureIndex) */
    public static TextureAtlasSprite superFind(SpriteFinder finder, QuadView quad, int textureIndex) {
        return finder.find(quad);
    }

    /** ModelHelper.toQuadLists(mesh) */
    @SuppressWarnings("unchecked")
    public static List<BakedQuad>[] toQuadLists(Mesh mesh) {
        SpriteFinder finder = get(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS));
        ImmutableList.Builder<BakedQuad>[] builders = new ImmutableList.Builder[7];
        for (int i = 0; i < 7; i++) {
            builders[i] = ImmutableList.builder();
        }
        mesh.forEach(quad -> {
            Direction cullFace = quad.cullFace();
            int index = cullFace == null ? 6 : cullFace.get3DDataValue();
            builders[index].add(quad.toBakedQuad(finder.find(quad)));
        });
        List<BakedQuad>[] lists = new List[7];
        for (int i = 0; i < 7; i++) {
            lists[i] = builders[i].build();
        }
        return lists;
    }
}
