package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4 BlockElement (class_785) on 1.21.5, where it is a record (javap both). 1.21.4: a class with public final fields
 * from / to (Vector3f), faces, rotation, shade, lightEmission, and two constructors that stored their arguments and then
 * filled each face's missing UVs from the element's box (method_3402). 1.21.5: record components comp_3727..comp_3732
 * (from / to as Vector3fc), constructors that store their arguments as they are; a face's missing UVs are computed at
 * bake time instead (FaceBakery.bakeQuad, class_796.method_3468: the same box-derived UVs when the face has none).
 * The record and its accessors stay through 26.2 (no later damage rows; CuboidModelElement past the wall).
 */
@Environment(EnvType.CLIENT)
public final class CbBlockElements {

    private CbBlockElements() {
    }

    /** new BlockElement(from, to, faces): 1.21.5's constructor takes Vector3fc; the same vectors, stored as 1.21.4 did */
    public static net.minecraft.class_785 newBlockElement(org.joml.Vector3f from, org.joml.Vector3f to,
            java.util.Map<net.minecraft.class_2350, net.minecraft.class_783> faces) {
        return new net.minecraft.class_785(from, to, faces);
    }

    /** new BlockElement(from, to, faces, rotation, shade, lightEmission) */
    public static net.minecraft.class_785 newBlockElement(org.joml.Vector3f from, org.joml.Vector3f to,
            java.util.Map<net.minecraft.class_2350, net.minecraft.class_783> faces, net.minecraft.class_789 rotation,
            boolean shade, int lightEmission) {
        return new net.minecraft.class_785(from, to, faces, rotation, shade, lightEmission);
    }

    /**
     * from: 1.21.4 answered its Vector3f field. 1.21.5 holds a Vector3fc; the answer is a copy, so writing into it does
     * not change the record (1.21.4 writes through the field did: release_notes_known.md).
     */
    public static org.joml.Vector3f field_4228(net.minecraft.class_785 element) {
        return new org.joml.Vector3f(element.comp_3727());
    }

    /** to: as from */
    public static org.joml.Vector3f field_4231(net.minecraft.class_785 element) {
        return new org.joml.Vector3f(element.comp_3728());
    }

    /** faces */
    public static java.util.Map<net.minecraft.class_2350, net.minecraft.class_783> field_4230(net.minecraft.class_785 element) {
        return element.comp_3729();
    }

    /** rotation (nullable in both) */
    public static net.minecraft.class_789 field_4232(net.minecraft.class_785 element) {
        return element.comp_3730();
    }

    /** shade */
    public static boolean field_4229(net.minecraft.class_785 element) {
        return element.comp_3731();
    }

    /** lightEmission */
    public static int field_53159(net.minecraft.class_785 element) {
        return element.comp_3732();
    }
}
