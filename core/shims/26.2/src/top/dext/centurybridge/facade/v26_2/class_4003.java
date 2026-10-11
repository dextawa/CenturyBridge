package top.dext.centurybridge.facade.v26_2;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Facade for 1.20.1 net/minecraft/class_4003 (TextureSheetParticle), deleted on
 * 26.2 but its parent SingleQuadParticle survives and KEEPS the data-driven
 * render path (getU0/U1/V0/V1, getQuadSize, setSprite) that 26.2's extract step
 * reads. So a legacy sprite particle reached via superRenames extends this
 * facade -> real SingleQuadParticle, and its sprite/size/colour overrides are
 * still consumed by the renderer (NOT an orphan, unlike entity-model draw
 * overrides). The one new abstract SingleQuadParticle.getLayer() is given a
 * default so the legacy subclass -- which never declared it -- still loads.
 *
 * The old constructors lack the TextureAtlasSprite the 26.2 ctor wants; null is
 * passed and the mod sets the sprite later via pickSprite/setSpriteFromAge
 * (method_18140/18142), the standard particle lifecycle. Member names are
 * intermediary tokens.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_4003 extends SingleQuadParticle {

    public TextureAtlasSprite field_17886; // sprite

    public class_4003(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, (TextureAtlasSprite) null);
    }

    public class_4003(ClientLevel level, double x, double y, double z,
                         double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz, (TextureAtlasSprite) null);
    }

    /** the new abstract on SingleQuadParticle; legacy subclasses never declared
     *  it. Default to a translucent terrain-sheet layer so they load and draw. */
    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    /** setSprite(TextureAtlasSprite) (method_18141) */
    public void method_18141(TextureAtlasSprite s) {
        this.field_17886 = s;
        this.setSprite(s);
    }

    /** setSprite(SpriteSet) -- pick by age (method_18140) */
    public void method_18140(SpriteSet set) {
        this.setSpriteFromAge(set);
    }

    /** setSpriteForAge(SpriteSet) (method_18142) */
    public void method_18142(SpriteSet set) {
        this.setSpriteFromAge(set);
    }

    // ---- inherited Particle/SingleQuadParticle methods the legacy subclass
    // ---- calls or overrides by intermediary name; forward to the surviving
    // ---- 26.2 equivalents. getU0..V1/getQuadSize/getLightCoords feed 26.2's
    // ---- extract step, so a subclass override of them is honoured (NOT an
    // ---- orphan). Only method_3074 (the old immediate-mode render) has no
    // ---- 26.2 counterpart -- the extract pipeline draws instead -- so it is
    // ---- an inert no-op.

    /** remove() (method_3070) */
    public void method_3070() {
        super.remove();
    }

    /** getLightColor/getLightCoords(float) (method_3068) */
    protected int method_3068(float partial) {
        return super.getLightCoords(partial);
    }

    /** getQuadSize(float) (method_18132) */
    public float method_18132(float partial) {
        return super.getQuadSize(partial);
    }

    /** getU0 (method_18133) */
    public float method_18133() {
        return super.getU0();
    }

    /** getU1 (method_18134) */
    public float method_18134() {
        return super.getU1();
    }

    /** getV0 (method_18135) */
    public float method_18135() {
        return super.getV0();
    }

    /** getV1 (method_18136) */
    public float method_18136() {
        return super.getV1();
    }

    /** old immediate-mode render(VertexConsumer, Camera, float) (method_3074) --
     *  no 26.2 counterpart; the extract pipeline draws. Inert so a subclass that
     *  calls super renders through the real path, and one that overrides it is a
     *  (harmless) orphan rather than a link failure. */
    public void method_3074(com.mojang.blaze3d.vertex.VertexConsumer vc,
            net.minecraft.client.Camera camera, float partial) {
    }
}
