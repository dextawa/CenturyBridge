package top.dext.centurybridge.step.v1_21_11.rt;

import net.minecraft.class_4590;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * 1.21.10 Transformation (class_4590) private fields read/written through a mod's access widener. 1.21.11 keeps the
 * same four fields (translation field_20902, leftRotation field_20903, scale field_20904, rightRotation field_20905) but
 * typed Vector3fc / Quaternionfc; its constructor and decomposition (method_22938) store either the caller's object or
 * a fresh Vector3f / Quaternionf (javap -c -p of both code jars), so the value is normally the mutable class 1.21.10
 * declared and is handed back as the same object. A read-only view another caller passed in is copied (a mod's write
 * into the copy does not reach the Transformation). Like 1.21.10's raw field read, nothing forces decomposition: a
 * matrix-built instance answers null until decomposed. Writes store the mod's object as is (Vector3f is a Vector3fc).
 */
public final class CbLegacyTransformation {

    private static Vector3f vec(Vector3fc v) {
        if (v == null || v instanceof Vector3f) {
            return (Vector3f) v;
        }
        return new Vector3f(v);
    }

    private static Quaternionf quat(Quaternionfc q) {
        if (q == null || q instanceof Quaternionf) {
            return (Quaternionf) q;
        }
        return new Quaternionf(q);
    }

    /** translation */
    public static Vector3f field_20902(class_4590 t) {
        return vec(t.field_20902);
    }

    public static void field_20902(class_4590 t, Vector3f v) {
        t.field_20902 = v;
    }

    /** leftRotation */
    public static Quaternionf field_20903(class_4590 t) {
        return quat(t.field_20903);
    }

    public static void field_20903(class_4590 t, Quaternionf q) {
        t.field_20903 = q;
    }

    /** scale */
    public static Vector3f field_20904(class_4590 t) {
        return vec(t.field_20904);
    }

    public static void field_20904(class_4590 t, Vector3f v) {
        t.field_20904 = v;
    }

    /** rightRotation */
    public static Quaternionf field_20905(class_4590 t) {
        return quat(t.field_20905);
    }

    public static void field_20905(class_4590 t, Quaternionf q) {
        t.field_20905 = q;
    }

    /** compose(translation, leftRotation, scale, rightRotation): 1.21.11's same body over the widened parameter types */
    public static Matrix4f method_22934(Vector3f translation, Quaternionf left, Vector3f scale, Quaternionf right) {
        return class_4590.method_22934(translation, left, scale, right);
    }

    private CbLegacyTransformation() {
    }
}
