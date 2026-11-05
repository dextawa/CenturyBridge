package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;

/**
 * 1.21.11's CachedPerspectiveProjectionMatrixBuffer (net/minecraft/class_11279) on 26.1: a projection uniform buffer
 * holding a perspective matrix (fov in degrees, width / height aspect, the constructor's depth range), rewritten only
 * when size or fov change. 26.1 split it into a Projection and a ProjectionMatrixBuffer (see
 * CachedOrthoProjectionMatrixBuffer); 26.1's perspective matrix also follows the device's depth range convention
 * (GpuDevice.isZZeroToOne), as every 26.1 projection does.
 * <p>
 * 1.21.11's private fields are this class's (buffer / bufferSlice the ProjectionMatrixBuffer's, zNear / zFar the
 * constructor's, width / height / fov the last asked for); createProjectionMatrix(width, height, fov) is the matrix the
 * projection uploads for them (see CachedOrthoProjectionMatrixBuffer).
 */
@Environment(EnvType.CLIENT)
public class CachedPerspectiveProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    public GpuBuffer buffer;
    public GpuBufferSlice bufferSlice;
    public float zNear;
    public float zFar;
    public int width;
    public int height;
    public float fov;

    public CachedPerspectiveProjectionMatrixBuffer(String name, float zNear, float zFar) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.buffer = this.cb$buffer.buffer;
        this.bufferSlice = this.cb$buffer.bufferSlice;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public GpuBufferSlice getBuffer(int width, int height, float fov) {
        this.width = width;
        this.height = height;
        this.fov = fov;
        cb$projection.setupPerspective(zNear, zFar, fov, width, height);
        return cb$buffer.getBuffer(cb$projection);
    }

    public Matrix4f createProjectionMatrix(int width, int height, float fov) {
        Projection p = new Projection();
        p.setupPerspective(zNear, zFar, fov, width, height);
        return p.getMatrix(new Matrix4f());
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
