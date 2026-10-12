package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 ImageButton (net/minecraft/class_344): a button drawn from a texture sheet -- the region at
 * (xTexStart, yTexStart) of the given texture, moved down by yDiffTex when hovered or focused and by
 * twice that when inactive. 1.20.2's ImageButton draws WidgetSprites from the GUI atlas instead and lost
 * the sheet constructors and fields. This is a real 1.20.2 ImageButton (it extends it, so vanilla takes
 * it and instanceof holds) carrying the 1.20.1 constructors, the 1.20.1 protected fields and the 1.20.1
 * renderWidget: renderTexture called on this widget (CbLegacyRenderTexture, so a subclass override
 * still runs). `new ImageButton(..)` reaches it through ctorRedirects (CbGuiButtons), a mod subclass
 * through superRenames. The sprites handed to the 1.20.2 super constructor are never drawn: this
 * renderWidget replaces the one that draws them.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_344 extends net.minecraft.class_344 {

    /** resourceLocation */
    protected final net.minecraft.class_2960 field_2127;
    /** xTexStart */
    protected final int field_2126;
    /** yTexStart */
    protected final int field_2125;
    /** yDiffTex */
    protected final int field_19079;
    /** textureWidth */
    protected final int field_2124;
    /** textureHeight */
    protected final int field_19080;

    /** ImageButton(x, y, width, height, xTexStart, yTexStart, texture, onPress): yDiffTex = height, a 256x256 sheet */
    public class_344(int x, int y, int width, int height, int xTexStart, int yTexStart, net.minecraft.class_2960 texture,
                     net.minecraft.class_4185.class_4241 onPress) {
        this(x, y, width, height, xTexStart, yTexStart, height, texture, 256, 256, onPress);
    }

    /** ImageButton(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, onPress): a 256x256 sheet */
    public class_344(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
                     net.minecraft.class_2960 texture, net.minecraft.class_4185.class_4241 onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, 256, 256, onPress);
    }

    /** ImageButton(.., textureWidth, textureHeight, onPress): an empty message */
    public class_344(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
                     net.minecraft.class_2960 texture, int textureWidth, int textureHeight, net.minecraft.class_4185.class_4241 onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, textureWidth, textureHeight, onPress,
            net.minecraft.class_5244.field_39003);
    }

    public class_344(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
                     net.minecraft.class_2960 texture, int textureWidth, int textureHeight, net.minecraft.class_4185.class_4241 onPress,
                     net.minecraft.class_2561 message) {
        super(x, y, width, height, new net.minecraft.class_8666(texture, texture), onPress, message);
        this.field_2124 = textureWidth;
        this.field_19080 = textureHeight;
        this.field_2126 = xTexStart;
        this.field_2125 = yTexStart;
        this.field_19079 = yDiffTex;
        this.field_2127 = texture;
    }

    /** 1.20.1 renderWidget: the sheet region through renderTexture */
    @Override
    public void method_48579(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta) {
        ((top.dext.centurybridge.rt.v1_20_2.CbLegacyRenderTexture) (Object) this).method_48588(graphics, this.field_2127,
            this.method_46426(), this.method_46427(), this.field_2126, this.field_2125, this.field_19079,
            this.field_22758, this.field_22759, this.field_2124, this.field_19080);
    }
}
