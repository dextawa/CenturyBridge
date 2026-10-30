package top.dext.centurybridge.step.v1_21_11.rt;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * 1.21.10 VertexConsumer.addVertex(Matrix4f, x, y, z) (class_4588.method_22918) takes a Matrix4fc in 1.21.11, same name,
 * same body over the read-only view (javap -c -p both). A converted call passes its Matrix4f on unchanged (it is a
 * Matrix4fc). A 1.21.10 override of the Matrix4f form is reached from 1.21.11's Matrix4fc form with the matrix vanilla
 * passed: the same object when it is a Matrix4f (every 1.21.11 vanilla caller passes one), a copy otherwise.
 */
public final class CbVertexMatrix {
    private CbVertexMatrix() {
    }

    /** the override hook's wrap: a Matrix4fc as the Matrix4f a 1.21.10 override was written for */
    public static Matrix4f mutable(Matrix4fc matrix) {
        if (matrix == null || matrix instanceof Matrix4f) {
            return (Matrix4f) matrix;
        }
        return new Matrix4f(matrix);
    }
}
