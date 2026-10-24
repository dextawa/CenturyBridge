package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import net.minecraft.class_1842;
import net.minecraft.class_2960;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import org.joml.Vector3f;

/**
 * 1.21.1's Potion, PotionContents and dust particle options on 1.21.2.
 * <ul>
 * <li>Potion(effects) had no name: 1.21.1's Potion.getName(potion, prefix) named such a potion by its registry path
 *     ("empty" when it had none). 1.21.2 names every potion by its own name; one built by 1.21.1's constructor has
 *     none ({@link #unnamed}) and answers name() with that registry path (CbItemPotionNameMixin), so 1.21.2's
 *     PotionContents names it as 1.21.1 did.</li>
 * <li>PotionContents(potion, customColor, customEffects): 1.21.2 adds a custom name; 1.21.1 had none.</li>
 * <li>DustParticleOptions / DustColorTransitionOptions took their colours as Vector3f (0..1 per channel); 1.21.2
 *     takes packed RGB ints and gives Vector3f back from them. The colour is packed to the 8 bits per channel
 *     1.21.2 keeps (a channel off by under 1/255).</li>
 * </ul>
 */
public final class CbItemPotions {
    private CbItemPotions() {
    }

    /** the name 1.21.1's Potion(effects) constructor gave: none */
    public static String unnamed() {
        return null;
    }

    /** 1.21.1 Potion.getName(potion, prefix) */
    public static String getName(Optional<class_6880<class_1842>> potion, String prefix) {
        if (potion.isPresent()) {
            String name = potion.get().comp_349().field_8954;
            if (name != null) {
                return prefix + name;
            }
        }
        return prefix + potion.flatMap(class_6880::method_40230).map(key -> key.method_29177().method_12832()).orElse("empty");
    }

    /** the name a potion built without one answers (CbItemPotionNameMixin): its registry path, "empty" without one */
    public static String registryName(class_1842 potion) {
        class_2960 id = class_7923.field_41179.method_10221(potion);
        return id != null ? id.method_12832() : "empty";
    }

    /** PotionContents' custom name, which 1.21.1's constructor did not have */
    public static Optional<String> noCustomName() {
        return Optional.empty();
    }

    /** a 1.21.1 dust colour as 1.21.2's packed RGB */
    public static int rgb(Vector3f color) {
        return channel(color.x()) << 16 | channel(color.y()) << 8 | channel(color.z());
    }

    private static int channel(float f) {
        return Math.max(0, Math.min(255, Math.round(f * 255.0F)));
    }

    /** 1.21.2's packed RGB as 1.21.1's Vector3f (DustParticleOptions.REDSTONE_PARTICLE_COLOR and the like) */
    public static Vector3f vector(int rgb) {
        return new Vector3f((rgb >> 16 & 255) / 255.0F, (rgb >> 8 & 255) / 255.0F, (rgb & 255) / 255.0F);
    }

    /** DustParticleOptions.REDSTONE_PARTICLE_COLOR (fieldGetters: the getter takes the field's name) */
    public static Vector3f field_28272() {
        return vector(net.minecraft.class_2390.field_28272);
    }

    /** DustColorTransitionOptions.SCULK_PARTICLE_COLOR */
    public static Vector3f field_28267() {
        return vector(net.minecraft.class_5743.field_28267);
    }
}
