package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Screen / AbstractContainerScreen statics of 1.21.5 that 1.21.6 no longer declares.
 * <ul>
 * <li>Screen.CUBE_MAP and Screen.PANORAMA: 1.21.5 built them in Screen's static initializer (a CubeMap of
 *     textures/gui/title/background/panorama and a PanoramaRenderer of it, javap); 1.21.6 builds the same two in
 *     GameRenderer (field_60579 / field_60580, the title screen and the other menu screens draw that one), so these are
 *     the game renderer's.</li>
 * <li>AbstractContainerScreen.SLOT_ITEM_BLIT_OFFSET: the constant 100 (1.21.5 javap).</li>
 * <li>Screen.method_57734 / ScreenRectangle.transformAxisAligned(Matrix4f): see CbScreenTooltipMixin and
 *     {@link #guiMatrix}.</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbScreens {

    private CbScreens() {
    }

    /** Screen.CUBE_MAP */
    public static net.minecraft.class_751 field_49508() {
        return net.minecraft.class_310.method_1551().field_1773.field_60579;
    }

    /** Screen.PANORAMA */
    public static net.minecraft.class_766 field_49509() {
        return net.minecraft.class_310.method_1551().field_1773.method_71649();
    }

    /** AbstractContainerScreen.SLOT_ITEM_BLIT_OFFSET */
    public static int field_32322() {
        return 100;
    }

    /**
     * ScreenRectangle.transformAxisAligned(Matrix4f): 1.21.5 transformed the corners as (x, y, 0) positions, so only the
     * matrix's XY part acts: x' = m00 x + m10 y + m30, y' = m01 x + m11 y + m31 -- 1.21.6's Matrix3x2f of the same
     * entries (1.21.5's identity shortcut gives what the identity gives).
     */
    public static org.joml.Matrix3x2f guiMatrix(org.joml.Matrix4f m) {
        return new org.joml.Matrix3x2f(m.m00(), m.m01(), m.m10(), m.m11(), m.m30(), m.m31());
    }
}
