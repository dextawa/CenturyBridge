package top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;

/**
 * Fabric API 1.21.3's RenderContext on 1.21.4, whose models get the emitter (which now holds the transform stack) and a cull test
 * directly. 1.21.3's QuadTransform is 1.21.4's mesh QuadTransform (same single method). The contexts the layer hands a 1.21.3
 * model are {@link CbFapiRenderer}'s.
 */
@Environment(EnvType.CLIENT)
public interface RenderContext {
    QuadEmitter getEmitter();

    boolean hasTransform();

    void pushTransform(QuadTransform transform);

    void popTransform();

    boolean isFaceCulled(net.minecraft.class_2350 face);

    net.minecraft.class_811 itemTransformationMode();

    /** 1.21.3's default: each mesh is output to the context's emitter */
    default Consumer<Mesh> meshConsumer() {
        return mesh -> mesh.outputTo(this.getEmitter());
    }
}
