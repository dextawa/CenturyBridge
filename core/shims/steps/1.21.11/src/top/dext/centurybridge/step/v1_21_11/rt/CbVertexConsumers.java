package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
import net.minecraft.class_9848;

/**
 * VertexConsumer members 1.21.11 demands of an implementation that a 1.21.10 one cannot have (javap of both jars):
 * setColor(int) (class_4588.method_39415) was a default in 1.21.10 and is abstract in 1.21.11; setLineWidth(float)
 * (method_75298) is new and abstract. The abstract fills (smx1-vertex-matrix.json) give a 1.21.10 implementation
 * 1.21.10's answers.
 */
@Environment(EnvType.CLIENT)
public final class CbVertexConsumers {
    private CbVertexConsumers() {
    }

    /** 1.21.10's default setColor(int): setColor(red, green, blue, alpha) of the packed ARGB value */
    public static class_4588 setColor(class_4588 consumer, int argb) {
        return consumer.method_1336(class_9848.method_61327(argb), class_9848.method_61329(argb), class_9848.method_61331(argb),
            class_9848.method_61320(argb));
    }

    /** 1.21.10 knew no line width per vertex: a 1.21.10 consumer writes none (lines take their render type's width) */
    public static class_4588 setLineWidth(class_4588 consumer, float width) {
        return consumer;
    }
}
