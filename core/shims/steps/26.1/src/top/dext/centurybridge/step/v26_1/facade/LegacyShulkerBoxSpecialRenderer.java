package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_1.rt.CbWallShulkerBox;

/**
 * 1.21.11's ShulkerBoxSpecialRenderer: a shulker box special model that poses itself by its orientation. 26.1's
 * renderer draws unposed (26.1's item definitions carry the pose); this one is a 26.1 ShulkerBoxSpecialRenderer whose
 * submit and getExtents are 1.21.11's (javap class_10511): shulkerBoxRenderer.submit(pose, collector, light, overlay,
 * orientation, openness, null, material, outline) -- 26.1's ShulkerBoxRenderer.submit(.., Direction, ..), the same
 * body (push, modelTransform(orientation), draw, pop; the layer's access widener opens it) -- and
 * shulkerBoxRenderer.getExtents(orientation, openness, consumer) (rt/CbWallShulkerBox).
 * <p>
 * Made by 1.21.11's new ShulkerBoxSpecialRenderer(renderer, openness, orientation, material) (ctorRedirects), by a
 * 1.21.11 caller baking a ShulkerBoxSpecialRenderer.Unbaked (rt/CbWallItemModels#bakeShulkerBox), and the superclass
 * of a 1.21.11 mod's ShulkerBoxSpecialRenderer subclass (superRenames).
 */
@Environment(EnvType.CLIENT)
public class LegacyShulkerBoxSpecialRenderer extends ShulkerBoxSpecialRenderer {
    private final ShulkerBoxRenderer shulkerBoxRenderer;
    private final float openness;
    private final Direction orientation;
    private final SpriteId sprite;

    public LegacyShulkerBoxSpecialRenderer(ShulkerBoxRenderer shulkerBoxRenderer, float openness, Direction orientation, SpriteId sprite) {
        super(shulkerBoxRenderer, openness, sprite);
        this.shulkerBoxRenderer = shulkerBoxRenderer;
        this.openness = openness;
        this.orientation = orientation;
        this.sprite = sprite;
    }

    /**
     * A subclass's super.submit(displayContext, pose, collector, light, overlay, foil, outline): 1.21.11's body, the
     * context unread. Super calls into this class keep their intermediary names past the namespace wall (the
     * subclass's own methods take 26.1's), so super's members are declared under them.
     */
    public void method_65699(ItemDisplayContext displayContext, PoseStack pose, SubmitNodeCollector collector, int light, int overlay,
                             boolean hasFoil, int outline) {
        this.shulkerBoxRenderer.submit(pose, collector, light, overlay, this.orientation, this.openness, null, this.sprite, outline);
    }

    /** a subclass's super call of 26.1's submit shape (its override hook falling through to super) */
    public void method_65699(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outline) {
        this.shulkerBoxRenderer.submit(pose, collector, light, overlay, this.orientation, this.openness, null, this.sprite, outline);
    }

    /** a subclass's super.getExtents(consumer) */
    public void method_72175(Consumer<Vector3fc> consumer) {
        CbWallShulkerBox.getExtents(this.shulkerBoxRenderer, this.orientation, this.openness, consumer);
    }

    @Override
    public void submit(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outline) {
        this.shulkerBoxRenderer.submit(pose, collector, light, overlay, this.orientation, this.openness, null, this.sprite, outline);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        CbWallShulkerBox.getExtents(this.shulkerBoxRenderer, this.orientation, this.openness, consumer);
    }
}
