package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import top.dext.centurybridge.step.v26_1.facade.LegacyShulkerBoxSpecialRenderer;

/**
 * 1.21.11 ShulkerBoxRenderer.getExtents(orientation, openness, consumer) and ShulkerBoxSpecialRenderer(renderer,
 * openness, orientation, material) on 26.1's, whose shulker box special model has no orientation.
 */
@Environment(EnvType.CLIENT)
public final class CbWallShulkerBox {

    private CbWallShulkerBox() {
    }

    /**
     * 1.21.11's getExtents(orientation, openness, consumer), declared by a mod's shulker box renderer (adapterHooks: a
     * mod class under ShulkerBoxRenderer declaring it implements this).
     */
    public interface LegacyExtents {
        void getExtents(Direction orientation, float openness, Consumer<Vector3fc> consumer);
    }

    /** a mod's call of 1.21.11's getExtents(orientation, openness, consumer): a 1.21.11 override's answer, else 1.21.11's body */
    public static void getExtents(ShulkerBoxRenderer renderer, Direction orientation, float openness, Consumer<Vector3fc> consumer) {
        if (renderer instanceof LegacyExtents legacy) {
            legacy.getExtents(orientation, openness, consumer);
            return;
        }
        superGetExtents(renderer, orientation, openness, consumer);
    }

    /**
     * 1.21.11's body (javap class_834.method_72163): a fresh pose stack, prepareModel(pose, orientation, openness) --
     * the transform 26.1 keeps as modelTransform(orientation) (createModelTransform: the same translate, scale,
     * rotation, flip and offset) and the lid opened by openness -- then the model root's GUI extents in that pose.
     * 26.1's getExtents(openness, consumer) opens the lid and gives the extents unposed: each point is posed here.
     */
    public static void superGetExtents(ShulkerBoxRenderer renderer, Direction orientation, float openness, Consumer<Vector3fc> consumer) {
        Matrix4fc pose = ShulkerBoxRenderer.modelTransform(orientation).getMatrix();
        renderer.getExtents(openness, v -> consumer.accept(pose.transformPosition(v, new Vector3f())));
    }

    /** 1.21.11 new ShulkerBoxSpecialRenderer(renderer, openness, orientation, material): the renderer posing itself */
    public static ShulkerBoxSpecialRenderer renderer(ShulkerBoxRenderer renderer, float openness, Direction orientation, SpriteId sprite) {
        return new LegacyShulkerBoxSpecialRenderer(renderer, openness, orientation, sprite);
    }
}
