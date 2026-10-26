package top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer;

/**
 * Fabric API 1.21.5's renderer-v1 material.GlintMode (removed in 1.21.6's renderer-api 7.0, where a quad carries its
 * ItemStackRenderState.FoilType itself: QuadEmitter.glint, null = the item's default). Constants, field and fromGlint
 * as 1.21.5's (javap): DEFAULT null, NONE / STANDARD / SPECIAL the FoilType constants.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public enum GlintMode {
    DEFAULT(null),
    NONE(net.minecraft.class_10444.class_10445.field_55341),
    STANDARD(net.minecraft.class_10444.class_10445.field_55342),
    SPECIAL(net.minecraft.class_10444.class_10445.field_55343);

    public final net.minecraft.class_10444.class_10445 glint;

    GlintMode(net.minecraft.class_10444.class_10445 glint) {
        this.glint = glint;
    }

    public static GlintMode fromGlint(net.minecraft.class_10444.class_10445 glint) {
        if (glint == null) {
            return DEFAULT;
        }
        for (GlintMode mode : values()) {
            if (mode.glint == glint) {
                return mode;
            }
        }
        return DEFAULT;
    }
}
