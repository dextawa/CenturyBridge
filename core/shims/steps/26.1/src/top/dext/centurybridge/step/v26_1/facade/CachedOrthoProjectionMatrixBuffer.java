package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;

/**
 * 1.21.11's CachedOrthoProjectionMatrixBuffer (net/minecraft/class_11278) on 26.1: a projection uniform buffer
 * holding an orthographic matrix for (0, width) x (0, height) -- y flipped when invertY -- and the constructor's
 * depth range, rewritten only when the size changes. 26.1 split it into a Projection (the camera parameters, which
 * rebuild their matrix when changed) and a ProjectionMatrixBuffer (the uniform buffer, rewritten when the projection's
 * matrix changed); this is the pair, set up the way 1.21.11's built its matrix.
 */
@Environment(EnvType.CLIENT)
public class CachedOrthoProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    private final float cb$zNear;
    private final float cb$zFar;
    private final boolean cb$invertY;

    public CachedOrthoProjectionMatrixBuffer(String name, float zNear, float zFar, boolean invertY) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.cb$zNear = zNear;
        this.cb$zFar = zFar;
        this.cb$invertY = invertY;
    }

    public GpuBufferSlice getBuffer(float width, float height) {
        cb$projection.setupOrtho(cb$zNear, cb$zFar, width, height, cb$invertY);
        return cb$buffer.getBuffer(cb$projection);
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
