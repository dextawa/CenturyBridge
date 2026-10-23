package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Arguments 1.21.2's renderer / layer constructors take that 1.21.1's did not, or took in another form
 * (superCtorReshapes, for a mod's new site and a mod subclass's super call alike):
 * - an ItemRenderer where 1.21.1 took the ItemInHandRenderer that drew the items: that renderer's own ItemRenderer;
 * - an EquipmentLayerRenderer (1.21.2's armour drawing): built as 1.21.2's EntityRendererProvider.Context builds it,
 *   from the game's equipment models and the armour-trim atlas of the model manager -- the given ModelManager where
 *   1.21.1 took one (HumanoidArmorLayer), else the game's;
 * - the game's EntityModelSet / equipment models / map renderer where 1.21.2 asks for them and 1.21.1's class took
 *   them from the game itself (CapeLayer, Deadmau5EarsLayer, the renderer context, EntityRenderDispatcher).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderCtors {

    /** ItemInHandRenderer's ItemRenderer (CbRenderItemInHandAccess) */
    public interface ItemInHand {
        net.minecraft.class_918 cb$itemRenderer();
    }

    private CbRenderCtors() {
    }

    public static net.minecraft.class_918 itemRenderer(net.minecraft.class_759 itemInHandRenderer) {
        return ((ItemInHand) itemInHandRenderer).cb$itemRenderer();
    }

    public static net.minecraft.class_10197 equipmentRenderer(net.minecraft.class_1092 modelManager) {
        return new net.minecraft.class_10197(net.minecraft.class_310.method_1551().method_64036(),
            modelManager.method_24153(net.minecraft.class_4722.field_42071));
    }

    public static net.minecraft.class_10197 equipmentRenderer() {
        return equipmentRenderer(net.minecraft.class_310.method_1551().method_1554());
    }

    public static net.minecraft.class_5599 modelSet() {
        return net.minecraft.class_310.method_1551().method_31974();
    }

    public static net.minecraft.class_10201 equipmentAssets() {
        return net.minecraft.class_310.method_1551().method_64036();
    }

    public static net.minecraft.class_330 mapRenderer() {
        return net.minecraft.class_310.method_1551().method_61965();
    }

    /**
     * new BakedGlyph.Effect(x0, y0, x1, y1, depth, r, g, b, a): 1.21.2's record keeps the colour packed. 1.21.1 drew the
     * effect with VertexConsumer.setColor(r, g, b, a), which stored (int) (channel * 255) of each as a byte: packed the
     * same way.
     */
    public static net.minecraft.class_382.class_328 glyphEffect(float x0, float y0, float x1, float y1, float depth, float r, float g, float b,
                                                               float a) {
        int color = ((int) (a * 255.0F) & 0xFF) << 24 | ((int) (r * 255.0F) & 0xFF) << 16 | ((int) (g * 255.0F) & 0xFF) << 8
            | (int) (b * 255.0F) & 0xFF;
        return new net.minecraft.class_382.class_328(x0, y0, x1, y1, depth, color);
    }
}
