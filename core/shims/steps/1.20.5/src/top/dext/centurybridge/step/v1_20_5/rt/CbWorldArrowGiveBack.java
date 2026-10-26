package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Arrow's 1.20.4 potion state, given back for the code that still reads it -- mods' mixins and the climbing 1.20.3
 * layer's (CbWorldArrowMixin; instanceFieldGetters, MixinBridge carries its @Shadow reads through these).
 * <p>
 * 1.20.4's Arrow kept the potion (field_7597), the custom effects (field_7598) and whether its colour was fixed
 * (field_7596) in fields of its own, set together from the item (setEffectsFromItem) or the save data. 1.20.5 keeps the
 * same three as the PotionContents of the arrow's pickup stack (getPotionContents / setPotionContents, javap: the potion
 * holder, the custom colour, the custom effects; addEffect and the colour read and write it there).
 */
public final class CbWorldArrowGiveBack {

    /** Arrow.potion: the contents' potion, 1.20.4's empty potion when it has none */
    public static net.minecraft.class_1842 field_7597(net.minecraft.class_1667 arrow) {
        return CbPotions.potion(arrow.method_57315().comp_2378());
    }

    /** Arrow.fixedColor: 1.20.4 fixed the colour exactly when the item or save data gave one, 1.20.5's custom colour */
    public static boolean field_7596(net.minecraft.class_1667 arrow) {
        return arrow.method_57315().comp_2379().isPresent();
    }

    /** writing Arrow.potion: the contents with that potion (1.20.4's empty potion: none), custom colour and effects kept */
    public static void field_7597(net.minecraft.class_1667 arrow, net.minecraft.class_1842 potion) {
        net.minecraft.class_1844 contents = arrow.method_57315();
        @SuppressWarnings("unchecked")
        java.util.Optional<net.minecraft.class_6880<net.minecraft.class_1842>> holder = CbPotions.holder(potion);
        arrow.method_57316(new net.minecraft.class_1844(holder, contents.comp_2379(), contents.comp_2380()));
    }

    /**
     * writing Arrow.fixedColor: 1.20.4 kept the flag beside the colour in the entity data; fixing it keeps the colour the
     * arrow shows now as the custom colour, unfixing drops the custom colour (1.20.5 then derives it from the contents)
     */
    public static void field_7596(net.minecraft.class_1667 arrow, boolean fixed) {
        net.minecraft.class_1844 contents = arrow.method_57315();
        java.util.Optional<Integer> color = fixed ? java.util.Optional.of(arrow.method_7460()) : java.util.Optional.empty();
        arrow.method_57316(new net.minecraft.class_1844(contents.comp_2378(), color, contents.comp_2380()));
    }

    /**
     * Arrow.effects: a live view of the contents' custom effects. Adding goes through 1.20.5's addEffect (1.20.4's
     * addEffect added to this set); removing writes the contents back without the effect.
     */
    public static java.util.Set<net.minecraft.class_1293> field_7598(net.minecraft.class_1667 arrow) {
        return new java.util.AbstractSet<>() {
            @Override
            public java.util.Iterator<net.minecraft.class_1293> iterator() {
                java.util.Iterator<net.minecraft.class_1293> it = new java.util.ArrayList<>(arrow.method_57315().comp_2380()).iterator();
                return new java.util.Iterator<>() {
                    private net.minecraft.class_1293 last;

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public net.minecraft.class_1293 next() {
                        return last = it.next();
                    }

                    @Override
                    public void remove() {
                        if (last == null) {
                            throw new IllegalStateException();
                        }
                        removeEffect(arrow, last);
                        last = null;
                    }
                };
            }

            @Override
            public int size() {
                return arrow.method_57315().comp_2380().size();
            }

            @Override
            public boolean add(net.minecraft.class_1293 effect) {
                if (contains(effect)) {
                    return false;
                }
                arrow.method_7463(effect);
                return true;
            }

            @Override
            public boolean remove(Object o) {
                return o instanceof net.minecraft.class_1293 e && removeEffect(arrow, e);
            }
        };
    }

    private static boolean removeEffect(net.minecraft.class_1667 arrow, net.minecraft.class_1293 effect) {
        net.minecraft.class_1844 contents = arrow.method_57315();
        java.util.List<net.minecraft.class_1293> effects = new java.util.ArrayList<>(contents.comp_2380());
        if (!effects.remove(effect)) {
            return false;
        }
        arrow.method_57316(new net.minecraft.class_1844(contents.comp_2378(), contents.comp_2379(), effects));
        return true;
    }

    private CbWorldArrowGiveBack() {
    }
}
