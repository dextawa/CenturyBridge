package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5 RenderType.translucent(), which 1.21.6 removed (its chunk layers became ChunkSectionLayer; RenderType keeps
 * solid, cutoutMipped, cutout and tripwire). Rebuilt as 1.21.5 built it -- "translucent", 786432 bytes, crumbling and
 * sorted on upload, the TRANSLUCENT pipeline (1.21.6's ChunkSectionLayer.TRANSLUCENT draws with the same one), the
 * lightmap, the mipmapped block atlas and the translucent target -- on 1.21.6's RenderType classes (javap of both).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbChunkTypes {

    private static net.minecraft.class_1921 translucent;

    private CbChunkTypes() {
    }

    /** translucent() */
    public static synchronized net.minecraft.class_1921 method_23583() {
        if (translucent == null) {
            translucent = net.minecraft.class_1921.method_24049("translucent", 786432, true, true, net.minecraft.class_10799.field_56895,
                net.minecraft.class_1921.class_4688.method_23598()
                    .method_23608(net.minecraft.class_4668.field_21383)
                    .method_34577(net.minecraft.class_4668.field_21376)
                    .method_23610(net.minecraft.class_4668.field_25280)
                    .method_23617(true));
        }
        return translucent;
    }
}
