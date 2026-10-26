package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestEntity;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestEntityApi;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * The 1.20.4 Entity members 1.20.5 dropped or moved, put back on Entity (covers) with 1.20.4's
 * contracts, and the places 1.20.5 decides what they used to decide made to ask them again.
 * <p>
 * Eye height: 1.20.4 took an entity's eye height from {@code getEyeHeight(pose, dimensions)}, which
 * mods override (LivingEntity: final, via getStandingEyeHeight -- mixin/CbRestLivingEntityMixin);
 * 1.20.5 reads {@code dimensions.eyeHeight()} where 1.20.4 called it (the constructor with STANDING,
 * fixupDimensions, refreshDimensions, getEyeHeight(pose)). The cover answers that same value, so a
 * vanilla entity's eye height is 1.20.5's, and those reads ask the cover -- that is, a mod's override.
 * <p>
 * Riding: see rt/CbRestEntity. The covers answer the vanilla 1.20.5 geometry in 1.20.4's terms: the
 * attachment point of this entity's class unturned, the vehicle attachment's height as a riding offset.
 * They reach the vanilla 1.20.5 shape by calling it with the step's Guard held, so a mod class's
 * synthesized 1.20.5 method falls through to its superclass instead of forwarding back.
 * <p>
 * The injectors into 1.20.5's own methods (the eye height reads, maxUpStep) are in CbRestEntityMixinHooks (default
 * priority).
 */
@Mixin(value = net.minecraft.class_1297.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestEntityMixin implements CbRestEntityApi, CbPrioritySplit.EntityEyes {

    @Shadow
    public abstract net.minecraft.class_4050 method_18376();

    @Shadow
    protected abstract void method_5693(net.minecraft.class_2945.class_9222 builder);

    @Override
    public void cbRest$defineSynchedData(net.minecraft.class_2945.class_9222 builder) {
        this.method_5693(builder);
    }

    @Shadow
    public abstract float method_36454();

    @Shadow
    public abstract net.minecraft.class_9066 method_56072();

    @Shadow
    public abstract float method_17682();

    @Shadow
    public abstract net.minecraft.class_243 method_55668(net.minecraft.class_1297 vehicle);

    /** setMaxUpStep's height for an entity that is not a LivingEntity (1.20.5 gives those 0) */
    @Unique
    private float cbRest$maxUpStep;

    // ---------------------------------------------------------------- eye height

    /** 1.20.4 Entity.getEyeHeight(Pose, EntityDimensions): the dimensions' eye height, what 1.20.5 uses */
    protected float method_18378(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }

    /** 1.20.4's getEyeHeight called virtually, for 1.20.5's eye height reads (CbRestEntityMixinHooks) */
    @Override
    public float cb$v1_20_5$eyeHeight(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return this.method_18378(pose, dimensions);
    }

    @Override
    public float cb$v1_20_5$maxUpStep() {
        return this.cbRest$maxUpStep;
    }

    // ---------------------------------------------------------------- riding

    /**
     * 1.20.4 Entity.getPassengerAttachmentPoint(passenger, dimensions, scale): the vanilla 1.20.5
     * point of this entity's class, in the vehicle's own frame (1.20.4's caller turned it by the yaw)
     */
    protected org.joml.Vector3f method_52533(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale) {
        boolean entered = Guard.cbEnter(this);
        try {
            net.minecraft.class_243 point = ((CbRestEntityInvoker) this).cbRest$attachmentPoint(passenger, dimensions, scale);
            return CbRestEntity.cbUnturn(point, this.method_36454());
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /** 1.20.4 Entity.getMyRidingOffset(vehicle): ridingOffset(vehicle), as 1.20.4 answered */
    public float method_52536(net.minecraft.class_1297 vehicle) {
        return this.method_52537(vehicle);
    }

    /**
     * 1.20.4 Entity.ridingOffset(vehicle): how far 1.20.4 raised this entity riding the vehicle -- minus
     * the height of this entity's vanilla 1.20.5 vehicle attachment point, by which 1.20.5 lowers it
     */
    protected float method_52537(net.minecraft.class_1297 vehicle) {
        boolean entered = Guard.cbEnter(this);
        try {
            return (float) -this.method_55668(vehicle).field_1351;
        } finally {
            if (entered) {
                Guard.cbExit(this);
            }
        }
    }

    /**
     * 1.20.4 Entity.getNameTagOffsetY(): how high 1.20.4's renderer put the name tag -- in 1.20.5 the
     * name tag attachment's height plus the 0.5 the renderer adds; 1.20.4's getBbHeight() + 0.5 where the
     * entity has no name tag attachment
     */
    public float method_51152() {
        net.minecraft.class_243 tag = this.method_56072().method_55675(net.minecraft.class_9064.field_47745, 0, this.method_36454());
        return tag == null ? this.method_17682() + 0.5F : (float) tag.field_1351 + 0.5F;
    }

    @Override
    public org.joml.Vector3f cbRest$passengerAttachmentPoint(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale) {
        return this.method_52533(passenger, dimensions, scale);
    }

    @Override
    public float cbRest$myRidingOffset(net.minecraft.class_1297 vehicle) {
        return this.method_52536(vehicle);
    }

    @Override
    public float cbRest$nameTagOffsetY() {
        return this.method_51152();
    }

    // ---------------------------------------------------------------- equipment (LivingEntity's since 1.20.5)

    /** 1.20.4 Entity.getHandSlots(): no slots (a LivingEntity answers its own) */
    public Iterable<net.minecraft.class_1799> method_5877() {
        return java.util.Collections.emptyList();
    }

    /** 1.20.4 Entity.getArmorSlots(): no slots (a LivingEntity answers its own) */
    public Iterable<net.minecraft.class_1799> method_5661() {
        return java.util.Collections.emptyList();
    }

    /** 1.20.4 Entity.getAllSlots(): the hand slots, then the armor slots -- 1.20.4's two kinds of slot */
    public Iterable<net.minecraft.class_1799> method_5743() {
        return com.google.common.collect.Iterables.concat(this.method_5877(), this.method_5661());
    }

    /** 1.20.4 Entity.setItemSlot(slot, stack): an entity without equipment keeps nothing */
    public void method_5673(net.minecraft.class_1304 slot, net.minecraft.class_1799 stack) {
    }

    // ---------------------------------------------------------------- movement

    /**
     * 1.20.4 Entity.setMaxUpStep(height): a LivingEntity's step height is its STEP_HEIGHT attribute in
     * 1.20.5 (base 0.6, 1.20.4's LivingEntity default), so the call sets the base value; any other
     * entity keeps the height here, and maxUpStep() answers it as 1.20.4's field did
     */
    public void method_49477(float height) {
        if ((Object) this instanceof net.minecraft.class_1309 living) {
            net.minecraft.class_1324 step = living.method_5996(net.minecraft.class_5134.field_47761);
            if (step != null) {
                step.method_6192(height);
            }
        } else {
            this.cbRest$maxUpStep = height;
        }
    }
}
