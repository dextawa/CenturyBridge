package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * DirectStateAccess.bufferSubData (method_71027) gained a fourth argument at 1.21.9: the buffer's usage flags
 * (GlCommandEncoder.writeToBuffer passes GlBuffer.usage()). The ARB implementation ignores it; the emulated one binds
 * the buffer to the target its usage names (bit 32 -> 34962, 64 -> 34963, 128 -> 35345) and otherwise to 36663
 * (GL_COPY_WRITE_BUFFER), the only target 1.21.8's emulated body bound (javap -c -p of both code jars). A 1.21.8 call
 * has no usage to give; usage 0 selects exactly 1.21.8's target on either implementation.
 */
@Environment(EnvType.CLIENT)
public final class CbDirectStateAccess {

    private CbDirectStateAccess() {
    }

    public static int legacyUsage() {
        return 0;
    }
}
