package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.facade.ItemProperties;
import top.dext.centurybridge.step.v1_21_4.facade.ItemPropertyFunction;

/**
 * 1.21.3 item model semantics on 1.21.4's item model definitions. 1.21.4 renders an item through
 * assets/&lt;ns&gt;/items/&lt;id&gt;.json; the converter writes one for each models/item model of a 1.21.3 mod
 * (ResourceTranslator.itemDefinitions). One whose model had "overrides" gets {@code centurybridge:legacy_overrides}:
 * the model, plus the overrides as 1.21.3's BakedOverrides applied them -- every predicate read through
 * ItemProperties.getProperty (an unknown one reads -infinity), and the last override in file order whose every
 * threshold the values reach wins (BakedOverrides stored them reversed and took the first match).
 */
@Environment(EnvType.CLIENT)
public final class CbItemModels {

    private CbItemModels() {
    }

    public static final net.minecraft.class_2960 LEGACY_OVERRIDES = net.minecraft.class_2960.method_60655("centurybridge", "legacy_overrides");

    /** one 1.21.3 override: the predicate thresholds and the model it switches to */
    public record LegacyOverride(Map<net.minecraft.class_2960, Float> predicate, net.minecraft.class_2960 model) {
        static final Codec<LegacyOverride> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(net.minecraft.class_2960.field_25139, Codec.FLOAT).fieldOf("predicate").forGetter(LegacyOverride::predicate),
            net.minecraft.class_2960.field_25139.fieldOf("model").forGetter(LegacyOverride::model)
        ).apply(i, LegacyOverride::new));
    }

    /** the definition's model: base model, overrides in file order, the tints applied to whichever model is chosen */
    public record Unbaked(net.minecraft.class_2960 model, List<LegacyOverride> overrides, List<net.minecraft.class_10401> tints)
            implements net.minecraft.class_10439.class_10441 {

        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            net.minecraft.class_2960.field_25139.fieldOf("model").forGetter(Unbaked::model),
            LegacyOverride.CODEC.listOf().fieldOf("overrides").forGetter(Unbaked::overrides),
            net.minecraft.class_10402.field_55234.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)
        ).apply(i, Unbaked::new));

        @Override
        public MapCodec<Unbaked> method_65585() {
            return CODEC;
        }

        @Override
        public void method_62326(net.minecraft.class_10526.class_10103 resolver) {
            resolver.method_62642(this.model);
            for (LegacyOverride o : this.overrides) {
                resolver.method_62642(o.model());
            }
        }

        @Override
        public net.minecraft.class_10439 method_65587(net.minecraft.class_10439.class_10440 ctx) {
            net.minecraft.class_10439 base = new net.minecraft.class_10430.class_10431(this.model, this.tints).method_65587(ctx);
            // the distinct predicate ids in first-seen order, as BakedOverrides indexed them
            LinkedHashSet<net.minecraft.class_2960> ids = new LinkedHashSet<>();
            for (LegacyOverride o : this.overrides) {
                ids.addAll(o.predicate().keySet());
            }
            List<net.minecraft.class_2960> idList = new ArrayList<>(ids);
            int n = this.overrides.size();
            int[][] index = new int[n][];
            float[][] threshold = new float[n][];
            net.minecraft.class_10439[] models = new net.minecraft.class_10439[n];
            // reversed: the last override in the file is tried first
            for (int k = 0; k < n; k++) {
                LegacyOverride o = this.overrides.get(n - 1 - k);
                index[k] = new int[o.predicate().size()];
                threshold[k] = new float[o.predicate().size()];
                int j = 0;
                for (var e : o.predicate().entrySet()) {
                    index[k][j] = idList.indexOf(e.getKey());
                    threshold[k][j] = e.getValue();
                    j++;
                }
                models[k] = new net.minecraft.class_10430.class_10431(o.model(), this.tints).method_65587(ctx);
            }
            return new Baked(base, idList.toArray(new net.minecraft.class_2960[0]), index, threshold, models);
        }
    }

    static final class Baked implements net.minecraft.class_10439 {
        private final net.minecraft.class_10439 base;
        private final net.minecraft.class_2960[] ids;
        private final int[][] index;
        private final float[][] threshold;
        private final net.minecraft.class_10439[] models;

        Baked(net.minecraft.class_10439 base, net.minecraft.class_2960[] ids, int[][] index, float[][] threshold, net.minecraft.class_10439[] models) {
            this.base = base;
            this.ids = ids;
            this.index = index;
            this.threshold = threshold;
            this.models = models;
        }

        @Override
        public void method_65584(net.minecraft.class_10444 state, net.minecraft.class_1799 stack, net.minecraft.class_10442 resolver,
                net.minecraft.class_811 context, net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed) {
            net.minecraft.class_10439 chosen = this.base;
            if (this.ids.length != 0) {
                float[] values = new float[this.ids.length];
                for (int i = 0; i < this.ids.length; i++) {
                    ItemPropertyFunction p = ItemProperties.method_27878(stack, this.ids[i]);
                    values[i] = p != null ? p.call(stack, level, entity, seed) : Float.NEGATIVE_INFINITY;
                }
                for (int k = 0; k < this.models.length; k++) {
                    boolean all = true;
                    for (int j = 0; j < this.index[k].length && all; j++) {
                        // 1.21.3: a value below its threshold fails the override (a NaN value does not)
                        all = !(values[this.index[k][j]] < this.threshold[k][j]);
                    }
                    if (all) {
                        chosen = this.models[k];
                        break;
                    }
                }
            }
            chosen.method_65584(state, stack, resolver, context, level, entity, seed);
        }
    }
}
