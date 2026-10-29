package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.util.StringRepresentable;

/**
 * 1.21.11's public static EnumCodec CODEC fields 26.1 declares as Codec (common side). 26.1 still builds each with
 * StringRepresentable.fromEnum (javap: the putstatic of its EnumCodec), so the value is the EnumCodec 1.21.11 code reads
 * (fieldGetters; climbed layers' statics, e.g. the 1.21.5 layer's CbEntityVariants reading Fox.Variant.CODEC).
 */
public final class CbWallEnumCodecs {
    private CbWallEnumCodecs() {
    }

    /** net.minecraft.world.entity.animal.fox.Fox.Variant.CODEC */
    @SuppressWarnings("unchecked")
    public static StringRepresentable.EnumCodec<net.minecraft.world.entity.animal.fox.Fox.Variant> foxVariant() {
        return (StringRepresentable.EnumCodec<net.minecraft.world.entity.animal.fox.Fox.Variant>) net.minecraft.world.entity.animal.fox.Fox.Variant.CODEC;
    }

    /** net.minecraft.world.entity.animal.fish.Salmon.Variant.CODEC */
    @SuppressWarnings("unchecked")
    public static StringRepresentable.EnumCodec<net.minecraft.world.entity.animal.fish.Salmon.Variant> salmonVariant() {
        return (StringRepresentable.EnumCodec<net.minecraft.world.entity.animal.fish.Salmon.Variant>) net.minecraft.world.entity.animal.fish.Salmon.Variant.CODEC;
    }

    /** net.minecraft.server.dialog.DialogAction.CODEC */
    @SuppressWarnings("unchecked")
    public static StringRepresentable.EnumCodec<net.minecraft.server.dialog.DialogAction> dialogAction() {
        return (StringRepresentable.EnumCodec<net.minecraft.server.dialog.DialogAction>) net.minecraft.server.dialog.DialogAction.CODEC;
    }
}
