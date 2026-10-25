package top.dext.centurybridge.rt.v1_20_2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_5131;
import net.minecraft.class_5132;
import net.minecraft.class_6880;

/**
 * 1.20.1's living entity applied and removed an effect's attributes through addAttributeModifiers(entity,
 * attributes, amplifier) / removeAttributeModifiers(entity, attributes, amplifier); 1.20.2's calls add(attributes,
 * amplifier) / remove(attributes) (onEffectAdded, onEffectUpdated, onEffectRemoved). Vanilla keeps calling 1.20.2's
 * methods, so a 1.20.2-era mod's mixin on them acts as without CenturyBridge; the restored 1.20.1 members
 * (StatusEffectBridge) are consulted there:
 *
 * before 1.20.2's call, the restored member (which a converted mod's mixin may change) and its unmixed copy
 * (CbEffects.golden*) each run on a scratch copy of the entity's attributes; 1.20.2's call then runs on the real
 * ones. Only where the two scratch copies end up different -- a mixin on the restored member changed what it does
 * -- is each attribute either copy touched set to what the restored member made of it, as 1.20.1 would have.
 * Unmixed, the attributes are exactly what 1.20.2's call made them.
 *
 * An effect whose 1.20.1 behaviour differs (CbLegacyMobEffectOverride: a converted class overriding the hooks, the
 * 1.20.1 effect classes CenturyBridge ships again) is applied through the restored members, as before.
 * -Dcenturybridge.legacyEffectAttributeRoute=false: the restored members are never consulted for other effects.
 */
public final class CbEffectAttributeRoute {

    public static final boolean ROUTE = !"false".equals(System.getProperty("centurybridge.legacyEffectAttributeRoute"));

    private static final class_5132 NO_DEFAULTS = class_5132.method_26861().method_26866();

    private CbEffectAttributeRoute() {
    }

    /** LivingEntity's effect.add(attributes, amplifier) */
    public static void add(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        if (effect instanceof CbLegacyMobEffectOverride legacy) {
            legacy.method_5555(entity, attributes, amplifier);
            return;
        }
        if (!ROUTE) {
            effect.method_5555(attributes, amplifier);
            return;
        }
        Scratch mixed = new Scratch(attributes);
        ((CbLegacyMobEffect) effect).method_5555(entity, mixed, amplifier);
        Scratch golden = new Scratch(attributes);
        CbEffects.goldenAddAttributeModifiers(effect, golden, amplifier);
        Set<class_1320> touched = mixed.alignWith(golden);
        effect.method_5555(attributes, amplifier);
        mixed.applyIfDifferent(golden, touched, attributes);
    }

    /** LivingEntity's effect.remove(attributes), the instance's amplifier being what 1.20.1 passed */
    public static void remove(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        if (effect instanceof CbLegacyMobEffectOverride legacy) {
            legacy.method_5562(entity, attributes, amplifier);
            return;
        }
        if (!ROUTE) {
            effect.method_5562(attributes);
            return;
        }
        Scratch mixed = new Scratch(attributes);
        ((CbLegacyMobEffect) effect).method_5562(entity, mixed, amplifier);
        Scratch golden = new Scratch(attributes);
        CbEffects.goldenRemoveAttributeModifiers(effect, golden);
        Set<class_1320> touched = mixed.alignWith(golden);
        effect.method_5562(attributes);
        mixed.applyIfDifferent(golden, touched, attributes);
    }

    /**
     * An attribute map answering copies of a real map's instances, made the first time each is asked (so a copy is
     * the real instance as it stood then); nothing done to a copy reaches the real map.
     */
    static final class Scratch extends class_5131 {
        private final class_5131 real;
        private final Map<class_1320, class_1324> copies = new LinkedHashMap<>();

        Scratch(class_5131 real) {
            super(NO_DEFAULTS);
            this.real = real;
        }

        @Override
        public class_1324 method_26842(class_1320 attribute) {
            class_1324 copy = this.copies.get(attribute);
            if (copy == null && !this.copies.containsKey(attribute)) {
                class_1324 of = this.real.method_26842(attribute);
                if (of != null) {
                    copy = new class_1324(attribute, i -> { });
                    copy.method_26831(of);
                }
                this.copies.put(attribute, copy);
            }
            return copy;
        }

        @Override
        public class_1324 method_45329(class_6880<class_1320> attribute) {
            return this.method_26842(attribute.comp_349());
        }

        @Override
        public boolean method_27306(class_1320 attribute) {
            return this.method_26842(attribute) != null;
        }

        @Override
        public double method_26852(class_1320 attribute) {
            class_1324 instance = this.method_26842(attribute);
            return instance != null ? instance.method_6194() : this.real.method_26852(attribute);
        }

        @Override
        public double method_26856(class_1320 attribute) {
            class_1324 instance = this.method_26842(attribute);
            return instance != null ? instance.method_6201() : this.real.method_26856(attribute);
        }

        /** the attributes either map touched, each now copied into both (taken from the real map before 1.20.2's call) */
        Set<class_1320> alignWith(Scratch other) {
            Set<class_1320> touched = new LinkedHashSet<>(this.copies.keySet());
            touched.addAll(other.copies.keySet());
            for (class_1320 a : touched) {
                this.method_26842(a);
                other.method_26842(a);
            }
            return touched;
        }

        /** where this map and {@code golden} differ on a touched attribute, every touched real instance becomes this map's */
        void applyIfDifferent(Scratch golden, Set<class_1320> touched, class_5131 attributes) {
            boolean differ = false;
            for (class_1320 a : touched) {
                if (!state(this.copies.get(a)).equals(state(golden.copies.get(a)))) {
                    differ = true;
                    break;
                }
            }
            if (!differ) {
                return;
            }
            for (class_1320 a : touched) {
                class_1324 to = attributes.method_26842(a);
                class_1324 from = this.copies.get(a);
                if (to != null && from != null) {
                    to.method_26831(from);
                }
            }
        }

        /** base value and every modifier (id, name, amount, operation), in a fixed order */
        private static List<Object> state(class_1324 instance) {
            List<Object> out = new ArrayList<>();
            if (instance == null) {
                return out;
            }
            out.add(instance.method_6201());
            List<class_1322> modifiers = new ArrayList<>(instance.method_6195());
            modifiers.sort(Comparator.comparing(m -> m.method_6189().toString()));
            for (class_1322 m : modifiers) {
                out.add(List.of(m.method_6189(), Objects.toString(m.method_6185()), m.method_6186(), m.method_6182()));
            }
            return out;
        }
    }
}
