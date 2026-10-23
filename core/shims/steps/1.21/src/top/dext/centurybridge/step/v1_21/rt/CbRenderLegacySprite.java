package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSpriteAccess;

/**
 * 1.20.6's SpriteCoordinateExpander, for a 1.20.6 subclass of it (superRenames): a 1.20.6 consumer
 * (CbRenderLegacyConsumer) whose 1.20.6 methods are 1.20.6's expander -- vertex(..), overlayCoords, endVertex and
 * the default color go on to the delegate (vertex answering what the delegate answers, as 1.20.6's did), the whole
 * vertex goes on with the sprite's UVs. color, uv (through the sprite), uv2 and normal are 1.21's expander's, the
 * same as 1.20.6's. 1.21's addVertex / setUv1 / whole vertex reach the 1.20.6 methods (the subclass's own if it
 * overrides them), as 1.20.6's renderers reached them.
 */
@Environment(EnvType.CLIENT)
public class CbRenderLegacySprite extends net.minecraft.class_4723 implements CbRenderLegacyConsumer {

    public CbRenderLegacySprite(net.minecraft.class_4588 delegate, net.minecraft.class_1058 sprite) {
        super(delegate, sprite);
    }

    private net.minecraft.class_4588 cbDelegate() {
        return ((CbRenderSpriteAccess) (Object) this).cb$delegate();
    }

    // ------------------------------------------------------------------ 1.21's calls to the 1.20.6 methods

    @Override
    public net.minecraft.class_4588 method_22912(float x, float y, float z) {
        return CbRenderLegacyConsumer.super.method_22912(x, y, z);
    }

    @Override
    public net.minecraft.class_4588 method_60796(int u, int v) {
        return CbRenderLegacyConsumer.super.method_60796(u, v);
    }

    @Override
    public void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny,
                             float nz) {
        CbRenderLegacyConsumer.super.method_23919(x, y, z, color, u, v, overlay, light, nx, ny, nz);
    }

    // ------------------------------------------------------------------ 1.20.6's expander

    /** vertex(x, y, z): the delegate's, and its answer */
    @Override
    public net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z) {
        return CbRenderVertex.cbVertex(this.cbDelegate(), x, y, z);
    }

    /** overlayCoords(u, v): the delegate's, and its answer */
    @Override
    public net.minecraft.class_4588 method_22917(int u, int v) {
        return CbRenderVertex.cbOverlayCoords(this.cbDelegate(), u, v);
    }

    /** endVertex(): the delegate's */
    @Override
    public void cbLegacyEndVertex() {
        CbRenderVertex.cbEndVertex(this.cbDelegate());
    }

    /** defaultColor(r, g, b, a): the delegate's */
    @Override
    public void method_22901(int r, int g, int b, int a) {
        CbRenderVertex.cbDefaultColor(this.cbDelegate(), r, g, b, a);
    }

    /** unsetDefaultColor(): the delegate's */
    @Override
    public void method_35666() {
        CbRenderVertex.cbUnsetDefaultColor(this.cbDelegate());
    }

    /** vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz): the delegate's, the UVs through the sprite */
    @Override
    public void method_23919(float x, float y, float z, float r, float g, float b, float a, float u, float v, int overlay, int light,
                             float nx, float ny, float nz) {
        net.minecraft.class_1058 sprite = ((CbRenderSpriteAccess) (Object) this).cb$sprite();
        CbRenderVertex.cbBulkVertex(this.cbDelegate(), x, y, z, r, g, b, a, sprite.method_4580(u), sprite.method_4570(v), overlay, light,
            nx, ny, nz);
    }
}
