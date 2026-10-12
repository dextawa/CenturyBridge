package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * What a converted subclass of 1.20.4's DustParticleOptionsBase extends (superRenames). 1.20.4's base held a
 * colour and a scale and wrote both; 1.20.5 split it: ScalableParticleOptionsBase holds the scale, and its
 * subclasses (dust, colour transition) their own colours -- every other reference to the old base names
 * 1.20.5's class (classRenames), so vanilla's dust options stay what a 1.20.4 mod tests for. This class is
 * that base again, on ScalableParticleOptionsBase: 1.20.4's fields, constructor (the scale clamped as 1.20.4
 * did), colour, scale, and the network and text forms 1.20.4 wrote. 1.20.5 asks a scale by its own name;
 * that answers 1.20.4's getScale(), so a subclass overriding it is asked.
 */
public abstract class DustParticleOptionsBase extends net.minecraft.class_9679
        implements top.dext.centurybridge.step.v1_20_5.rt.CbParticles.WriteNetwork,
                   top.dext.centurybridge.step.v1_20_5.rt.CbParticles.WriteString {

    /** MIN_SCALE */
    public static final float field_33114 = 0.01F;

    /** MAX_SCALE */
    public static final float field_33115 = 4.0F;

    /** color */
    public final org.joml.Vector3f field_28273;

    /** scale */
    public final float field_28274;

    public DustParticleOptionsBase(org.joml.Vector3f color, float scale) {
        super(scale);
        this.field_28273 = color;
        this.field_28274 = net.minecraft.class_3532.method_15363(scale, 0.01F, 4.0F);
    }

    /** readVector3f(reader) */
    public static org.joml.Vector3f method_33118(com.mojang.brigadier.StringReader reader)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return top.dext.centurybridge.step.v1_20_5.rt.CbParticles.readVector3f(reader);
    }

    /** readVector3f(buf) */
    public static org.joml.Vector3f method_33466(net.minecraft.class_2540 buf) {
        return top.dext.centurybridge.step.v1_20_5.rt.CbParticles.readVector3f(buf);
    }

    /** writeToNetwork(buf): the colour, then the scale */
    @Override
    public void method_10294(net.minecraft.class_2540 buf) {
        buf.writeFloat(field_28273.x());
        buf.writeFloat(field_28273.y());
        buf.writeFloat(field_28273.z());
        buf.writeFloat(field_28274);
    }

    /** writeToString(): the type id, the colour and the scale */
    @Override
    public String method_10293() {
        return String.format(java.util.Locale.ROOT, "%s %.2f %.2f %.2f %.2f",
            net.minecraft.class_7923.field_41180.method_10221(this.method_10295()),
            field_28273.x(), field_28273.y(), field_28273.z(), field_28274);
    }

    /** getColor() */
    public org.joml.Vector3f method_33119() {
        return field_28273;
    }

    /** getScale() */
    public float method_33120() {
        return field_28274;
    }

    /** 1.20.5's getScale(): 1.20.4's, which a subclass may override */
    @Override
    public float method_59846() {
        return method_33120();
    }
}
