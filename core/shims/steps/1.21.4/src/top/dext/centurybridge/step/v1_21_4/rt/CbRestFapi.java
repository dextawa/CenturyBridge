package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.class_5195;
import net.minecraft.class_6005;
import net.minecraft.class_6008;

/**
 * Fabric API and biome music contracts 1.21.4 changed.
 * <ul>
 * <li>CustomIngredient.getMatchingItems(): a stream of holders in 1.21.4, a list in 1.21.3; a 1.21.3
 *     implementation's list is streamed in order ({@link #stream}), a 1.21.3 call collects the stream
 *     (CbRestRecipes.listOf).</li>
 * <li>Biome background music: 1.21.3 had one Music per biome (Optional&lt;Music&gt;), 1.21.4 a weighted list
 *     (Optional&lt;SimpleWeightedRandomList&lt;Music&gt;&gt;). 1.21.3's single music is the one-entry list
 *     ({@link #musicList}); a list answered to 1.21.3 code is its first entry, which for every vanilla biome is
 *     its only one ({@link #firstMusic}). Both adaptive: a value already of the wanted kind passes through.</li>
 * <li>BiomeModificationContext.EffectsContext.setMusicVolume(volume): new and abstract in 1.21.4. A 1.21.3
 *     implementation has no music volume to hold (1.21.3's biome effects had none), so the request changes
 *     nothing it could answer for.</li>
 * </ul>
 */
public final class CbRestFapi {

    private CbRestFapi() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream stream(List list) {
        return list == null ? null : list.stream();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional musicList(Optional music) {
        if (music == null) {
            return null;
        }
        return music.map(m -> m instanceof class_5195 one ? class_6005.method_38061(one) : m);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional firstMusic(Optional music) {
        if (music == null) {
            return null;
        }
        return music.flatMap(m -> {
            if (m instanceof class_6005<?> list) {
                List<? extends class_6008.class_6010<?>> entries = (List) list.method_34994();
                return entries.isEmpty() ? Optional.empty() : Optional.of(entries.get(0).comp_2542());
            }
            return Optional.of(m);
        });
    }

    public static void setMusicVolume(BiomeModificationContext.EffectsContext context, float volume) {
        // a 1.21.3 effects context holds no music volume (see the class comment)
    }
}
