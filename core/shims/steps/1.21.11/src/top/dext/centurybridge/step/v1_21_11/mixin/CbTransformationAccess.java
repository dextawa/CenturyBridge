package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_4590;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Transformation's private decomposition fields (translation, leftRotation, scale, rightRotation), read and written by
 * rt/CbLegacyTransformation for a mod's 1.21.10 raw field access. An accessor, not an access widener: past the
 * 1.21.11 -> 26.1 wall the class is com/mojang/math/Transformation, which the loader's class tweaker does not touch
 * (AwRules.tweakable), so a widened line would be silently ignored there.
 */
@Mixin(class_4590.class)
public interface CbTransformationAccess {
    @Accessor("field_20902")
    Vector3fc cb$v1_21_11$translation();

    @Accessor("field_20902")
    void cb$v1_21_11$translation(Vector3fc v);

    @Accessor("field_20903")
    Quaternionfc cb$v1_21_11$leftRotation();

    @Accessor("field_20903")
    void cb$v1_21_11$leftRotation(Quaternionfc q);

    @Accessor("field_20904")
    Vector3fc cb$v1_21_11$scale();

    @Accessor("field_20904")
    void cb$v1_21_11$scale(Vector3fc v);

    @Accessor("field_20905")
    Quaternionfc cb$v1_21_11$rightRotation();

    @Accessor("field_20905")
    void cb$v1_21_11$rightRotation(Quaternionfc q);
}
