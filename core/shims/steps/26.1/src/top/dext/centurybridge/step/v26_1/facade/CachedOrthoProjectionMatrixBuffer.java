package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;

/**
 * 1.21.11's CachedOrthoProjectionMatrixBuffer (net/minecraft/class_11278) on 26.1: a projection uniform buffer
 * holding an orthographic matrix for (0, width) x (0, height) -- y flipped when invertY -- and the constructor's
 * depth range, rewritten only when the size changes. 26.1 split it into a Projection (the camera parameters, which
 * rebuild their matrix when changed) and a ProjectionMatrixBuffer (the uniform buffer, rewritten when the projection's
 * matrix changed); this is the pair, set up the way 1.21.11's built its matrix.
 * <p>
 * 1.21.11's private fields (mods' access wideners) are this class's: buffer / bufferSlice are the ProjectionMatrixBuffer's
 * (it creates and slices its uniform buffer once, as 1.21.11's constructor did), zNear / zFar / invertY the constructor's,
 * width / height the last size asked for. createProjectionMatrix(width, height) is the matrix the projection uploads for
 * that size (26.1's Projection.getMatrix, which follows 26.1's depth convention as every 26.1 projection does).
 */
@Environment(EnvType.CLIENT)
public class CachedOrthoProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    public GpuBuffer buffer;
    public GpuBufferSlice bufferSlice;
    public float zNear;
    public float zFar;
    public boolean invertY;
    public float width;
    public float height;

    public CachedOrthoProjectionMatrixBuffer(String name, float zNear, float zFar, boolean invertY) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.buffer = this.cb$buffer.buffer;
        this.bufferSlice = this.cb$buffer.bufferSlice;
        this.zNear = zNear;
        this.zFar = zFar;
        this.invertY = invertY;
    }

    public GpuBufferSlice getBuffer(float width, float height) {
        this.width = width;
        this.height = height;
        cb$projection.setupOrtho(zNear, zFar, width, height, invertY);
        return cb$buffer.getBuffer(cb$projection);
    }

    public Matrix4f createProjectionMatrix(float width, float height) {
        Projection p = new Projection();
        p.setupOrtho(zNear, zFar, width, height, invertY);
        return p.getMatrix(new Matrix4f());
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
