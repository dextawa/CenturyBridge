package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Arguments 1.21.6 added to a method whose 1.21.5 shape used a fixed value there; each answers what 1.21.5's body used
 * (javap of both). The step's callReshapes push them for 1.21.5 calls, and its frameDefaults hand them to a 1.21.5
 * override's rewritten super call when the mod entered its old method itself (no frame from vanilla).
 * <ul>
 * <li>ClientLevel.disconnect(): 1.21.5 disconnected the connection with Component.translatable("multiplayer.status.quitting");
 *     1.21.6's disconnect(Component reason) passes the reason it is given.</li>
 * <li>GlTexture.flushModeChanges(): 1.21.5 set every texture parameter on GL_TEXTURE_2D (3553); 1.21.6's
 *     flushModeChanges(int target) sets them on the target.</li>
 * <li>RecipeProvider.colorItemWithDye(dyes, items, group): 1.21.5 built each recipe under RecipeCategory.BUILDING_BLOCKS
 *     (field_40634); 1.21.6's takes the category.</li>
 * </ul>
 */
public final class CbCallDefaults {

    private CbCallDefaults() {
    }

    public static net.minecraft.class_2561 quitting() {
        return net.minecraft.class_2561.method_43471("multiplayer.status.quitting");
    }

    public static int texture2d() {
        return 3553;
    }

    public static net.minecraft.class_7800 dyeCategory() {
        return net.minecraft.class_7800.field_40634;
    }
}
