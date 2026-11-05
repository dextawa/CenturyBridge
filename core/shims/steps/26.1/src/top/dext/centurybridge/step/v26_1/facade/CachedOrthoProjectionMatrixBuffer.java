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
 * 1.21.11's private fields (mods' access wideners) are this class's, under their intermediary names (field_60059..field_60065, method_71094): buffer / bufferSlice are the ProjectionMatrixBuffer's
 * (it creates and slices its uniform buffer once, as 1.21.11's constructor did), zNear / zFar / invertY the constructor's,
 * width / height the last size asked for. createProjectionMatrix(width, height) is the matrix the projection uploads for
 * that size (26.1's Projection.getMatrix, which follows 26.1's depth convention as every 26.1 projection does).
 */
@Environment(EnvType.CLIENT)
public class CachedOrthoProjectionMatrixBuffer implements AutoCloseable {

    private final ProjectionMatrixBuffer cb$buffer;
    private final Projection cb$projection = new Projection();
    public GpuBuffer field_60059;
    public GpuBufferSlice field_60060;
    public float field_60061;
    public float field_60062;
    public boolean field_60063;
    public float field_60064;
    public float field_60065;

    public CachedOrthoProjectionMatrixBuffer(String name, float zNear, float zFar, boolean invertY) {
        this.cb$buffer = new ProjectionMatrixBuffer(name);
        this.field_60059 = this.cb$buffer.buffer;
        this.field_60060 = this.cb$buffer.bufferSlice;
        this.field_60061 = zNear;
        this.field_60062 = zFar;
        this.field_60063 = invertY;
    }

    public GpuBufferSlice getBuffer(float width, float height) {
        this.field_60064 = width;
        this.field_60065 = height;
        cb$projection.setupOrtho(field_60061, field_60062, width, height, field_60063);
        return cb$buffer.getBuffer(cb$projection);
    }

    public Matrix4f method_71094(float width, float height) {
        Projection p = new Projection();
        p.setupOrtho(field_60061, field_60062, width, height, field_60063);
        return p.getMatrix(new Matrix4f());
    }

    @Override
    public void close() {
        cb$buffer.close();
    }
}
