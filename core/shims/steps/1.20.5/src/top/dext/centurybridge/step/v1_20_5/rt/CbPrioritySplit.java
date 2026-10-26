package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.20.5's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the state and restored members both
 * use, reached through the restorer (its {@code @Unique} state is the restorer's own; its restored members are called
 * virtually, so a subclass's override still answers). Client-side ones: {@link CbPrioritySplitClient}.
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbEntityFireballRawMixin / CbEntityThrowableItemRawMixin: 1.20.4's getItemRaw, called virtually */
    public interface ItemRaw {
        net.minecraft.class_1799 cb$v1_20_5$itemRaw();
    }

    /** CbEntityPiglinSoundMixin: 1.20.4's makeSound(sound), called virtually */
    public interface PiglinSound {
        void cb$v1_20_5$makeSound(net.minecraft.class_3414 sound);
    }

    /** CbRestSwimNodeEvaluatorMixin: 1.20.4's isDiagonalNodeValid, called virtually */
    public interface SwimDiagonal {
        boolean cb$v1_20_5$isDiagonalNodeValid(net.minecraft.class_9 diagonal, net.minecraft.class_9 a, net.minecraft.class_9 b);
    }

    /** CbEntityHorseMixin: 1.20.4's getCustomJump, called virtually */
    public interface HorseJump {
        double cb$v1_20_5$customJump();
    }

    /** CbFinDisplayInfoMixin: 1.20.4's serializeToNetwork(FriendlyByteBuf), called virtually */
    public interface DisplayWrite {
        void cb$v1_20_5$serializeToNetwork(net.minecraft.class_2540 buf);
    }

    /** CbNetExplodePacketMixin: 1.20.4's writeParticle, called virtually */
    public interface ExplodeParticles {
        void cb$v1_20_5$writeParticle(net.minecraft.class_2540 buf, net.minecraft.class_2394 particle);
    }

    /** CbRestAbstractHorseMixin: the rider's attachment height, and 1.20.4's getPassengersRidingOffsetY called virtually */
    public interface HorseRiding {
        float cb$v1_20_5$attachmentHeight(net.minecraft.class_4048 dimensions);

        float cb$v1_20_5$passengersRidingOffsetY(net.minecraft.class_4048 dimensions, float scale);
    }

    /** CbRestMinecraftServerMixin: 1.20.4's logTickTime, called virtually */
    public interface ServerTickTime {
        void cb$v1_20_5$logTickTime(long nanos);
    }

    /** CbRestTamableAnimalMixin: 1.20.4's reassessTameGoals, called virtually */
    public interface TameGoals {
        void cb$v1_20_5$reassessTameGoals();
    }

    /** CbRestEntityMixin: 1.20.4's getEyeHeight(pose, dimensions) called virtually, and setMaxUpStep's kept height */
    public interface EntityEyes {
        float cb$v1_20_5$eyeHeight(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions);

        float cb$v1_20_5$maxUpStep();
    }
}
