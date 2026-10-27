package top.dext.centurybridge.step.v1_21_9.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.9's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state both use, reached
 * through the restorer (its {@code @Unique} state is the restorer's own).
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbServerRestoredMixin: the flight flag a 1.21.8 setFlightAllowed set on a non-dedicated server (null: never set) */
    public interface ServerFlight {
        Boolean cb$v1_21_9$flightAllowed();
    }
}
