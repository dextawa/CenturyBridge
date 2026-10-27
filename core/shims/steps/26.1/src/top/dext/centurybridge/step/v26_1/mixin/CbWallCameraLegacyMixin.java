package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11 Camera members 26.1 reshaped.
 * <ul>
 * <li>getNearPlane(): 1.21.11 spanned the near plane with the fov option (degrees, unmodified by effects); 26.1's
 *     getNearPlane(fov) takes the fov.</li>
 * <li>getPartialTickTime(): 1.21.11 kept the partial tick setup() was given -- the game renderer's, 1 when the
 *     camera entity is frozen by the tick rate manager. 26.1's update(deltaTracker) computes the same value through
 *     getCameraEntityPartialTicks; for the main camera (the one 26.1 updates) this asks it of the game's delta
 *     tracker; any other camera answers what its last setup(..) stored, as 1.21.11's field held.</li>
 * <li>setup(level, entity, detached, mirrored, partialTick): restored with 1.21.11's body (javap class_4184.method_19321):
 *     it sets the level, entity and detached flag and places and turns the camera on the entity. 26.1 splits it into
 *     setLevel / setEntity and update(deltaTracker), whose alignWithEntity(partialTick) is the same placement body
 *     call for call (minecart lerp, eye-height lerp, third-person zoom by scale and CAMERA_DISTANCE, sleeping turn),
 *     except that it reads detached and mirrored from the camera-type option where 1.21.11 took them as arguments;
 *     update also computes fov, frustum and projection, which 1.21.11's game renderer did elsewhere. A 1.21.11
 *     subclass overrides this member and its super call runs this body; 26.1 vanilla never calls it.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Camera.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallCameraLegacyMixin {
    @Shadow
    private boolean initialized;
    @Shadow
    private Level level;
    @Shadow
    private Entity entity;
    @Shadow
    private boolean detached;
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    protected abstract void move(float zoom, float dy, float dx);

    @Shadow
    private float getMaxZoom(float maxZoom) {
        throw new AssertionError();
    }

    public Camera.NearPlane getNearPlane() {
        return ((Camera) (Object) this).getNearPlane(Minecraft.getInstance().options.fov().get().floatValue());
    }

    public float getPartialTickTime() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameRenderer == null || minecraft.gameRenderer.getMainCamera() != (Object) this) {
            // a camera 26.1 does not update (a 1.21.11 mod's own): what its last setup(..) was given, as 1.21.11 kept
            return this.cb$setupPartialTick;
        }
        return ((Camera) (Object) this).getCameraEntityPartialTicks(minecraft.getDeltaTracker());
    }

    /** the partial tick 1.21.11's setup stored (class_4184.field_47549, set @23), for a camera 26.1 does not update */
    @Unique
    private float cb$setupPartialTick;

    /** 1.21.11's setup(level, entity, detached, mirrored, partialTick) */
    public void setup(Level level, Entity entity, boolean detached, boolean mirrored, float partialTick) {
        this.initialized = true;
        this.level = level;
        this.entity = entity;
        this.detached = detached;
        this.cb$setupPartialTick = partialTick;
        if (entity.isPassenger() && entity.getVehicle() instanceof Minecart minecart
                && minecart.getBehavior() instanceof NewMinecartBehavior behavior && behavior.cartHasPosRotLerp()) {
            Vec3 offset = minecart.getPassengerRidingPosition(entity).subtract(minecart.position())
                .subtract(entity.getVehicleAttachmentPoint(minecart))
                .add(new Vec3(0.0, Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight), 0.0));
            this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
            this.setPosition(behavior.getCartLerpPosition(partialTick).add(offset));
        } else {
            this.setRotation(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
            this.setPosition(Mth.lerp(partialTick, entity.xo, entity.getX()),
                Mth.lerp(partialTick, entity.yo, entity.getY()) + Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight),
                Mth.lerp(partialTick, entity.zo, entity.getZ()));
        }
        if (detached) {
            if (mirrored) {
                this.setRotation(this.yRot + 180.0F, -this.xRot);
            }
            float distance = 4.0F;
            float scale = 1.0F;
            if (entity instanceof LivingEntity living) {
                scale = living.getScale();
                distance = (float) living.getAttributeValue(Attributes.CAMERA_DISTANCE);
            }
            float vehicleScale = scale;
            float vehicleDistance = distance;
            if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity vehicle) {
                vehicleScale = vehicle.getScale();
                vehicleDistance = (float) vehicle.getAttributeValue(Attributes.CAMERA_DISTANCE);
            }
            this.move(-this.getMaxZoom(Math.max(scale * distance, vehicleScale * vehicleDistance)), 0.0F, 0.0F);
        } else if (entity instanceof LivingEntity living && living.isSleeping()) {
            Direction bed = living.getBedOrientation();
            this.setRotation(bed != null ? bed.toYRot() - 180.0F : 0.0F, 0.0F);
            this.move(0.0F, 0.3F, 0.0F);
        }
    }
}
