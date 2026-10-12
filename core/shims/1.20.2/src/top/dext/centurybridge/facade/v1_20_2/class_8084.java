package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 TextAndImageButton (net/minecraft/class_8084), removed in 1.20.2 (SpriteIconButton draws GUI
 * atlas sprites instead): a 150x20 button showing a region of a texture sheet next to its scrolling
 * label. The 1.20.1 class, on the 1.20.2 Button: renderWidget draws the button and then the sheet
 * region through renderTexture (CbLegacyRenderTexture), renderString scrolls the label in the space
 * left of the image. The 1.20.1 bodies; the scrolling-string call takes 1.20.2's form, whose centre
 * argument is the midpoint 1.20.1 computed. Reached through classRenames (with its Builder).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_8084 extends net.minecraft.class_4185 {

    /** resourceLocation */
    public final net.minecraft.class_2960 field_43070;
    /** xTexStart */
    public final int field_43071;
    /** yTexStart */
    public final int field_43072;
    /** yDiffTex */
    public final int field_43073;
    /** textureWidth */
    public final int field_43074;
    /** textureHeight */
    public final int field_43075;
    /** xOffset */
    public final int field_42123;
    /** yOffset */
    public final int field_42124;
    /** usedTextureWidth */
    public final int field_42125;
    /** usedTextureHeight */
    public final int field_42126;

    /** 1.20.1's package-private constructor (reached through the Builder; public, as every member a
     *  facade stands in for: a mod's own access widener on the dead class cannot reach this one) */
    public class_8084(net.minecraft.class_2561 message, int xTexStart, int yTexStart, int xOffset, int yOffset, int yDiffTex,
               int usedTextureWidth, int usedTextureHeight, int textureWidth, int textureHeight,
               net.minecraft.class_2960 texture, net.minecraft.class_4185.class_4241 onPress) {
        super(0, 0, 150, 20, message, onPress, net.minecraft.class_4185.field_40754);
        this.field_43074 = textureWidth;
        this.field_43075 = textureHeight;
        this.field_43071 = xTexStart;
        this.field_43072 = yTexStart;
        this.field_43073 = yDiffTex;
        this.field_43070 = texture;
        this.field_42123 = xOffset;
        this.field_42124 = yOffset;
        this.field_42125 = usedTextureWidth;
        this.field_42126 = usedTextureHeight;
    }

    /** renderWidget: the button, then the sheet region */
    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta) {
        super.method_48579(graphics, mouseX, mouseY, delta);
        ((top.dext.centurybridge.rt.v1_20_2.CbLegacyRenderTexture) (Object) this).method_48588(graphics, this.field_43070,
            this.method_48600(), this.method_48602(), this.field_43071, this.field_43072, this.field_43073,
            this.field_42125, this.field_42126, this.field_43074, this.field_43075);
    }

    /** renderString: the label scrolls between x + 2 and the image */
    @Override
    public void method_48589(net.minecraft.class_332 graphics, net.minecraft.class_327 font, int color) {
        int left = this.method_46426() + 2;
        int right = this.method_46426() + this.method_25368() - this.field_42125 - 6;
        net.minecraft.class_339.method_49605(graphics, font, this.method_25369(), (left + right) / 2, left, this.method_46427(),
            right, this.method_46427() + this.method_25364(), color);
    }

    /** getTextureX */
    public int method_48600() {
        return this.method_46426() + (this.field_22758 / 2 - this.field_42125 / 2) + this.field_42123;
    }

    /** getTextureY */
    public int method_48602() {
        return this.method_46427() + this.field_42124;
    }

    /** builder(message, texture, onPress) */
    public static class_8084$class_8085 method_48601(net.minecraft.class_2561 message, net.minecraft.class_2960 texture,
                                                    net.minecraft.class_4185.class_4241 onPress) {
        return new class_8084$class_8085(message, texture, onPress);
    }
}
