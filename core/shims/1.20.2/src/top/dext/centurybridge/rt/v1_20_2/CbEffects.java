package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_1291;
import net.minecraft.class_1309;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_5131;
import net.minecraft.class_7917;
import net.minecraft.class_7923;
import net.minecraft.class_8630;
import top.dext.centurybridge.shims.v1_20_2.CbEffectTemplateAccess;

/**
 * 1.20.1 status effects on 1.20.2 (s1misc effect bridge).
 *
 * Attributes: 1.20.1 applied an effect's attribute modifiers through
 * addAttributeModifiers(entity, attributes, amplifier), each worth
 * getAttributeModifierValue(amplifier, modifier), and removed them through
 * removeAttributeModifiers(entity, attributes, amplifier) -- all three overridable, and
 * AttackDamageMobEffect / HealthBoostMobEffect / AbsorptionMobEffect did. 1.20.2 calls
 * add(attributes, amplifier) / remove(attributes) and lets each AttributeModifierTemplate
 * compute its value. The 1.20.1 methods are back on MobEffect with 1.20.1's bodies
 * (StatusEffectBridge, using {@link #addAttributeModifiers} / {@link #removeAttributeModifiers});
 * for an effect whose 1.20.1 behaviour differs ({@link CbLegacyMobEffectOverride}) the living
 * entity calls them again (CbLivingEffectAttributes) and the effect's templates answer with its
 * getAttributeModifierValue (CbEffectTemplateLegacyValue) wherever 1.20.2 asks them (the
 * potion tooltip included).
 *
 * getAttributeModifiers(): 1.20.1 answered the effect's own map of modifiers, 1.20.2 its map of
 * templates, behind the same Map descriptor (generic drift): a converted call reads a live
 * 1.20.1 view of the map ({@link #legacyModifiers}), and a converted override's answer reaches
 * 1.20.2 as a template view of that effect ({@link #templates}).
 *
 * Suspicious-stew holders: 1.20.1's one effect + duration, 1.20.2's list of entries.
 */
public final class CbEffects {

    // ---- 1.20.1 MobEffect statics (the registry's numeric ids)

    /** MobEffect.byId(id) */
    public static class_1291 method_5569(int id) {
        return class_7923.field_41174.method_10200(id);
    }

    /** MobEffect.getId(effect) */
    public static int method_5554(class_1291 effect) {
        return class_7923.field_41174.method_10206(effect);
    }

    /** MobEffect.getIdFromNullable(effect): the same registry lookup in 1.20.1 */
    public static int method_43257(class_1291 effect) {
        return class_7923.field_41174.method_10206(effect);
    }

    // ---- attributes

    /**
     * The modifier 1.20.1 stored for a template: its id, the effect's description id as its
     * name, the template's base amount and operation. A vanilla template is read field by field
     * (its create(amplifier) may itself be answering through getAttributeModifierValue); a 1.20.1
     * modifier standing as a template ({@link CbLegacyEffectTemplate}) is that modifier; any
     * other template by what it creates at amplifier 0, which is its base amount.
     */
    public static class_1322 baseModifier(class_1291 effect, class_8630 template) {
        if (template instanceof CbLegacyEffectTemplate legacy) {
            return legacy.modifier;
        }
        if (template instanceof CbEffectTemplateAccess vanilla) {
            return new class_1322(vanilla.cb$id(), effect::method_5567, vanilla.cb$amount(), vanilla.cb$operation());
        }
        class_1322 m = template.method_52522(0);
        return new class_1322(m.method_6189(), effect::method_5567, m.method_6186(), m.method_6182());
    }

    /** the modifier 1.20.1 added at an amplifier: named "description amplifier", worth getAttributeModifierValue */
    public static class_1322 legacyModifier(class_1291 effect, class_1322 base, int amplifier) {
        return new class_1322(base.method_6189(), effect.method_5567() + " " + amplifier,
            ((CbLegacyMobEffect) effect).method_5563(amplifier, base), base.method_6182());
    }

    /**
     * 1.20.1 MobEffect.addAttributeModifiers(entity, attributes, amplifier). Like 1.20.1 it reads
     * the effect's own modifier map, not the overridable getAttributeModifiers() (a 1.20.1
     * override of that changed the potion tooltip, never what the effect applied).
     */
    public static void addAttributeModifiers(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        for (Map.Entry<class_1320, class_8630> e : ((CbEffectModifierMap) effect).cb$modifierMap().entrySet()) {
            class_1324 instance = attributes.method_26842(e.getKey());
            if (instance != null) {
                class_1322 base = baseModifier(effect, e.getValue());
                instance.method_6200(base.method_6189());
                instance.method_26837(legacyModifier(effect, base, amplifier));
            }
        }
    }

