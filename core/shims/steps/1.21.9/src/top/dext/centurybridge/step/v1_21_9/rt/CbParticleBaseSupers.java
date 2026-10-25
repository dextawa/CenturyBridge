package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3940;
import net.minecraft.class_4002;
import net.minecraft.class_703;
import net.minecraft.class_711;

/**
 * super.setColor(..) / super.setAlpha(..) / super.pickSprite(..) / super.calculateState(..) from a 1.21.8 particle override, one overload per
 * 1.21.8 class declaring the member (Particle, SpellParticle for setAlpha, TextureSheetParticle for pickSprite),
 * the receiver typed as the key names it. On a SingleQuadParticle the superclass's body is 1.21.9's
 * setColor / setAlpha: the call is made with the particle's guard held, so the override hook synthesized in the
 * mod class falls through to vanilla's (super) instead of forwarding to the mod's override again; on any other
 * particle it is 1.21.8 Particle's own body (CbParticles.baseColor / baseAlpha)
 */
@Environment(EnvType.CLIENT)
public final class CbParticleBaseSupers {
    private CbParticleBaseSupers() {
    }

    private static void color(class_703 p, float r, float g, float b) {
        if (p instanceof class_3940 q) {
            boolean entered = Guard.cbEnter(q);
            try {
                q.method_74305(r, g, b);
            } finally {
                if (entered) {
                    Guard.cbExit(q);
                }
            }
        } else {
            CbParticles.baseColor(p, r, g, b);
        }
    }

    private static void alpha(class_703 p, float a) {
        if (p instanceof class_3940 q) {
            boolean entered = Guard.cbEnter(q);
            try {
                q.method_74308(a);
            } finally {
                if (entered) {
                    Guard.cbExit(q);
                }
            }
        } else {
            CbParticles.baseAlpha(p, a);
        }
    }

    public static void method_3084(class_703 p, float r, float g, float b) {
        color(p, r, g, b);
    }

    public static void method_3083(class_703 p, float a) {
        alpha(p, a);
    }

    public static void method_3083(class_711 p, float a) {
        alpha(p, a);
    }

    /** TextureSheetParticle.pickSprite(sprites): 1.21.8's body (setSprite of a random sprite of the set, setSprite virtual) */
    public static void method_18140(class_3940 p, class_4002 sprites) {
        p.method_74307(sprites.method_18139(p.field_3840));
    }

    /** BreakingItemParticle.ItemParticleProvider.calculateState(stack, level): 1.21.8's body (the provider's scratch state, updated for the item on the ground) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_10444 method_65524(net.minecraft.class_647.class_10417 provider, net.minecraft.class_1799 stack,
            net.minecraft.class_638 level) {
        net.minecraft.class_310.method_1551().method_65386().method_65598(provider.field_55268, stack, net.minecraft.class_811.field_4318, level, null, 0);
        return provider.field_55268;
    }
}
