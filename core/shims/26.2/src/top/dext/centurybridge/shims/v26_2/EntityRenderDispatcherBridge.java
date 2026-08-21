package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * net.minecraft.client.renderer.texture.TextureAtlasSprite (EntityRenderDispatcher): camera-angle accessors changed from
 * double to float precision at 1.20.2. Old mods pass double literals; the JVM
 * sees descriptor (D)F and finds no match. We add the missing overloads that
 * narrow to float and forward.
 */
@Mixin(net.minecraft.client.renderer.texture.TextureAtlasSprite.class)
public abstract class EntityRenderDispatcherBridge {

    /** 1.20.1 getPitch(tickDelta) -> double. 102 call sites. */
    public float getU(double tickDelta) {
        return ((net.minecraft.client.renderer.texture.TextureAtlasSprite) (Object) this).getU((float) tickDelta);
    }

    /** 1.20.1 getYaw(tickDelta) -> double. 102 call sites. */
    public float getV(double tickDelta) {
        return ((net.minecraft.client.renderer.texture.TextureAtlasSprite) (Object) this).getV((float) tickDelta);
    }
}
