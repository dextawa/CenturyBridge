package top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

/**
 * Fabric API 1.21.3's MeshBuilder on 1.21.4, whose renderer hands out a MutableMesh instead: the builder's emitter is the mutable
 * mesh's, and build() answers an immutable copy of the quads emitted so far and empties the builder, as 1.21.3's did.
 */
@Environment(EnvType.CLIENT)
public interface MeshBuilder {
    QuadEmitter getEmitter();

    Mesh build();
}
