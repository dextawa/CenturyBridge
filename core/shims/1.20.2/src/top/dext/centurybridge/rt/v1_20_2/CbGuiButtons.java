package top.dext.centurybridge.rt.v1_20_2;

/**
 * ctorRedirects and staticRedirects of the 1.20.1 texture-sheet buttons: `new ImageButton(..)` with a
 * 1.20.1 constructor builds the legacy ImageButton (facade, a real 1.20.2 ImageButton), and 1.20.1
 * CommonButtons' TextAndImageButton factories build the TextAndImageButton facade exactly as 1.20.1 did
 * (the same label, sheet and coordinates; the sheets exist again through CbGuiLegacyTextures).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiButtons {

    /** was net/minecraft/class_344.<init>(IIIIIILnet/minecraft/class_2960;Lnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int x, int y, int width, int height, int xTexStart, int yTexStart,
                                                         net.minecraft.class_2960 texture, net.minecraft.class_4185.class_4241 onPress) {
        return new top.dext.centurybridge.facade.v1_20_2.class_344(x, y, width, height, xTexStart, yTexStart, texture, onPress);
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;Lnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int x, int y, int width, int height, int xTexStart, int yTexStart,
                                                         int yDiffTex, net.minecraft.class_2960 texture,
                                                         net.minecraft.class_4185.class_4241 onPress) {
        return new top.dext.centurybridge.facade.v1_20_2.class_344(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture, onPress);
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;IILnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int x, int y, int width, int height, int xTexStart, int yTexStart,
                                                         int yDiffTex, net.minecraft.class_2960 texture, int textureWidth,
                                                         int textureHeight, net.minecraft.class_4185.class_4241 onPress) {
        return new top.dext.centurybridge.facade.v1_20_2.class_344(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture,
            textureWidth, textureHeight, onPress);
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;IILnet/minecraft/class_4185$class_4241;Lnet/minecraft/class_2561;)V */
    public static net.minecraft.class_344 cbNewclass_344(int x, int y, int width, int height, int xTexStart, int yTexStart,
                                                         int yDiffTex, net.minecraft.class_2960 texture, int textureWidth,
                                                         int textureHeight, net.minecraft.class_4185.class_4241 onPress,
                                                         net.minecraft.class_2561 message) {
        return new top.dext.centurybridge.facade.v1_20_2.class_344(x, y, width, height, xTexStart, yTexStart, yDiffTex, texture,
            textureWidth, textureHeight, onPress, message);
    }

    private static final net.minecraft.class_2960 WIDGETS = new net.minecraft.class_2960("textures/gui/widgets.png");
    private static final net.minecraft.class_2960 ACCESSIBILITY = new net.minecraft.class_2960("textures/gui/accessibility.png");

    /** was net/minecraft/class_8082.method_48592 CommonButtons.languageTextAndImage(onPress) */
    public static top.dext.centurybridge.facade.v1_20_2.class_8084 method_48592(net.minecraft.class_4185.class_4241 onPress) {
        return top.dext.centurybridge.facade.v1_20_2.class_8084.method_48601(net.minecraft.class_2561.method_43471("options.language"),
                WIDGETS, onPress)
            .method_48605(3, 109).method_48606(65, 3).method_48604(20).method_48607(14, 14).method_48608(256, 256).method_48603();
    }

    /** was net/minecraft/class_8082.method_48594 CommonButtons.accessibilityTextAndImage(onPress) */
    public static top.dext.centurybridge.facade.v1_20_2.class_8084 method_48594(net.minecraft.class_4185.class_4241 onPress) {
        return top.dext.centurybridge.facade.v1_20_2.class_8084.method_48601(
                net.minecraft.class_2561.method_43471("options.accessibility.title"), ACCESSIBILITY, onPress)
            .method_48605(3, 2).method_48606(65, 2).method_48604(20).method_48607(14, 16).method_48608(32, 64).method_48603();
    }

    private CbGuiButtons() {
    }
}
