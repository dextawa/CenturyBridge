package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 TextAndImageButton.Builder (net/minecraft/class_8084$class_8085), with its 1.20.1 defaults
 * (every coordinate 0 until set). See {@link class_8084}.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_8084$class_8085 {

    public final net.minecraft.class_2561 field_42127;
    public final net.minecraft.class_2960 field_42128;
    public final net.minecraft.class_4185.class_4241 field_42129;
    /** xTexStart, yTexStart */
    public int field_42130;
    public int field_42131;
    /** yDiffTex */
    public int field_42132;
    /** usedTextureWidth, usedTextureHeight */
    public int field_42133;
    public int field_42134;
    /** textureWidth, textureHeight */
    public int field_42135;
    public int field_42136;
    /** xOffset, yOffset */
    public int field_42137;
    public int field_42138;

    public class_8084$class_8085(net.minecraft.class_2561 message, net.minecraft.class_2960 texture,
                                 net.minecraft.class_4185.class_4241 onPress) {
        this.field_42127 = message;
        this.field_42128 = texture;
        this.field_42129 = onPress;
    }

    /** texStart(x, y) */
    public class_8084$class_8085 method_48605(int x, int y) {
        this.field_42130 = x;
        this.field_42131 = y;
        return this;
    }

    /** offset(x, y) */
    public class_8084$class_8085 method_48606(int x, int y) {
        this.field_42137 = x;
        this.field_42138 = y;
        return this;
    }

    /** yDiffTex(yDiffTex) */
    public class_8084$class_8085 method_48604(int yDiffTex) {
        this.field_42132 = yDiffTex;
        return this;
    }

    /** usedTextureSize(width, height) */
    public class_8084$class_8085 method_48607(int width, int height) {
        this.field_42133 = width;
        this.field_42134 = height;
        return this;
    }

    /** textureSize(width, height) */
    public class_8084$class_8085 method_48608(int width, int height) {
        this.field_42135 = width;
        this.field_42136 = height;
        return this;
    }

    /** build() */
    public class_8084 method_48603() {
        return new class_8084(this.field_42127, this.field_42130, this.field_42131, this.field_42137, this.field_42138,
            this.field_42132, this.field_42133, this.field_42134, this.field_42135, this.field_42136, this.field_42128,
            this.field_42129);
    }
}
