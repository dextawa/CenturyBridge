package top.dext.centurybridge.step.v26_2.rt;

import java.util.Optional;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ARGB;
import org.joml.Vector4fc;

/**
 * 26.1.2 command-encoder clear colours were one ARGB int (GlCommandEncoder: glClearColor(ARGB.redFloat(c),
 * greenFloat, blueFloat, alphaFloat)); 26.2 takes the same four floats as a Vector4fc (r, g, b, a), and a render
 * pass's optional clear colour is an Optional of it where 26.1.2's was an OptionalInt. ARGB.vector4fFromARGB32 is
 * 26.2's own conversion of that int into those floats (redFloat, greenFloat, blueFloat, alphaFloat).
 */
@Environment(EnvType.CLIENT)
public final class CbS26CommandEncoderClient {
    private CbS26CommandEncoderClient() {}

    public static Vector4fc color(int argb) {
        return ARGB.vector4fFromARGB32(argb);
    }

    public static Optional<Vector4fc> clearColor(OptionalInt argb) {
        return argb.isPresent() ? Optional.of(ARGB.vector4fFromARGB32(argb.getAsInt())) : Optional.empty();
    }
}
