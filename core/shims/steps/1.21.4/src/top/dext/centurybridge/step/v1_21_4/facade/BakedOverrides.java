package top.dext.centurybridge.step.v1_21_4.facade;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 BakedOverrides (class_806, gone in 1.21.4): a model's overrides, baked -- each override's model baked with the
 * baker, its predicates indexed over the distinct properties; resolve reads every property through ItemProperties
 * (unknown: -infinity) and answers the model of the last override (in file order) whose thresholds the values reach,
 * or null. 1.21.3's code.
 */
@Environment(EnvType.CLIENT)
public class BakedOverrides {

    /** EMPTY */
    public static final BakedOverrides field_4292 = new BakedOverrides();
    /** NO_OVERRIDE */
    public static final float field_42072 = Float.NEGATIVE_INFINITY;
    /** overrides */
    public final BakedOverride[] field_28792;
    /** properties */
    public final net.minecraft.class_2960[] field_28793;

    public BakedOverrides() {
        this.field_28792 = new BakedOverride[0];
        this.field_28793 = new net.minecraft.class_2960[0];
    }

    public BakedOverrides(net.minecraft.class_7775 baker, List<ItemOverride> overrides) {
        this.field_28793 = overrides.stream().flatMap(o -> o.comp_3194().stream()).map(ItemOverride.Predicate::comp_3195).distinct()
            .toArray(net.minecraft.class_2960[]::new);
        Object2IntMap<net.minecraft.class_2960> index = new Object2IntOpenHashMap<>();
        for (int i = 0; i < this.field_28793.length; i++) {
            index.put(this.field_28793[i], i);
        }
        List<BakedOverride> baked = new ArrayList<>();
        for (int i = overrides.size() - 1; i >= 0; i--) {
            ItemOverride o = overrides.get(i);
            net.minecraft.class_1087 model = baker.method_45873(o.comp_3193(), net.minecraft.class_1086.field_5350);
            PropertyMatcher[] matchers = o.comp_3194().stream()
                .map(p -> new PropertyMatcher(index.getInt(p.comp_3195()), p.comp_3196())).toArray(PropertyMatcher[]::new);
            baked.add(new BakedOverride(matchers, model));
        }
        this.field_28792 = baked.toArray(new BakedOverride[0]);
    }

    /** findOverride */
    public net.minecraft.class_1087 method_64063(net.minecraft.class_1799 stack, net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed) {
        int n = this.field_28793.length;
        if (n != 0) {
            float[] values = new float[n];
            for (int i = 0; i < n; i++) {
                ItemPropertyFunction p = ItemProperties.method_27878(stack, this.field_28793[i]);
                values[i] = p != null ? p.call(stack, level, entity, seed) : Float.NEGATIVE_INFINITY;
            }
            for (BakedOverride o : this.field_28792) {
                if (o.method_33700(values)) {
                    return o.comp_3190;
                }
            }
        }
        return null;
    }

    /** BakedOverrides.BakedOverride (class_806$class_5827) */
    @Environment(EnvType.CLIENT)
    public static final class BakedOverride {
        public final PropertyMatcher[] comp_3189;
        public final net.minecraft.class_1087 comp_3190;

        public BakedOverride(PropertyMatcher[] matchers, net.minecraft.class_1087 model) {
            this.comp_3189 = matchers;
            this.comp_3190 = model;
        }

        /** test */
        public boolean method_33700(float[] values) {
            for (PropertyMatcher m : this.comp_3189) {
                if (values[m.comp_3191] < m.comp_3192) {
                    return false;
                }
            }
            return true;
        }

        public PropertyMatcher[] comp_3189() {
            return this.comp_3189;
        }

        public net.minecraft.class_1087 comp_3190() {
            return this.comp_3190;
        }
    }

    /** BakedOverrides.PropertyMatcher (class_806$class_5828) */
    @Environment(EnvType.CLIENT)
    public static final class PropertyMatcher {
        public final int comp_3191;
        public final float comp_3192;

        public PropertyMatcher(int index, float value) {
            this.comp_3191 = index;
            this.comp_3192 = value;
        }

        public int comp_3191() {
            return this.comp_3191;
        }

        public float comp_3192() {
            return this.comp_3192;
        }
    }
}
