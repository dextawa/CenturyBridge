package top.dext.centurybridge.shims.v1_21_1;

import net.minecraft.class_490;
import net.minecraft.class_332;
import net.minecraft.class_1309;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

/**
 * class_490 (EntityRenderer): drawEntity statics gained parameters at 1.20.2.
 *
 * Old 9-arg:  drawEntity(ctx, x, y, size, mouseX, mouseY, entity)
 * New 10-arg: drawEntity(ctx, x, y, size, mouseX, mouseY, mouseX-delta, entity)  -- synthetic float
 * Actually:
 *   1.20.1: method_2486(DrawContext, int x, int y, int size, float mouseX, float mouseY, EntityModel)
 *   1.21.1: method_2486(DrawContext, int x, int y, int, int, int, float, float, float, EntityModel)
 *
 * We add the old descriptor as a shim overload that calls the new one with
 * zero for the three extra int parameters (light/overlay unused in basic render path).
 *
 * method_48472 similarly gained a leading float + Vector3f in 1.20.2.
 */
@Mixin(class_490.class)
public abstract class EntityRendererBridge {

    public static void method_2486(class_332 context,
                                    int x, int y, int size,
                                    float mouseX, float mouseY,
                                    class_1309 entity) {
        // new signature: (ctx, x, y, size, overlayLight, blockLight, mouseX, mouseY, yaw, entity)
        // pass zeros for the new int params; yaw from mouseX delta = 0
        class_490.method_2486(context, x, y, size, 0, 0, mouseX, mouseY, 0.0f, entity);
    }

    public static void method_48472(class_332 context,
                                     int x, int y, int size,
                                     Quaternionf rotation,
                                     Quaternionf cameraAngle,
                                     class_1309 entity) {
        // new: (ctx, float x, float y, int size, Vector3f pivot, Quaternionf rot, Quaternionf cam, entity)
        class_490.method_48472(context, (float) x, (float) y, size,
                               new Vector3f(0, 0, 0), rotation, cameraAngle, entity);
    }
}
