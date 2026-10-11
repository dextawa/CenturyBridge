package top.dext.centurybridge.rt.v1_21_4;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_10401;
import net.minecraft.class_10402;
import net.minecraft.class_10430;
import net.minecraft.class_10439;
import net.minecraft.class_10442;
import net.minecraft.class_10444;
import net.minecraft.class_10526;
import net.minecraft.class_1309;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_638;
import net.minecraft.class_811;
import top.dext.centurybridge.facade.v1_21_4.class_1800;
import top.dext.centurybridge.facade.v1_21_4.class_326;
import top.dext.centurybridge.facade.v1_21_4.class_5272;

/**
 * The 1.20.1 item model semantics on 1.21.4's item model definitions, for the
 * definitions the converter writes from a mod's old models/item JSON:
 *
 * <ul>
 * <li>{@code centurybridge:legacy_overrides} -- the model plus its old "overrides":
 * every predicate is read through ModelPredicateProviderRegistry (the facade, which
 * keeps what mods register), an unregistered one reads -infinity, and the last
 * override in file order whose every threshold is met wins -- ModelOverrideList
 * walked its overrides reversed and took the first match.</li>
 * <li>{@code centurybridge:legacy_color} -- one tint index answered by the colour
 * provider the mod registered for the item (ColorProviderRegistry.ITEM), as
 * ItemColors.getColor did; no provider is no tint. Old providers return RGB; the
 * renderer ignored their alpha, 1.21.4 tints are ARGB, so the alpha is made opaque.</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbLegacyItemModels {

    public static final class_2960 OVERRIDES = class_2960.method_60655("centurybridge", "legacy_overrides");
    public static final class_2960 LEGACY_COLOR = class_2960.method_60655("centurybridge", "legacy_color");

    /** the colour providers mods registered, per item (ColorProviderRegistry.ITEM, removed in 1.21.4) */
    public static final Map<class_1792, class_326> ITEM_COLORS = new IdentityHashMap<>();

    /** one old override: every predicate's value must reach its threshold */
    public record LegacyOverride(Map<class_2960, Float> predicate, class_2960 model) {
        static final Codec<LegacyOverride> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(class_2960.field_25139, Codec.FLOAT).fieldOf("predicate").forGetter(LegacyOverride::predicate),
            class_2960.field_25139.fieldOf("model").forGetter(LegacyOverride::model)
        ).apply(i, LegacyOverride::new));
    }

    public record Unbaked(class_2960 model, List<LegacyOverride> overrides, List<class_10401> tints)
            implements class_10439.class_10441 {

        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            class_2960.field_25139.fieldOf("model").forGetter(Unbaked::model),
            LegacyOverride.CODEC.listOf().fieldOf("overrides").forGetter(Unbaked::overrides),
            class_10402.field_55234.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(i, Unbaked::new));

        @Override
        public MapCodec<Unbaked> method_65585() {
            return CODEC;
        }

        @Override
        public void method_62326(class_10526.class_10103 resolver) {
            resolver.method_62642(model);
            for (LegacyOverride o : overrides) {
                resolver.method_62642(o.model());
            }
        }

        @Override
        public class_10439 method_65587(class_10439.class_10440 ctx) {
            class_10439 base = new class_10430.class_10431(model, tints).method_65587(ctx);
            List<class_2960[]> keys = new ArrayList<>();
            List<float[]> thresholds = new ArrayList<>();
            List<class_10439> models = new ArrayList<>();
            for (LegacyOverride o : overrides) {
                class_2960[] k = o.predicate().keySet().toArray(new class_2960[0]);
                float[] t = new float[k.length];
                for (int j = 0; j < k.length; j++) {
                    t[j] = o.predicate().get(k[j]);
                }
                keys.add(k);
                thresholds.add(t);
                models.add(new class_10430.class_10431(o.model(), tints).method_65587(ctx));
            }
            return new Baked(base, keys, thresholds, models);
        }
    }

    static final class Baked implements class_10439 {
        private final class_10439 base;
        private final List<class_2960[]> keys;
        private final List<float[]> thresholds;
        private final List<class_10439> models;

        Baked(class_10439 base, List<class_2960[]> keys, List<float[]> thresholds, List<class_10439> models) {
            this.base = base;
            this.keys = keys;
            this.thresholds = thresholds;
            this.models = models;
        }

        @Override
        public void method_65584(class_10444 state, class_1799 stack, class_10442 resolver, class_811 mode,
                class_638 world, class_1309 entity, int seed) {
            class_10439 chosen = base;
            class_1792 item = stack.method_7909();
            for (int i = models.size() - 1; i >= 0; i--) {
                class_2960[] k = keys.get(i);
                float[] t = thresholds.get(i);
                boolean all = true;
                for (int j = 0; j < k.length && all; j++) {
                    class_1800 p = class_5272.method_27878(item, k[j]);
                    float v = p != null ? p.call(stack, world, entity, seed) : Float.NEGATIVE_INFINITY;
                    all = v >= t[j];
                }
                if (all) {
                    chosen = models.get(i);
                    break;
                }
            }
            chosen.method_65584(state, stack, resolver, mode, world, entity, seed);
        }
    }

    public record LegacyColor(int index) implements class_10401 {
        public static final MapCodec<LegacyColor> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("index").forGetter(LegacyColor::index)
        ).apply(i, LegacyColor::new));

        @Override
        public int method_65389(class_1799 stack, class_638 world, class_1309 entity) {
            class_326 provider = ITEM_COLORS.get(stack.method_7909());
            if (provider == null) {
                return -1;
            }
            int rgb = provider.getColor(stack, index);
            return rgb == -1 ? -1 : rgb | 0xFF000000;
        }

        @Override
        public MapCodec<LegacyColor> method_65387() {
            return CODEC;
        }
    }

    private CbLegacyItemModels() {
    }
}
