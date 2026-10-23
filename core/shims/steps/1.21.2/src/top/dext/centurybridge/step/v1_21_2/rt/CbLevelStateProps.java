package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_2350;
import net.minecraft.class_2754;
import net.minecraft.class_2769;
import net.minecraft.class_3542;

/**
 * 1.21.1's block state property API on 1.21.2.
 * <ul>
 * <li>Property.getPossibleValues() answers a List since 1.21.2 (a Collection in 1.21.1; vanilla's were insertion
 *     ordered sets): a 1.21.1 call takes the List as the Collection it is. A 1.21.1 Property subclass's
 *     getPossibleValues answers 1.21.2 as a List in its iteration order, duplicates dropped as 1.21.1's sets did.
 *     1.21.2 also asks a property the index of a value
 *     (getInternalIndex, new and abstract): a 1.21.1 subclass answers its value's position in that list.</li>
 * <li>DirectionProperty is gone (1.21.2 uses EnumProperty&lt;Direction&gt;; classRenames to EnumProperty): its
 *     factories are 1.21.1's -- every direction, those a predicate keeps, the given ones, a collection -- as
 *     EnumProperty.create of Direction.</li>
 * <li>EnumProperty.create(name, class, collection) takes a List since 1.21.2: the collection's values in order, each
 *     once (1.21.1 kept them in an ImmutableSet).</li>
 * <li>StateHolder.findNextInCollection(collection, value): 1.21.1's body (the value after it, wrapping to the first;
 *     a value not in the collection runs the iterator past its end, as 1.21.1's did).</li>
 * </ul>
 */
public final class CbLevelStateProps {

    /** a collection of values as the List 1.21.2 wants: its order, each value once */
    public static <T> List<T> list(Collection<T> values) {
        if (values instanceof List<T> l && new LinkedHashSet<>(l).size() == l.size()) {
            return l;
        }
        return new ArrayList<>(new LinkedHashSet<>(values));
    }

    public interface LegacyPossibleValues {
        Collection<?> method_11898();
    }

    /** a 1.21.1 property's getPossibleValues, as 1.21.2's List (override hook unwrap) */
    @SuppressWarnings("unchecked")
    public static List<?> possibleValues(Collection<?> values) {
        return list((Collection<Object>) values);
    }

    /** getInternalIndex(value) of a 1.21.1 Property subclass (abstractFills): its value's position */
    public static int internalIndex(class_2769<?> self, Comparable<?> value) {
        return self.method_11898().indexOf(value);
    }

    // ---- DirectionProperty ----

    /**
     * {@code x instanceof DirectionProperty} (instanceOfRedirects): 1.21.1's DirectionProperty was the EnumProperty of
     * Direction; with the class renamed onto EnumProperty the test must still say no to every other enum property
     */
    public static boolean isDirectionProperty(Object o) {
        return o instanceof class_2754<?> e && e.method_11902() == class_2350.class;
    }

    /** {@code (DirectionProperty) x} (checkCastRedirects): the cast 1.21.1 made, before the renamed one */
    public static Object castDirectionProperty(Object o) {
        if (o == null || isDirectionProperty(o)) {
            return o;
        }
        throw new ClassCastException("class " + o.getClass().getName()
            + " cannot be cast to 1.21.1's DirectionProperty (an EnumProperty of Direction)");
    }

    /** DirectionProperty.create(name): every direction */
    public static class_2754<class_2350> method_35305(String name) {
        return method_11844(name, d -> true);
    }

    /** DirectionProperty.create(name, predicate) */
    public static class_2754<class_2350> method_11844(String name, Predicate<class_2350> filter) {
        List<class_2350> values = new ArrayList<>();
        for (class_2350 d : class_2350.values()) {
            if (filter.test(d)) {
                values.add(d);
            }
        }
        return method_11843(name, values);
    }

    /** DirectionProperty.create(name, directions...) */
    public static class_2754<class_2350> method_11845(String name, class_2350... values) {
        return method_11843(name, java.util.Arrays.asList(values));
    }

    /** DirectionProperty.create(name, collection) */
    public static class_2754<class_2350> method_11843(String name, Collection<class_2350> values) {
        return class_2754.method_11847(name, class_2350.class, list(values));
    }

    /** EnumProperty.create(name, class, collection) */
    public static <T extends Enum<T> & class_3542> class_2754<T> method_11847(String name, Class<T> type,
                                                                              Collection<T> values) {
        return class_2754.method_11847(name, type, list(values));
    }

    // ---- StateHolder ----

    /** StateHolder.findNextInCollection(collection, value) */
    public static <T> T method_28495(Collection<T> values, T value) {
        Iterator<T> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(value)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return values.iterator().next();
            }
        }
        return iterator.next();
    }

    private CbLevelStateProps() {
    }
}
