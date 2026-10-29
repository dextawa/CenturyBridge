package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringRepresentable;

/**
 * 1.21.11's public static EnumCodec CODEC fields 26.1 declares as Codec (client side). 26.1 still builds each with
 * StringRepresentable.fromEnum (javap: the putstatic of its EnumCodec), so the value is the EnumCodec 1.21.11 code reads
 * (fieldGetters; climbed layers' statics, e.g. the 1.21.5 layer's CbEntityVariants reading Fox.Variant.CODEC).
 */
@Environment(EnvType.CLIENT)
public final class CbWallEnumCodecsClient {
    private CbWallEnumCodecsClient() {
    }

    /** net.minecraft.client.renderer.UniformValue.Type.CODEC */
    @SuppressWarnings("unchecked")
    public static StringRepresentable.EnumCodec<net.minecraft.client.renderer.UniformValue.Type> uniformType() {
        return (StringRepresentable.EnumCodec<net.minecraft.client.renderer.UniformValue.Type>) net.minecraft.client.renderer.UniformValue.Type.CODEC;
    }

    /** net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.CODEC */
    @SuppressWarnings("unchecked")
    public static StringRepresentable.EnumCodec<net.minecraft.client.gui.components.debug.DebugScreenEntryStatus> debugEntryStatus() {
        return (StringRepresentable.EnumCodec<net.minecraft.client.gui.components.debug.DebugScreenEntryStatus>) net.minecraft.client.gui.components.debug.DebugScreenEntryStatus.CODEC;
    }
}