    /** 1.20.1 MobEffect.removeAttributeModifiers(entity, attributes, amplifier), from the effect's own map too */
    public static void removeAttributeModifiers(class_1291 effect, class_1309 entity, class_5131 attributes, int amplifier) {
        for (Map.Entry<class_1320, class_8630> e : ((CbEffectModifierMap) effect).cb$modifierMap().entrySet()) {
            class_1324 instance = attributes.method_26842(e.getKey());
            if (instance != null) {
                instance.method_6200(e.getValue().method_52521());
            }
        }
    }

    /** 1.20.1 MobEffect.getAttributeModifierValue(amplifier, modifier) */
    public static double attributeModifierValue(int amplifier, class_1322 modifier) {
        return modifier.method_6186() * (double) (amplifier + 1);
    }

    // ---- getAttributeModifiers(): Map<Attribute, AttributeModifier> (1.20.1) and
    // Map<Attribute, AttributeModifierTemplate> (1.20.2)

    /** each effect's own template map -> that effect (CbEffectModifierOwner), both held weakly, the map by identity */
    private static final ConcurrentMap<Object, class_1291> OWNERS = new MapMaker().weakKeys().weakValues().makeMap();

    /** template -> the 1.20.1 modifier read for it, so a view answers the same object every time, as 1.20.1's map did */
    private static final ConcurrentMap<Object, class_1322> BASE = new MapMaker().weakKeys().makeMap();

    /** an effect's constructor has returned: its own map, and whose it is */
    public static void ownModifiers(Map<class_1320, class_8630> map, class_1291 effect) {
        OWNERS.put(map, effect);
    }

    /** the effect a template map belongs to: the one that owns it, else the effect of a template in it; null when unknown */
    private static class_1291 ownerOf(Map<?, ?> templates) {
        class_1291 owner = OWNERS.get(templates);
        if (owner != null) {
            return owner;
        }
        for (Object t : templates.values()) {
            if (t instanceof CbEffectTemplateAccess vanilla) {
                return vanilla.cb$effect();
            }
            if (t instanceof CbLegacyEffectTemplate legacy && legacy.effect != null) {
                return legacy.effect;
            }
        }
        return null;
    }

    /** a map value as 1.20.1 holds it: a modifier (one already is passes as it is) */
    private static Object toModifier(class_1291 effect, Object v) {
        if (!(v instanceof class_8630 template)) {
            return v;
        }
        if (template instanceof CbLegacyEffectTemplate legacy) {
            return legacy.modifier;
        }
        return BASE.computeIfAbsent(template, t -> {
            if (t instanceof CbEffectTemplateAccess vanilla) {
                return baseModifier(vanilla.cb$effect(), template);
            }
            return effect != null ? baseModifier(effect, template) : template.method_52522(0);
        });
    }

    /** a map value as 1.20.2 holds it: a template (one already is passes as it is) */
    private static Object toTemplate(class_1291 effect, Object v) {
        return v instanceof class_1322 modifier ? new CbLegacyEffectTemplate(effect, modifier) : v;
    }

