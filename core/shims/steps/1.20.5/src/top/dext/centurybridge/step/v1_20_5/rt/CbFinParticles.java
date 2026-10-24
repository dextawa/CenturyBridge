package top.dext.centurybridge.step.v1_20_5.rt;

import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_2400;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_9381;

/**
 * 1.20.4's ParticleTypes.ENTITY_EFFECT and AMBIENT_ENTITY_EFFECT on 1.20.5. Both were SimpleParticleTypes whose
 * color came in the velocity arguments (SpellParticle.MobProvider / AmbientMobProvider: setColor(xSpeed, ySpeed,
 * zSpeed), the ambient one at alpha 0.15, the speeds passed on to the particle as well). 1.20.5 removed
 * ambient_entity_effect and made entity_effect a ParticleType of ColorParticleOption (the color, alpha included, in
 * the options; its provider still hands the speeds to the particle). The 1.20.4 fields read one marker
 * SimpleParticleType each (never registered: there is no particle type of that class to name); where a particle is
 * made of one it becomes 1.20.5's entity_effect with the color 1.20.4 would have taken from the speeds and the
 * marker's alpha, the speeds left as they are:
 * <ul>
 * <li>ParticleEngine.createParticle (every client-side particle: Level.addParticle and the particle packet
 * handler end there; mixin CbFinParticleClientMixin);</li>
 * <li>ServerLevel.sendParticles (both overloads; mixin CbFinParticleSendMixin): 1.20.4's client made one particle
 * with (speed * dx, speed * dy, speed * dz) as its speeds for count 0, and for count n n particles at x + gaussian *
 * dx (and y, z) with gaussian * speed as speeds -- so as many color-carrying particles of count 0, drawn the same way
 * here, are sent;</li>
 * <li>AreaEffectCloud.setParticle (mixin CbFinParticleCloudMixin): 1.20.4's cloud tinted an entity effect with its
 * color; 1.20.5's cloud keeps an entity_effect option tinted with its color.</li>
 * </ul>
 */
public final class CbFinParticles {

    private CbFinParticles() {
    }

    /** a 1.20.4 entity effect particle type: the alpha its provider gave the particle */
    public static final class LegacyEffect extends class_2400 {
        final float alpha;

        LegacyEffect(float alpha) {
            super(false);
            this.alpha = alpha;
        }
    }

    /** SpellParticle.MobProvider: alpha untouched (1) */
    public static final LegacyEffect ENTITY_EFFECT = new LegacyEffect(1.0F);
    /** SpellParticle.AmbientMobProvider: setAlpha(0.15F) */
    public static final LegacyEffect AMBIENT_ENTITY_EFFECT = new LegacyEffect(0.15F);

    /** ParticleTypes.ENTITY_EFFECT (1.20.4 field) */
    public static class_2400 field_11226() {
        return ENTITY_EFFECT;
    }

    /** ParticleTypes.AMBIENT_ENTITY_EFFECT (1.20.4 field) */
    public static class_2400 field_11225() {
        return AMBIENT_ENTITY_EFFECT;
    }

    private static float unit(double v) {
        return (float) Math.max(0.0, Math.min(1.0, v));
    }

    /** the 1.20.5 entity_effect option of a 1.20.4 marker colored (r, g, b) */
    public static class_9381 colored(LegacyEffect effect, double r, double g, double b) {
        int argb = net.minecraft.class_5253.class_5254.method_59554(effect.alpha, unit(r), unit(g), unit(b));
        return class_9381.method_58256(class_2398.field_11226, argb);
    }

    /** the options a particle is made of: a 1.20.4 marker colored by the speeds, anything else as it is */
    public static class_2394 forParticle(class_2394 options, double xSpeed, double ySpeed, double zSpeed) {
        return options instanceof LegacyEffect effect ? colored(effect, xSpeed, ySpeed, zSpeed) : options;
    }

    /** ServerLevel.sendParticles(options, ..) of a 1.20.4 marker (see the class comment): the players reached */
    public static int send(class_3218 level, LegacyEffect effect, double x, double y, double z, int count,
                           double dx, double dy, double dz, double speed) {
        if (count == 0) {
            return level.method_14199(colored(effect, speed * dx, speed * dy, speed * dz), x, y, z, 0, speed * dx, speed * dy, speed * dz, 1.0);
        }
        int reached = 0;
        for (int i = 0; i < count; i++) {
            double xs = level.field_9229.method_43059() * speed;
            double ys = level.field_9229.method_43059() * speed;
            double zs = level.field_9229.method_43059() * speed;
            double px = x + level.field_9229.method_43059() * dx;
            double py = y + level.field_9229.method_43059() * dy;
            double pz = z + level.field_9229.method_43059() * dz;
            reached = Math.max(reached, level.method_14199(colored(effect, xs, ys, zs), px, py, pz, 0, xs, ys, zs, 1.0));
        }
        return reached;
    }

    /** ServerLevel.sendParticles(player, options, force, ..) of a 1.20.4 marker: whether the player was reached */
    public static boolean send(class_3218 level, class_3222 player, LegacyEffect effect, boolean force, double x, double y, double z,
                               int count, double dx, double dy, double dz, double speed) {
        if (count == 0) {
            return level.method_14166(player, colored(effect, speed * dx, speed * dy, speed * dz), force, x, y, z, 0, speed * dx, speed * dy, speed * dz, 1.0);
        }
        boolean reached = false;
        for (int i = 0; i < count; i++) {
            double xs = level.field_9229.method_43059() * speed;
            double ys = level.field_9229.method_43059() * speed;
            double zs = level.field_9229.method_43059() * speed;
            double px = x + level.field_9229.method_43059() * dx;
            double py = y + level.field_9229.method_43059() * dy;
            double pz = z + level.field_9229.method_43059() * dz;
            reached |= level.method_14166(player, colored(effect, xs, ys, zs), force, px, py, pz, 0, xs, ys, zs, 1.0);
        }
        return reached;
    }
}
