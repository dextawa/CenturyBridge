package top.dext.centurybridge.rt.v1_20_2;

/**
 * Marks a converted entity class that overrides 1.20.1's getMyRidingOffset (method_5678): the
 * override hook adds this interface and synthesizes 1.20.2's getMyRidingOffset(Entity vehicle),
 * which calls {@link CbRiding#passengerOffset}.
 */
public interface CbLegacyRidingPassenger extends CbLegacyRiding {

    @Override
    double method_5678();
}
