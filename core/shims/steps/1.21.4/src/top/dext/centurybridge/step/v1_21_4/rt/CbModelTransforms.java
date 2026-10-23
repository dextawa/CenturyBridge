package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 ItemTransforms (class_809) members on 1.21.4, where it is a record: its eight public final fields are the
 * record's components (same order as both constructors), hasTransform and the copy constructor are gone.
 */
@Environment(EnvType.CLIENT)
public final class CbModelTransforms {
    private CbModelTransforms() {
    }

    /** thirdPersonLeftHand */
    public static net.minecraft.class_804 field_4305(net.minecraft.class_809 t) {
        return t.comp_3368();
    }

    /** thirdPersonRightHand */
    public static net.minecraft.class_804 field_4307(net.minecraft.class_809 t) {
        return t.comp_3369();
    }

    /** firstPersonLeftHand */
    public static net.minecraft.class_804 field_4302(net.minecraft.class_809 t) {
        return t.comp_3370();
    }

    /** firstPersonRightHand */
    public static net.minecraft.class_804 field_4304(net.minecraft.class_809 t) {
        return t.comp_3371();
    }

    /** head */
    public static net.minecraft.class_804 field_4311(net.minecraft.class_809 t) {
        return t.comp_3372();
    }

    /** gui */
    public static net.minecraft.class_804 field_4300(net.minecraft.class_809 t) {
        return t.comp_3373();
    }

    /** ground */
    public static net.minecraft.class_804 field_4303(net.minecraft.class_809 t) {
        return t.comp_3374();
    }

    /** fixed */
    public static net.minecraft.class_804 field_4306(net.minecraft.class_809 t) {
        return t.comp_3375();
    }

    /** a 1.21.3 ItemTransforms subclass overriding hasTransform (renamed onto this, overrideRenames) */
    public interface CbLegacyHasTransform {
        boolean cb$hasTransform(net.minecraft.class_811 context);
    }

    /** was hasTransform(context) (method_3501): a subclass's own answer, else the context's transform is not ItemTransform.NO_TRANSFORM */
    public static boolean method_3501(net.minecraft.class_809 t, net.minecraft.class_811 context) {
        if ((Object) t instanceof CbLegacyHasTransform own) { // final at compile time, extendable at run time (access widener)
            return own.cb$hasTransform(context);
        }
        return superHasTransform(t, context);
    }

    /** a subclass's super.hasTransform(context): 1.21.3's body */
    public static boolean superHasTransform(net.minecraft.class_809 t, net.minecraft.class_811 context) {
        return t.method_3503(context) != net.minecraft.class_804.field_4284;
    }

    /** a 1.21.3 subclass's super(other): the copy constructor is the record's canonical one with the other's transforms */
    public static net.minecraft.class_804 cbThirdLeft(net.minecraft.class_809 o) {
        return o.comp_3368();
    }

    public static net.minecraft.class_804 cbThirdRight(net.minecraft.class_809 o) {
        return o.comp_3369();
    }

    public static net.minecraft.class_804 cbFirstLeft(net.minecraft.class_809 o) {
        return o.comp_3370();
    }

    public static net.minecraft.class_804 cbFirstRight(net.minecraft.class_809 o) {
        return o.comp_3371();
    }

    public static net.minecraft.class_804 cbHead(net.minecraft.class_809 o) {
        return o.comp_3372();
    }

    public static net.minecraft.class_804 cbGui(net.minecraft.class_809 o) {
        return o.comp_3373();
    }

    public static net.minecraft.class_804 cbGround(net.minecraft.class_809 o) {
        return o.comp_3374();
    }

    public static net.minecraft.class_804 cbFixed(net.minecraft.class_809 o) {
        return o.comp_3375();
    }

    /** was new ItemTransforms(other): a copy of the eight transforms (the same ItemTransform objects) */
    public static net.minecraft.class_809 copy(net.minecraft.class_809 other) {
        return new net.minecraft.class_809(other.comp_3368(), other.comp_3369(), other.comp_3370(), other.comp_3371(),
            other.comp_3372(), other.comp_3373(), other.comp_3374(), other.comp_3375());
    }
}
