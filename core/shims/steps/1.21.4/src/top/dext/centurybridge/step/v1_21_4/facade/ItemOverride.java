package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.3 ItemOverride (class_799, a record gone in 1.21.4): an item model override -- its model and predicate thresholds */
@Environment(EnvType.CLIENT)
public final class ItemOverride {

    /** model */
    public final net.minecraft.class_2960 comp_3193;
    /** predicates */
    public final List<Predicate> comp_3194;

    public ItemOverride(net.minecraft.class_2960 model, List<Predicate> predicates) {
        this.comp_3193 = model;
        this.comp_3194 = predicates;
    }

    public net.minecraft.class_2960 comp_3193() {
        return this.comp_3193;
    }

    public List<Predicate> comp_3194() {
        return this.comp_3194;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ItemOverride other && Objects.equals(this.comp_3193, other.comp_3193) && Objects.equals(this.comp_3194, other.comp_3194);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_3193) + Objects.hashCode(this.comp_3194);
    }

    @Override
    public String toString() {
        return "ItemOverride[model=" + this.comp_3193 + ", predicates=" + this.comp_3194 + "]";
    }

    /** ItemOverride.Predicate (class_799$class_5826): a property and its threshold */
    @Environment(EnvType.CLIENT)
    public static final class Predicate {
        /** property */
        public final net.minecraft.class_2960 comp_3195;
        /** value */
        public final float comp_3196;

        public Predicate(net.minecraft.class_2960 property, float value) {
            this.comp_3195 = property;
            this.comp_3196 = value;
        }

        public net.minecraft.class_2960 comp_3195() {
            return this.comp_3195;
        }

        public float comp_3196() {
            return this.comp_3196;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Predicate other && Objects.equals(this.comp_3195, other.comp_3195)
                && Float.compare(this.comp_3196, other.comp_3196) == 0;
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hashCode(this.comp_3195) + Float.hashCode(this.comp_3196);
        }

        @Override
        public String toString() {
            return "Predicate[property=" + this.comp_3195 + ", value=" + this.comp_3196 + "]";
        }
    }
}
