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
 * 1.21.11's private fields are this class's, under their intermediary names (field_60066..field_60072, method_71097; buffer / bufferSlice the ProjectionMatrixBuffer's, zNear / zFar the
 * constructor's, width / height / fov the last asked for); createProjectionMatrix(width, height, fov) is the matrix the
 * projection uploads for them (see CachedOrthoProjectionMatrixBuffer).
 */
@Environment(EnvType.CLIENT)
public class CachedPerspectiveProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    public GpuBuffer field_60066;
    public GpuBufferSlice field_60067;
    public float field_60068;
    public float field_60069;
    public int field_60070;
    public int field_60071;
    public float field_60072;

    public CachedPerspectiveProjectionMatrixBuffer(String name, float zNear, float zFar) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.field_60066 = this.cb$buffer.buffer;
        this.field_60067 = this.cb$buffer.bufferSlice;
        this.field_60068 = zNear;
        this.field_60069 = zFar;
    }

    public GpuBufferSlice getBuffer(int width, int height, float fov) {
        this.field_60070 = width;
        this.field_60071 = height;
        this.field_60072 = fov;
        cb$projection.setupPerspective(field_60068, field_60069, fov, width, height);
        return cb$buffer.getBuffer(cb$projection);
    }

    public Matrix4f method_71097(int width, int height, float fov) {
        Projection p = new Projection();
        p.setupPerspective(field_60068, field_60069, fov, width, height);
        return p.getMatrix(new Matrix4f());
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
