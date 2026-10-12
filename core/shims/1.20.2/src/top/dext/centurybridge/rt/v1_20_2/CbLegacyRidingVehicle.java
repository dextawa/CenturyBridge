package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted entity class that overrides 1.20.1's getPassengersRidingOffset (method_5621):
 * the override hook adds this interface and synthesizes 1.20.2's getPassengerAttachmentPoint,
 * which calls {@link CbRiding#vehicleAttachment}.
 */
public interface CbLegacyRidingVehicle extends CbLegacyRiding {

    @Override
    double method_5621();
}
