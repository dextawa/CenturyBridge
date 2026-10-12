package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 riding offsets every entity answers again (s1misc riding bridge): a mixin on Entity
 * implements this interface with 1.20.1's per-class formulas ({@link CbRiding}), a converted
 * subclass's own override takes its place by ordinary virtual dispatch.
 *
 * 1.20.2 replaced both with the passenger attachment point (Entity.getPassengerAttachmentPoint,
 * method_52533) and Entity.getMyRidingOffset(Entity vehicle) (method_52536); see
 * {@link CbRiding} for how a 1.20.1 override is reconnected to them.
 */
public interface CbLegacyRiding {

    /** 1.20.1 Entity.getPassengersRidingOffset(): how far above the vehicle's feet its passengers sit */
    double method_5621();

    /** 1.20.1 Entity.getMyRidingOffset(): this entity's own vertical offset when it rides */
    double method_5678();

    /**
     * The vehicle's own 1.20.2 attachment point (Entity.getPassengerAttachmentPoint), called
     * virtually: on a converted vehicle whose override hook is running this falls through to the
     * vanilla implementation above it (the hook's guard).
     */
    org.joml.Vector3f cb$passengerAttachment(net.minecraft.class_1297 passenger, net.minecraft.class_4048 dimensions, float scale);
}
