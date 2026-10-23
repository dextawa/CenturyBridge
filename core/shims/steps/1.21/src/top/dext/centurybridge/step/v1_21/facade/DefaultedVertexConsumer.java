package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.rt.CbRenderLegacyConsumer;

/**
 * 1.20.6's DefaultedVertexConsumer (class_4585, gone in 1.21): a VertexConsumer that keeps a default color
 * (opaque white until set) for its subclass to use; defaultColor(..) sets it, unsetDefaultColor() lets it go. A
 * 1.20.6 subclass is a 1.20.6 consumer (CbRenderLegacyConsumer). The fields 1.20.6 kept protected are public
 * here: a mod's access widener may name them, and wideners never reach a CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public abstract class DefaultedVertexConsumer implements CbRenderLegacyConsumer {

    /** defaultColorSet */
    public boolean field_20889;

    /** defaultR */
    public int field_20890 = 255;

    /** defaultG */
    public int field_20891 = 255;

    /** defaultB */
    public int field_20892 = 255;

    /** defaultA */
    public int field_20893 = 255;

    public DefaultedVertexConsumer() {
    }

    /** defaultColor(r, g, b, a) */
    @Override
    public void method_22901(int r, int g, int b, int a) {
        this.field_20890 = r;
        this.field_20891 = g;
        this.field_20892 = b;
        this.field_20893 = a;
        this.field_20889 = true;
    }

    /** unsetDefaultColor() */
    @Override
    public void method_35666() {
        this.field_20889 = false;
    }
}
