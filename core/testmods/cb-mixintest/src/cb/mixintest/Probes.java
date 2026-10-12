package cb.mixintest;

/** what the test mixins add to their targets, for the driver to call (implemented by the mixins) */
public final class Probes {
    private Probes() {
    }

    /** ScoreboardMixin: a call through @Shadow getOrCreateObjective, a read of @Shadow DISPLAY_SLOT_LIST */
    public interface ScoreboardProbe {
        net.minecraft.class_266 cbtest$probeOrCreate(String name);

        int cbtest$listSlot();
    }

    /** EntityMixin: calls through @Shadow getTeam and @Shadow ignoreExplosion */
    public interface EntityProbe {
        Object cbtest$team();

        boolean cbtest$ignoresExplosion();
    }

    /** LivingEntityMixin: reads and writes of @Shadow lyHeadRot */
    public interface LivingProbe {
        double cbtest$lyHeadRot();

        void cbtest$setLyHeadRot(double v);
    }

    /** BoatMixin: calls through @Shadow setHurtTime / getHurtTime */
    public interface BoatProbe {
        void cbtest$setHurt(int t);

        int cbtest$hurt();
    }
}