    /** whether a map's values are of the kind (null: empty, no telling) */
    private static Boolean holdsTemplates(Map<?, ?> m) {
        for (Object v : m.values()) {
            if (v instanceof class_8630) {
                return Boolean.TRUE;
            }
            if (v instanceof class_1322) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    /**
     * getAttributeModifiers() as a converted 1.20.1 call reads it. 1.20.1 answered the effect's own
     * map, live and writable; this is a live view of the template map the call was answered with:
     * a template reads as the modifier 1.20.1 stored for it (the same object each time), and a
     * 1.20.1 modifier put in is kept as a template of the effect that computes 1.20.1's value for
     * it (CbLegacyEffectTemplate) -- 1.20.2 applying the effect reads that map. A map already in
     * 1.20.1's form (a converted override's, coming back) is answered as it is.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map legacyModifiers(Map templates) {
        if (templates == null) {
            return null;
        }
        if (templates instanceof DriftMapView view) {
            return view.modifiers ? view : view.backing;
        }
        if (Boolean.FALSE.equals(holdsTemplates(templates))) {
            return templates;
        }
        return new DriftMapView(templates, true, ownerOf(templates));
    }

    /**
     * What a converted 1.20.1 override of getAttributeModifiers() answers, as 1.20.2 reads it
     * (convertHooks, given the effect): a view whose templates are the override's modifiers of that
     * effect, each creating at an amplifier the modifier 1.20.1 applied (the effect's own
     * getAttributeModifierValue). 1.20.2 reads the map only for the potion tooltip -- applying an
     * effect reads its own map, as 1.20.1 did. A view of a 1.20.2 map comes back as that map.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map templates(class_1291 effect, Map modifiers) {
        if (modifiers == null) {
            return null;
        }
        if (modifiers instanceof DriftMapView view) {
            return view.modifiers ? view.backing : view;
        }
        if (Boolean.TRUE.equals(holdsTemplates(modifiers))) {
            return modifiers;
        }
        return new DriftMapView(modifiers, false, effect);
    }

    /**
     * A live view of an attribute map in the other release's form, read and written through:
     * {@code modifiers} -- 1.20.2 templates in the backing map, 1.20.1 modifiers in the view --
     * or the other way round.
     */
    private static final class DriftMapView extends AbstractMap<Object, Object> {
        final Map<Object, Object> backing;
        final boolean modifiers;
        final class_1291 effect;

        DriftMapView(Map<Object, Object> backing, boolean modifiers, class_1291 effect) {
            this.backing = backing;
            this.modifiers = modifiers;
            this.effect = effect;
        }

        Object out(Object v) {
            return this.modifiers ? toModifier(this.effect, v) : toTemplate(this.effect, v);
        }

        Object in(Object v) {
            return this.modifiers ? toTemplate(this.effect, v) : toModifier(this.effect, v);
        }

        @Override
        public int size() {
            return this.backing.size();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.backing.containsKey(key);
        }

        @Override
        public Object get(Object key) {
            return out(this.backing.get(key));
        }

        @Override
        public Object put(Object key, Object value) {
            return out(this.backing.put(key, in(value)));
        }

        @Override
        public Object remove(Object key) {
            return out(this.backing.remove(key));
        }

        @Override
        public void clear() {
            this.backing.clear();
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public int size() {
                    return DriftMapView.this.backing.size();
                }

                @Override
                public Iterator<Map.Entry<Object, Object>> iterator() {
                    Iterator<Map.Entry<Object, Object>> it = DriftMapView.this.backing.entrySet().iterator();
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Map.Entry<Object, Object> next() {
                            Map.Entry<Object, Object> e = it.next();
                            return new Map.Entry<>() {
                                @Override
                                public Object getKey() {
                                    return e.getKey();
                                }

                                @Override
                                public Object getValue() {
                                    return out(e.getValue());
                                }

                                @Override
                                public Object setValue(Object value) {
                                    return out(e.setValue(in(value)));
                                }

                                @Override
                                public boolean equals(Object o) {
                                    return o instanceof Map.Entry<?, ?> x && Objects.equals(getKey(), x.getKey())
                                        && Objects.equals(getValue(), x.getValue());
                                }

                                @Override
                                public int hashCode() {
                                    return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }
            };
        }
    }

    // ---- suspicious-stew holders

    /** 1.20.2 getSuspiciousEffects() of a converted 1.20.1 holder: its one effect */
    public static List<class_7917.class_8751> suspiciousEffects(CbLegacySuspiciousEffectHolder holder) {
        return List.of(new class_7917.class_8751(holder.method_10188(), holder.method_10187()));
    }

    /** SuspiciousEffectHolder.getSuspiciousEffect(): a 1.20.1 holder's own, else 1.20.2's first entry */
    public static class_1291 method_10188(class_7917 holder) {
        if (holder instanceof CbLegacySuspiciousEffectHolder legacy) {
            return legacy.method_10188();
        }
        List<class_7917.class_8751> entries = holder.method_53233();
        return entries.isEmpty() ? null : entries.get(0).comp_1838();
    }

    /** SuspiciousEffectHolder.getEffectDuration(): a 1.20.1 holder's own, else 1.20.2's first entry */
    public static int method_10187(class_7917 holder) {
        if (holder instanceof CbLegacySuspiciousEffectHolder legacy) {
            return legacy.method_10187();
        }
        List<class_7917.class_8751> entries = holder.method_53233();
        return entries.isEmpty() ? 0 : entries.get(0).comp_1839();
    }

    private CbEffects() {
    }
}
