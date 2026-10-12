package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's riding geometry on 1.20.5's.
 * <p>
 * 1.20.4 placed a passenger at {@code vehicle.getPassengerAttachmentPoint(passenger, dimensions, 1)}
 * -- a point in the vehicle's own frame, which the caller turned by the vehicle's yaw
 * ({@code rotateY(-yRot)}) -- plus {@code passenger.getMyRidingOffset(vehicle)} upwards (by default the
 * passenger's {@code ridingOffset(vehicle)}). 1.20.5 places it at
 * {@code vehicle.getPassengerAttachmentPoint(passenger, dimensions, 1)} -- already turned by the yaw --
 * minus {@code passenger.getVehicleAttachmentPoint(vehicle)}. The override hooks of this step
 * synthesize the 1.20.5 shapes in a mod class that overrides the 1.20.4 ones and come here
 * (bridge mode, the step's Guard held), so 1.20.5 places the passenger where the mod's 1.20.4 code
 * says; the mod's own super calls reach the covers of mixin/CbRestEntityMixin, which answer the
 * vanilla 1.20.5 geometry in 1.20.4's terms.
 */
public final class CbRestEntity {

    /**
     * Added to a mod class whose 1.20.4 getPassengerAttachmentPoint override the hook reconnects. It
     * names the old shape the mod's method implements; the mod declares that method protected, as
     * 1.20.4's was, so the bridge never calls it through this interface but through the public
     * CbRestEntityApi method Entity carries (a protected call made from Entity itself).
     */
    public interface CbLegacyPassengerAttachment {
        org.joml.Vector3f method_52533(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale);
    }

    /**
     * Added to a mod class whose 1.20.4 ridingOffset / getMyRidingOffset override the hook reconnects;
     * reached through CbRestEntityApi for the same reason as {@link CbLegacyPassengerAttachment}.
     */
    public interface CbLegacyRidingOffset {
        float method_52537(net.minecraft.class_1297 vehicle);

        float method_52536(net.minecraft.class_1297 vehicle);
    }

    /** the 1.20.4 yaw rotation of a vehicle-frame point: {@code rotateY(-yRot * pi / 180)} */
    public static net.minecraft.class_243 cbTurn(org.joml.Vector3f point, float yRot) {
        org.joml.Vector3f v = new org.joml.Vector3f(point).rotateY(-yRot * ((float) Math.PI / 180F));
        return new net.minecraft.class_243(v.x, v.y, v.z);
    }

    /** the inverse of {@link #cbTurn}: a 1.20.5 (turned) point back in the vehicle's own frame */
    public static org.joml.Vector3f cbUnturn(net.minecraft.class_243 point, float yRot) {
        net.minecraft.class_243 v = point.method_1024(yRot * ((float) Math.PI / 180F));
        return new org.joml.Vector3f((float) v.field_1352, (float) v.field_1351, (float) v.field_1350);
    }

    /**
     * 1.20.5 getPassengerAttachmentPoint(passenger, dimensions, scale) of a mod class overriding the
     * 1.20.4 one: the mod's point, turned by the vehicle's yaw as 1.20.4's caller turned it.
     */
    public static net.minecraft.class_243 cbPassengerAttachment(CbLegacyPassengerAttachment self, net.minecraft.class_1297 passenger,
                                                              net.minecraft.class_4048 dimensions, float scale) {
        net.minecraft.class_1297 vehicle = (net.minecraft.class_1297) self;
        org.joml.Vector3f point = ((CbRestEntityApi) vehicle).cbRest$passengerAttachmentPoint(passenger, dimensions, scale);
        return cbTurn(point, vehicle.method_36454());
    }

    /**
     * 1.20.5 getVehicleAttachmentPoint(vehicle) of a mod class overriding the 1.20.4 riding offset:
     * 1.20.4 raised the passenger by getMyRidingOffset(vehicle), 1.20.5 lowers it by this point, so its
     * height is minus that offset; across, it keeps what the vanilla class answers (1.20.4 had no
     * sideways riding offset). The vanilla answer is reached by calling the 1.20.5 shape again: the
     * synthesized method holds the guard, so the call falls through to the superclass.
     */
    public static net.minecraft.class_243 cbVehicleAttachment(CbLegacyRidingOffset self, net.minecraft.class_1297 vehicle) {
        net.minecraft.class_1297 passenger = (net.minecraft.class_1297) self;
        net.minecraft.class_243 vanilla = passenger.method_55668(vehicle);
        float offset = ((CbRestEntityApi) passenger).cbRest$myRidingOffset(vehicle);
        return new net.minecraft.class_243(vanilla.field_1352, -offset, vanilla.field_1350);
    }

    /** 1.20.4 EntityDimensions.width (a public field; 1.20.5's record component) */
    public static float field_18067(net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2185();
    }

    /** 1.20.4 EntityDimensions.height */
    public static float field_18068(net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2186();
    }

    /** 1.20.4 EntityDimensions.fixed */
    public static boolean field_18069(net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2189();
    }

    private CbRestEntity() {
    }
}
