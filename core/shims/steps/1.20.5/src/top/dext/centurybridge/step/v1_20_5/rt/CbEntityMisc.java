package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 entity members (area s4fin world/entity) whose 1.20.5 form takes an argument 1.20.4 fixed, or
 * moved to another class:
 * <ul>
 * <li>BehaviorUtils.lockGazeAndWalkToEachOther and AnimalMakeLove: 1.20.5 takes how close the two walk;
 *     1.20.4 always walked them to within 2 blocks.</li>
 * <li>AttributeModifier.Operation.fromValue(id): 1.20.4's lookup, which refused an unknown id.</li>
 * <li>IronGolem.Crackiness moved to Crackiness (shared with wolf armor); byFraction is the golem's.</li>
 * </ul>
 */
public final class CbEntityMisc {

    /** the distance 1.20.4's BehaviorUtils.lockGazeAndWalkToEachOther walked the two entities to */
    public static int walkCloseEnough() {
        return 2;
    }

    /** AttributeModifier.Operation.fromValue(id) (1.20.4: an unknown id is an error; 1.20.5's BY_ID clamps) */
    public static net.minecraft.class_1322.class_1323 operation(int id) {
        for (net.minecraft.class_1322.class_1323 op : net.minecraft.class_1322.class_1323.values()) {
            if (op.method_56082() == id) {
                return op;
            }
        }
        throw new IllegalArgumentException("No operation with value " + id);
    }

    /** IronGolem.Crackiness.byFraction(fraction): the golem's crackiness levels */
    public static net.minecraft.class_9273.class_4621 crackiness(float fraction) {
        return net.minecraft.class_9273.field_49210.method_57282(fraction);
    }

    /**
     * AttributeInstance.getModifiers(operation) (CbEntityAttributeInstanceMixin): 1.20.5's live map of one
     * operation's modifiers by id, as 1.20.4's live set
     */
    public static java.util.Set<net.minecraft.class_1322> modifierSet(java.util.Map<java.util.UUID, net.minecraft.class_1322> byId) {
        return new java.util.AbstractSet<>() {
            @Override
            public java.util.Iterator<net.minecraft.class_1322> iterator() {
                return byId.values().iterator();
            }

            @Override
            public int size() {
                return byId.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof net.minecraft.class_1322 m && m.equals(byId.get(m.comp_2447()));
            }

            @Override
            public boolean add(net.minecraft.class_1322 m) {
                return !m.equals(byId.put(m.comp_2447(), m));
            }

            @Override
            public boolean remove(Object o) {
                return o instanceof net.minecraft.class_1322 m && byId.remove(m.comp_2447(), m);
            }
        };
    }

    private CbEntityMisc() {
    }
}
