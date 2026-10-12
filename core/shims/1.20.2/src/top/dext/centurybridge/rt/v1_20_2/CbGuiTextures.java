package top.dext.centurybridge.rt.v1_20_2;

/**
 * fieldGetters for the 1.20.1 GUI texture ids whose fields 1.20.2 removed. Each returns the id the
 * field held in 1.20.1 (read off the 1.20.1 static initializers); the texture behind it is the 1.20.1
 * composite, rebuilt from 1.20.2's sprites when 1.20.2 no longer ships it (CbGuiLegacyTextures).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiTextures {

    private static final net.minecraft.class_2960 TOASTS = new net.minecraft.class_2960("textures/gui/toasts.png");
    private static final net.minecraft.class_2960 SPECTATOR_WIDGETS = new net.minecraft.class_2960("textures/gui/spectator_widgets.png");
    private static final net.minecraft.class_2960 BUNDLE = new net.minecraft.class_2960("textures/gui/container/bundle.png");
    private static final net.minecraft.class_2960 SOCIAL_INTERACTIONS = new net.minecraft.class_2960("textures/gui/social_interactions.png");
    private static final net.minecraft.class_2960 ACCESSIBILITY = new net.minecraft.class_2960("textures/gui/accessibility.png");

    /** was net/minecraft/class_368.field_2207 Toast.TEXTURE */
    public static net.minecraft.class_2960 field_2207() {
        return TOASTS;
    }

    /** was net/minecraft/class_365.field_2199 SpectatorGui.SPECTATOR_LOCATION */
    public static net.minecraft.class_2960 field_2199() {
        return SPECTATOR_WIDGETS;
    }

    /** was net/minecraft/class_5682.field_28359 ClientBundleTooltip.TEXTURE_LOCATION */
    public static net.minecraft.class_2960 field_28359() {
        return BUNDLE;
    }

    /** was net/minecraft/class_5522.field_26875 SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION (protected) */
    public static net.minecraft.class_2960 field_26875() {
        return SOCIAL_INTERACTIONS;
    }

    /** was net/minecraft/class_339.field_42117 AbstractWidget.ACCESSIBILITY_TEXTURE */
    public static net.minecraft.class_2960 field_42117() {
        return ACCESSIBILITY;
    }

    private CbGuiTextures() {
    }
}
