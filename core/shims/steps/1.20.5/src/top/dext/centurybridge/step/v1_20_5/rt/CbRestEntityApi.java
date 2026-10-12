package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * The 1.20.4 entity hooks an Entity carries again in 1.20.5 (added by mixin/CbRestEntityMixin), called
 * virtually so a 1.20.4 mod's own override answers: the bridges of this layer reach a mod's protected
 * 1.20.4 methods through these public ones.
 */
public interface CbRestEntityApi {

    /** 1.20.4 getPassengerAttachmentPoint(passenger, dimensions, scale), unrotated */
    org.joml.Vector3f cbRest$passengerAttachmentPoint(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions,
                                                      float scale);

    /** 1.20.4 getMyRidingOffset(vehicle) */
    float cbRest$myRidingOffset(net.minecraft.class_1297 vehicle);

    /** 1.20.4 getNameTagOffsetY() */
    float cbRest$nameTagOffsetY();
}
