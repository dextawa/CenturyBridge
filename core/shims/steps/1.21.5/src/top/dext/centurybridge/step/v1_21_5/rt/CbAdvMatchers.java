package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Map;

/**
 * 1.21.4's component parts of the advancement predicates on 1.21.5 (data-advancements area). 1.21.5 folded
 * ItemPredicate's two component parts (DataComponentPredicate components, Map&lt;ItemSubPredicate.Type, ItemSubPredicate&gt;
 * subPredicates; record components comp_2374 / comp_2462) into one DataComponentMatchers (class_10914) holding exactly
 * those two (javap: its constructor takes (class_9329, Map), its test is components.test then every sub-predicate, as
 * 1.21.4's ItemPredicate.test did on the stack), and gave EntityPredicate and BlockPredicate a DataComponentMatchers they
 * did not have in 1.21.4. Their 1.21.4 constructors (superCtorReshapes) pass DataComponentMatchers.ANY there
 * (field_58044: the empty predicate and no sub-predicates, which every holder passes), so a 1.21.4 predicate keeps
 * matching what it matched.
 */
public final class CbAdvMatchers {

    private CbAdvMatchers() {
    }

    /** DataComponentMatchers.ANY, for EntityPredicate's / BlockPredicate's new last component */
    public static net.minecraft.class_10914 any() {
        return net.minecraft.class_10914.field_58044;
    }

    /** ItemPredicate's 1.21.4 components + subPredicates, as 1.21.5's DataComponentMatchers of the two */
    public static net.minecraft.class_10914 matchers(net.minecraft.class_9329 components, Map<?, ?> subPredicates) {
        @SuppressWarnings("unchecked")
        Map<net.minecraft.class_9360.class_8745<?>, net.minecraft.class_9360> map =
            (Map<net.minecraft.class_9360.class_8745<?>, net.minecraft.class_9360>) subPredicates;
        return new net.minecraft.class_10914(components, map);
    }

    /** was ItemPredicate.components() (comp_2374 answering DataComponentPredicate) */
    public static net.minecraft.class_9329 components(net.minecraft.class_2073 predicate) {
        return predicate.comp_2374().comp_3833();
    }

    /** was ItemPredicate.subPredicates() (comp_2462) */
    public static Map<net.minecraft.class_9360.class_8745<?>, net.minecraft.class_9360> subPredicates(net.minecraft.class_2073 predicate) {
        return predicate.comp_2374().comp_3834();
    }
}
