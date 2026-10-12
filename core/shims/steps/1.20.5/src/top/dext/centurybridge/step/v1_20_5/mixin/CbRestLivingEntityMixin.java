package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestAttributes;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * 1.20.4 LivingEntity's eye height: getEyeHeight(pose, dimensions) was final there -- 0.2 sleeping,
 * otherwise getStandingEyeHeight(pose, dimensions), which mods override -- and getStandingEyeHeight
 * answered Entity's (the dimensions' eye height in 1.20.5, see mixin/CbRestEntityMixin). 1.20.5 keeps
 * sleeping at 0.2 through its sleeping dimensions.
 * <p>
 * 1.20.4 LivingEntity.getMobType() (see rt/CbRestMobType): the cover answers from the entity type's
 * tags; where 1.20.5 decides by the tags that replaced it -- undead healing and harm inverted, undead
 * ignoring poison and regeneration -- an entity whose class declares a MobType decides by it, as in
 * 1.20.4.
 * <p>
 * 1.20.4 getAttributeValue / getAttributeBaseValue(Attribute), to which the Holder members delegated, and
 * broadcastBreakEvent(hand) (the hand's slot): each cover calls 1.20.5's member with the step's Guard
 * held, so a mod class's synthesized 1.20.5 shape (its 1.20.4 override, reconnected by this step's hooks)
 * falls through to its superclass instead of forwarding back.
 */
@Mixin(net.minecraft.class_1309.class)
public abstract class CbRestLivingEntityMixin {

    @Shadow
    public abstract double method_45325(net.minecraft.class_6880<net.minecraft.class_1320> attribute);

    @Shadow
    public abstract double method_45326(net.minecraft.class_6880<net.minecraft.class_1320> attribute);

    @Shadow
    public abstract void method_20235(net.minecraft.class_1304 slot);

    /** 1.20.4 getAttributeValue(Attribute) */
    public double method_26825(net.minecraft.class_1320 attribute) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45325(CbRestAttributes.holder(attribute));
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 getAttributeBaseValue(Attribute) */
    public double method_26826(net.minecraft.class_1320 attribute) {
        boolean entered = Guard.cbEnter(this);
        try {
            return this.method_45326(CbRestAttributes.holder(attribute));
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 broadcastBreakEvent(InteractionHand): the main hand's slot or the off hand's */
    public void method_20236(net.minecraft.class_1268 hand) {
        boolean entered = Guard.cbEnter(this);
        try {
            this.method_20235(hand == net.minecraft.class_1268.field_5808 ? net.minecraft.class_1304.field_6173 : net.minecraft.class_1304.field_6171);
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 LivingEntity.getEyeHeight(Pose, EntityDimensions) (final) */
    protected final float method_18378(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return pose == net.minecraft.class_4050.field_18078 ? 0.2F : this.method_18394(pose, dimensions);
    }

    /** 1.20.4 LivingEntity.getStandingEyeHeight(Pose, EntityDimensions): the dimensions' eye height */
    protected float method_18394(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }

    /** 1.20.4 LivingEntity.getMobType(): what the entity type's tags say */
    public class_1310 method_6046() {
        return CbRestMobType.of(((net.minecraft.class_1297) (Object) this).method_5864());
    }

    @Redirect(method = {"method_6049", "method_5999"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1299;method_20210(Lnet/minecraft/class_6862;)Z"))
    private boolean cbRest$undeadTag(net.minecraft.class_1299<?> type, net.minecraft.class_6862<net.minecraft.class_1299<?>> tag) {
        if (tag == net.minecraft.class_3483.field_48290 || tag == net.minecraft.class_3483.field_48291) {
            class_1310 declared = CbRestMobType.declared((net.minecraft.class_1297) (Object) this);
            if (declared != null) {
                return declared == class_1310.field_6289;
            }
        }
        return type.method_20210(tag);
    }
}
