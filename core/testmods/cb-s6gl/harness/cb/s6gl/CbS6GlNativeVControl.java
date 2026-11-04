package cb.s6gl;

import java.util.HashMap;

/** Runs only against the real 1.21.5 code jar: the control for identity and in-place mutation semantics. */
public final class CbS6GlNativeVControl {
    private static final class Pass extends net.minecraft.class_10866 {
        Pass() {
            super(null, false);
        }

        HashMap<String, Object> legacyUniforms() {
            return field_57873;
        }
    }

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    public static void main(String[] args) {
        Pass pass = new Pass();
        int[] ints = {1, 2};
        pass.setUniform("ints", ints);
        check(pass.legacyUniforms().get("ints") == ints, "V setUniform retains the caller's array object");
        ints[0] = 91;
        check(((int[]) pass.legacyUniforms().get("ints"))[0] == 91, "V observes in-place edits through the same array");

        int nanBits = 0x7fa12345;
        float[] floats = {Float.intBitsToFloat(nanBits)};
        pass.setUniform("floats", floats);
        check(pass.legacyUniforms().get("floats") == floats, "V retains the float array object");
        check(Float.floatToRawIntBits(((float[]) pass.legacyUniforms().get("floats"))[0]) == nanBits,
            "V retains raw NaN payload bits");
        System.out.println("CB_S6GL_NATIVE_V_CONTROL OK 4 checks");
    }
}
