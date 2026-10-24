package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;

/**
 * 1.21.11's CachedPerspectiveProjectionMatrixBuffer (net/minecraft/class_11279) on 26.1: a projection uniform buffer
 * holding a perspective matrix (fov in degrees, width / height aspect, the constructor's depth range), rewritten only
 * when size or fov change. 26.1 split it into a Projection and a ProjectionMatrixBuffer (see
 * CachedOrthoProjectionMatrixBuffer); 26.1's perspective matrix also follows the device's depth range convention
 * (GpuDevice.isZZeroToOne), as every 26.1 projection does.
 */
@Environment(EnvType.CLIENT)
public class CachedPerspectiveProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    private final float cb$zNear;
    private final float cb$zFar;

    public CachedPerspectiveProjectionMatrixBuffer(String name, float zNear, float zFar) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.cb$zNear = zNear;
        this.cb$zFar = zFar;
    }

    public GpuBufferSlice getBuffer(int width, int height, float fov) {
        cb$projection.setupPerspective(cb$zNear, cb$zFar, fov, width, height);
        return cb$buffer.getBuffer(cb$projection);
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
