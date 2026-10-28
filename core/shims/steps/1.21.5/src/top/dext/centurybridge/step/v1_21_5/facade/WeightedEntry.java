package top.dext.centurybridge.step.v1_21_5.facade;

import net.minecraft.class_6010;

/**
 * 1.21.4's net.minecraft.util.random.WeightedEntry (net/minecraft/class_6008) and its IntrusiveBase
 * (class_6008$class_6009), gone in 1.21.5: a 1.21.5 WeightedList holds Weighted (value, int weight) records, the
 * weight outside the value. Converted references name these types (classRenames); 1.21.4's Wrapper record is 1.21.5's
 * Weighted (classRenames, net/minecraft/class_6010). Where a converted mod hands entries to a 1.21.5 list the wiring
 * wraps them into Weighted with their own weight (rt/CbWeighted). Bodies from javap -c -p 1.21.4 class_6008 /
 * class_6008$class_6009: wrap(value, weight) = new Wrapper(value, Weight.of(weight)); IntrusiveBase keeps a Weight
 * (its int constructor goes through Weight.of) and answers it.
 */
public interface WeightedEntry {

    /** getWeight() */
    Weight method_34979();

    /** wrap(value, weight): 1.21.4's Wrapper is 1.21.5's Weighted, whose constructor checks the weight as Weight.of did */
    static <T> class_6010<T> method_34980(T value, int weight) {
        return new class_6010<>(value, weight);
    }

    /** 1.21.4 WeightedEntry.IntrusiveBase */
    class IntrusiveBase implements WeightedEntry {
        /** weight (public: a facade's members stand in for the class's) */
        public final Weight field_29931;

        public IntrusiveBase(int weight) {
            this.field_29931 = Weight.method_34977(weight);
        }

        public IntrusiveBase(Weight weight) {
            this.field_29931 = weight;
        }

        @Override
        public Weight method_34979() {
            return this.field_29931;
        }
    }
}
