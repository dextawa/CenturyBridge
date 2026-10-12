package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's MobEffectInstance.FactorData: how far an effect with a blend factor (darkness) has faded in or
 * out, eased over its padding duration. 1.20.5 folded it into MobEffectInstance as a private BlendState
 * driven by the effect's blend duration; an instance whose effect a 1.20.4 mod gave a factor data factory
 * keeps one of these again (rt/CbEffects), ticked and answered exactly as 1.20.4 did. 1.20.4's fields,
 * constructors, tick, getFactor and codec as they were (the fields public: a facade's members must be).
 */
public class FactorData {

    /** CODEC */
    public static final com.mojang.serialization.Codec<FactorData> field_38085 =
        com.mojang.serialization.codecs.RecordCodecBuilder.create(i -> i.group(
            net.minecraft.class_5699.field_33441.fieldOf("padding_duration").forGetter(d -> d.field_38086),
            com.mojang.serialization.Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter(d -> d.field_39111),
            com.mojang.serialization.Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter(d -> d.field_38087),
            com.mojang.serialization.Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter(d -> d.field_38088),
            net.minecraft.class_5699.field_33441.fieldOf("ticks_active").orElse(0).forGetter(d -> d.field_42107),
            com.mojang.serialization.Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter(d -> d.field_38090),
            com.mojang.serialization.Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter(d -> d.field_38091)
        ).apply(i, FactorData::new));

    /** paddingDuration */
    public final int field_38086;

    /** factorStart */
    public float field_39111;

    /** factorTarget */
    public float field_38087;

    /** factorCurrent */
    public float field_38088;

    /** ticksActive */
    public int field_42107;

    /** factorPreviousFrame */
    public float field_38090;

    /** hadEffectLastTick */
    public boolean field_38091;

    public FactorData(int paddingDuration, float factorStart, float factorTarget, float factorCurrent, int ticksActive,
                      float factorPreviousFrame, boolean hadEffectLastTick) {
        this.field_38086 = paddingDuration;
        this.field_39111 = factorStart;
        this.field_38087 = factorTarget;
        this.field_38088 = factorCurrent;
        this.field_42107 = ticksActive;
        this.field_38090 = factorPreviousFrame;
        this.field_38091 = hadEffectLastTick;
    }

    public FactorData(int paddingDuration) {
        this(paddingDuration, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
    }

    /** tick(effect): ease toward 1 while the effect lasts past the padding, toward 0 once it ends within it */
    public void method_42136(net.minecraft.class_1293 effect) {
        this.field_38090 = this.field_38088;
        boolean hasEffect = !effect.method_48557(this.field_38086);
        ++this.field_42107;
        if (this.field_38091 != hasEffect) {
            this.field_38091 = hasEffect;
            this.field_42107 = 0;
            this.field_39111 = this.field_38088;
            this.field_38087 = hasEffect ? 1.0F : 0.0F;
        }
        float f = net.minecraft.class_3532.method_15363((float) this.field_42107 / (float) this.field_38086, 0.0F, 1.0F);
        this.field_38088 = net.minecraft.class_3532.method_16439(f, this.field_39111, this.field_38087);
    }

    /** getFactor(entity, partialTick) */
    public float method_42134(net.minecraft.class_1309 entity, float partialTick) {
        if (entity.method_31481()) {
            this.field_38090 = this.field_38088;
        }
        return net.minecraft.class_3532.method_16439(partialTick, this.field_38090, this.field_38088);
    }
}
