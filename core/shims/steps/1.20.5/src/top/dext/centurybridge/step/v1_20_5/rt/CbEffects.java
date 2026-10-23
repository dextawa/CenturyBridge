package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import top.dext.centurybridge.step.v1_20_5.facade.AttributeModifierTemplate;
import top.dext.centurybridge.step.v1_20_5.facade.FactorData;

/**
 * 1.20.4's mob effect API on 1.20.5's.
 * <ul>
 * <li>Factor data (darkness' fade). 1.20.4 gave an effect a FactorData factory and every instance of it
 *     one FactorData, ticked with the instance and asked for the fade; 1.20.5 keeps a private blend state
 *     per instance driven by the effect's blend duration. An effect a 1.20.4 mod gave a factory keeps it
 *     (CbMobEffectMixin) and its instances a FactorData of their own again (CbEffectInstanceMixin):
 *     made as 1.20.4 made it, ticked where 1.20.4 ticked it, answering 1.20.5's blend factor, saved and
 *     loaded under 1.20.4's key. A vanilla effect that blends (darkness) answers getFactorData() with a
 *     view of its 1.20.5 blend state.</li>
 * <li>Attribute templates. 1.20.4's getAttributeModifiers() was the live map of attribute to template; it
 *     is that map again over 1.20.5's (vanilla templates seen as 1.20.4's interface), and a template of a
 *     mod's own kind put into it is kept beside 1.20.5's and applied, removed and listed with them.</li>
 * <li>applyEffectTick returned nothing in 1.20.4 and returns whether the effect stays in 1.20.5: a 1.20.4
 *     override keeps it, unless the vanilla method its super call reached answered that it goes.</li>
 * <li>MobEffectInstance.save(tag) wrote 1.20.4's NBT; builtInRegistryHolder() is the registry's reference.</li>
 * </ul>
 */
public final class CbEffects {

    // ---------------------------------------------------------------- adapters (1.20.4 shapes a mod class declares)

    /** applyEffectTick(entity, amplifier), 1.20.4's (no answer) */
    public interface LegacyTick {
        void method_5572(net.minecraft.class_1309 entity, int amplifier);
    }

    /** createFactorData() */
    public interface CreatesFactorData {
        @SuppressWarnings("rawtypes")
        Optional method_42127();
    }

    /** setFactorDataFactory(factory) */
    public interface SetsFactorData {
        @SuppressWarnings("rawtypes")
        net.minecraft.class_1291 method_42126(Supplier factory);
    }

    /** getAttributeModifiers() */
    public interface ListsModifiers {
        @SuppressWarnings("rawtypes")
        Map method_5565();
    }

    /** MobEffect.builtInRegistryHolder() */
    public interface BuiltIn {
        @SuppressWarnings("rawtypes")
        net.minecraft.class_6880.class_6883 method_53123();
    }

    /** MobEffectInstance.getFactorData() */
    public interface GetsFactorData {
        @SuppressWarnings("rawtypes")
        Optional method_42129();
    }

    /** MobEffectInstance.save(tag) */
    public interface Saves {
        net.minecraft.class_2487 method_5582(net.minecraft.class_2487 tag);
    }

    // ---------------------------------------------------------------- slots (mixins)

    /** MobEffect (CbMobEffectMixin): its 1.20.4 factor data factory, its templates of a mod's own kind, 1.20.5's templates */
    public interface EffectSlot {
        @SuppressWarnings("rawtypes")
        Supplier cbS4$factorDataFactory();

        @SuppressWarnings("rawtypes")
        void cbS4$factorDataFactory(Supplier factory);

        /** attribute -> 1.20.4 template a mod put in that is not one of 1.20.5's */
        Map<net.minecraft.class_1320, AttributeModifierTemplate> cbS4$legacyTemplates();

        /** 1.20.5's own map, Holder&lt;Attribute&gt; -> MobEffect.AttributeTemplate */
        @SuppressWarnings("rawtypes")
        Map cbS4$templates();
    }

    /** MobEffectInstance (CbEffectInstanceMixin): its 1.20.4 factor data, if its effect makes one */
    public interface InstanceSlot {
        FactorData cbS4$factorData();

        void cbS4$factorData(FactorData data);
    }

    private static EffectSlot slot(net.minecraft.class_1291 effect) {
        return (EffectSlot) (Object) effect;
    }

    private static InstanceSlot slot(net.minecraft.class_1293 instance) {
        return (InstanceSlot) (Object) instance;
    }

    // ---------------------------------------------------------------- factor data: the effect

    /** effect.setFactorDataFactory(factory): the class's own override, else 1.20.4's (keeps the factory) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_1291 setFactorDataFactory(net.minecraft.class_1291 effect, Supplier factory) {
        return effect instanceof SetsFactorData s ? s.method_42126(factory) : superSetFactorDataFactory(effect, factory);
    }

    /** MobEffect.setFactorDataFactory's 1.20.4 body */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_1291 superSetFactorDataFactory(net.minecraft.class_1291 effect, Supplier factory) {
        slot(effect).cbS4$factorDataFactory(factory);
        return effect;
    }

    /** effect.createFactorData(): the class's own override, else 1.20.4's (the factory's, if any) */
    @SuppressWarnings("rawtypes")
    public static Optional createFactorData(net.minecraft.class_1291 effect) {
        return effect instanceof CreatesFactorData c ? c.method_42127() : superCreateFactorData(effect);
    }

    /** MobEffect.createFactorData's 1.20.4 body */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional superCreateFactorData(net.minecraft.class_1291 effect) {
        Supplier factory = slot(effect).cbS4$factorDataFactory();
        return factory == null ? Optional.empty() : Optional.ofNullable(factory.get());
    }

    // ---------------------------------------------------------------- factor data: the instance

    /** the factor data a 1.20.4 constructor was handed explicitly, until MobEffectInstance's constructor takes it */
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<Optional> PENDING = new ThreadLocal<>();

    /** new MobEffectInstance(effect, ..., hiddenEffect, factorData): the explicit factor data, for the instance to take */
    @SuppressWarnings("rawtypes")
    public static void pendFactorData(Optional factorData) {
        PENDING.set(factorData == null ? Optional.empty() : factorData);
    }

    /**
     * CbEffectInstanceMixin, a MobEffectInstance made (every constructor but the copy one reaches the one
     * this hooks): the factor data a converted constructor named, else -- 1.20.4's constructors -- the
     * effect's own (createFactorData).
     */
    @SuppressWarnings("rawtypes")
    public static void made(net.minecraft.class_1293 instance) {
        Optional explicit = PENDING.get();
        if (explicit != null) {
            PENDING.remove();
            slot(instance).cbS4$factorData((FactorData) explicit.orElse(null));
        } else if (slot(instance).cbS4$factorData() == null) {
            slot(instance).cbS4$factorData((FactorData) createFactorData(instance.method_5579().comp_349()).orElse(null));
        }
    }

    /** CbEffectInstanceMixin, the copy constructor: 1.20.4 gave the copy fresh factor data from the effect */
    public static void copied(net.minecraft.class_1293 instance) {
        slot(instance).cbS4$factorData((FactorData) createFactorData(instance.method_5579().comp_349()).orElse(null));
    }

    /** CbEffectInstanceMixin, MobEffectInstance.tick: the factor data ticks with the instance, as in 1.20.4 */
    public static void ticked(net.minecraft.class_1293 instance) {
        FactorData d = slot(instance).cbS4$factorData();
        if (d != null) {
            d.method_42136(instance);
        }
    }

    /**
     * CbEffectInstanceMixin, MobEffectInstance.getBlendFactor: the 1.20.4 factor data's fade -- a class's own
     * getFactorData() override's, else the instance's own -- or null for 1.20.5's blend state.
     */
    @SuppressWarnings("rawtypes")
    public static Float blendFactor(net.minecraft.class_1293 instance, net.minecraft.class_1309 entity, float partialTick) {
        if (!Guard.cbEnter(instance)) {
            return null; // asked through a view of 1.20.5's own blend state: 1.20.5 answers
        }
        try {
            FactorData d;
            if (instance instanceof GetsFactorData g) {
                Optional o = g.method_42129();
                d = o == null ? null : (FactorData) o.orElse(null);
            } else {
                d = slot(instance).cbS4$factorData();
            }
            return d == null ? null : d.method_42134(entity, partialTick);
        } finally {
            Guard.cbExit(instance);
        }
    }

    /** CbEffectInstanceMixin, skipBlending (a client told the effect is already in full): the factor data at rest */
    public static void skippedBlending(net.minecraft.class_1293 instance) {
        FactorData d = slot(instance).cbS4$factorData();
        if (d != null) {
            boolean has = !instance.method_48557(d.field_38086);
            float f = has ? 1.0F : 0.0F;
            d.field_38091 = has;
            d.field_42107 = d.field_38086;
            d.field_39111 = f;
            d.field_38087 = f;
            d.field_38088 = f;
            d.field_38090 = f;
        }
    }

    /** CbEffectInstanceMixin, copyBlendState(other): the other instance's factor data state carries over */
    public static void copiedBlendState(net.minecraft.class_1293 instance, net.minecraft.class_1293 other) {
        FactorData d = slot(instance).cbS4$factorData();
        FactorData o = slot(other).cbS4$factorData();
        if (d != null && o != null) {
            d.field_39111 = o.field_39111;
            d.field_38087 = o.field_38087;
            d.field_38088 = o.field_38088;
            d.field_42107 = o.field_42107;
            d.field_38090 = o.field_38090;
            d.field_38091 = o.field_38091;
        }
    }

    /** instance.getFactorData(): the class's own override, else 1.20.4's */
    @SuppressWarnings("rawtypes")
    public static Optional getFactorData(net.minecraft.class_1293 instance) {
        return instance instanceof GetsFactorData g ? g.method_42129() : superGetFactorData(instance);
    }

    /**
     * MobEffectInstance.getFactorData()'s answer: the instance's own factor data; for an effect 1.20.5 blends
     * itself (vanilla's darkness, which 1.20.4 gave factor data), a view of that blend; else none.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional superGetFactorData(net.minecraft.class_1293 instance) {
        FactorData d = slot(instance).cbS4$factorData();
        if (d != null) {
            return Optional.of(d);
        }
        int blend = instance.method_5579().comp_349().method_55652();
        return blend > 0 ? Optional.of(new BlendView(instance, blend)) : Optional.empty();
    }

    /**
     * An instance's 1.20.5 blend state seen as 1.20.4's FactorData: its fade is the instance's blend factor.
     * 1.20.5 ticks the state itself; a tick through the view does nothing more (1.20.4's own ticks were
     * vanilla's to make too).
     */
    public static final class BlendView extends FactorData {
        private final net.minecraft.class_1293 instance;

        BlendView(net.minecraft.class_1293 instance, int blendDuration) {
            super(blendDuration);
            this.instance = instance;
        }

        @Override
        public void method_42136(net.minecraft.class_1293 effect) {
        }

        @Override
        public float method_42134(net.minecraft.class_1309 entity, float partialTick) {
            return instance.method_55653(entity, partialTick);
        }
    }

    // ---------------------------------------------------------------- MobEffectInstance NBT

    private static final String FACTOR_DATA = "factor_calculation_data";

    /** instance.save(tag): the class's own override, else 1.20.4's NBT */
    public static net.minecraft.class_2487 save(net.minecraft.class_1293 instance, net.minecraft.class_2487 tag) {
        return instance instanceof Saves s ? s.method_5582(tag) : superSave(instance, tag);
    }

    /** MobEffectInstance.save(tag)'s 1.20.4 body: id, amplifier, duration, flags, the hidden effect, the factor data */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2487 superSave(net.minecraft.class_1293 instance, net.minecraft.class_2487 tag) {
        net.minecraft.class_2960 id = net.minecraft.class_7923.field_41174.method_10221(instance.method_5579().comp_349());
        tag.method_10582("id", String.valueOf(id));
        tag.method_10567("amplifier", (byte) instance.method_5578());
        tag.method_10569("duration", instance.method_5584());
        tag.method_10556("ambient", instance.method_5591());
        tag.method_10556("show_particles", instance.method_5581());
        tag.method_10556("show_icon", instance.method_5592());
        net.minecraft.class_1293 hidden = ((HiddenField) (Object) instance).cbS4$hidden();
        if (hidden != null) {
            tag.method_10566("hidden_effect", save(hidden, new net.minecraft.class_2487()));
        }
        FactorData d = slot(instance).cbS4$factorData();
        if (d != null) {
            FactorData.field_38085.encodeStart(net.minecraft.class_2509.field_11560, d).result()
                .ifPresent(t -> tag.method_10566(FACTOR_DATA, (net.minecraft.class_2520) t));
        }
        return tag;
    }

    /** MobEffectInstance's hidden effect field (CbEffectInstanceMixin) */
    public interface HiddenField {
        net.minecraft.class_1293 cbS4$hidden();
    }

    /** CbEffectInstanceMixin, 1.20.5's save(): the factor data under 1.20.4's key beside what 1.20.5 writes */
    public static net.minecraft.class_2520 saved(net.minecraft.class_1293 instance, net.minecraft.class_2520 tag) {
        FactorData d = slot(instance).cbS4$factorData();
        if (d != null && tag instanceof net.minecraft.class_2487 c) {
            FactorData.field_38085.encodeStart(net.minecraft.class_2509.field_11560, d).result()
                .ifPresent(t -> c.method_10566(FACTOR_DATA, t));
        }
        return tag;
    }

    /** CbEffectInstanceMixin, MobEffectInstance.load(tag): saved factor data comes back, as 1.20.4 loaded it */
    public static void loaded(net.minecraft.class_1293 instance, net.minecraft.class_2487 tag) {
        if (instance != null && tag.method_10573(FACTOR_DATA, 10)) {
            FactorData.field_38085.parse(net.minecraft.class_2509.field_11560, tag.method_10562(FACTOR_DATA)).result()
                .ifPresent(d -> slot(instance).cbS4$factorData(d));
        }
    }

    // ---------------------------------------------------------------- applyEffectTick

    /** each 1.20.4 tick on its way: {effect, entity, whether it stays} */
    private static final ThreadLocal<ArrayDeque<Object[]>> TICKS = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 1.20.5's applyEffectTick(entity, amplifier) of a class declaring 1.20.4's (override hook bridge): the
     * old method runs; the effect stays, as it did in 1.20.4, unless the vanilla method its super call
     * reached answered that it goes ({@link #answered}).
     */
    public static boolean applyEffectTick(LegacyTick effect, net.minecraft.class_1309 entity, int amplifier) {
        Object[] frame = {effect, entity, Boolean.TRUE};
        ArrayDeque<Object[]> frames = TICKS.get();
        frames.push(frame);
        try {
            effect.method_5572(entity, amplifier);
        } finally {
            frames.pop();
        }
        return (Boolean) frame[2];
    }

    /**
     * CbEffectTickMixin, a vanilla applyEffectTick that can answer false returning: when it ran as the super
     * call of a 1.20.4 tick on the same effect and entity, its answer is that tick's.
     */
    public static void answered(net.minecraft.class_1291 effect, net.minecraft.class_1309 entity, boolean stays) {
        if (!stays) {
            for (Object[] f : TICKS.get()) {
                if (f[0] == effect && f[1] == entity) {
                    f[2] = Boolean.FALSE;
                    return;
                }
            }
        }
    }

    // ---------------------------------------------------------------- attribute templates

    /** effect.getAttributeModifiers(): the class's own override, else 1.20.4's live map */
    @SuppressWarnings("rawtypes")
    public static Map attributeModifiers(net.minecraft.class_1291 effect) {
        return effect instanceof ListsModifiers l ? l.method_5565() : superAttributeModifiers(effect);
    }

    /** MobEffect.getAttributeModifiers(): the effect's templates by attribute, live (see {@link Templates}) */
    @SuppressWarnings("rawtypes")
    public static Map superAttributeModifiers(net.minecraft.class_1291 effect) {
        return new Templates(effect);
    }

    /** 1.20.5's MobEffect.AttributeTemplate (a package-private record), through CbAttributeTemplateMixin */
    public interface TemplateView {
        java.util.UUID cbS4$id();

        net.minecraft.class_1322 cbS4$create(String name, int amplifier);
    }

    /** one of 1.20.5's templates as 1.20.4's: its id, and its modifier for an amplifier as the effect makes it */
    public static final class VanillaTemplate implements AttributeModifierTemplate {
        final net.minecraft.class_1291 effect;
        final Object template;

        VanillaTemplate(net.minecraft.class_1291 effect, Object template) {
            this.effect = effect;
            this.template = template;
        }

        @Override
        public java.util.UUID method_52521() {
            return ((TemplateView) template).cbS4$id();
        }

        @Override
        public net.minecraft.class_1322 method_52522(int amplifier) {
            return ((TemplateView) template).cbS4$create(effect.method_5567(), amplifier);
        }
    }

    /**
     * 1.20.4's MobEffect.getAttributeModifiers() map: attribute -> template, over 1.20.5's map (holder ->
     * record) and the templates of a mod's own kind kept beside it. A put of one of 1.20.5's templates goes
     * into 1.20.5's map; a put of any other kind is kept beside it and applied with it (CbMobEffectMixin).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Templates extends AbstractMap<net.minecraft.class_1320, AttributeModifierTemplate> {
        private final net.minecraft.class_1291 effect;

        Templates(net.minecraft.class_1291 effect) {
            this.effect = effect;
        }

        private Map real() {
            return slot(effect).cbS4$templates();
        }

        private Map<net.minecraft.class_1320, AttributeModifierTemplate> legacy() {
            return slot(effect).cbS4$legacyTemplates();
        }

        @Override
        public AttributeModifierTemplate get(Object key) {
            if (!(key instanceof net.minecraft.class_1320 a)) {
                return null;
            }
            AttributeModifierTemplate l = legacy().get(a);
            if (l != null) {
                return l;
            }
            Object t = real().get(CbItemHolders.attributeHolder(a));
            return t == null ? null : new VanillaTemplate(effect, t);
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public AttributeModifierTemplate put(net.minecraft.class_1320 attribute, AttributeModifierTemplate template) {
            AttributeModifierTemplate old = get(attribute);
            Object holder = CbItemHolders.attributeHolder(attribute);
            if (template instanceof VanillaTemplate v && v.effect == effect) {
                legacy().remove(attribute);
                real().put(holder, v.template);
            } else {
                real().remove(holder);
                legacy().put(attribute, template);
            }
            return old;
        }

        @Override
        public AttributeModifierTemplate remove(Object key) {
            AttributeModifierTemplate old = get(key);
            if (key instanceof net.minecraft.class_1320 a) {
                legacy().remove(a);
                real().remove(CbItemHolders.attributeHolder(a));
            }
            return old;
        }

        @Override
        public Set<Entry<net.minecraft.class_1320, AttributeModifierTemplate>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public Iterator<Entry<net.minecraft.class_1320, AttributeModifierTemplate>> iterator() {
                    List<Entry<net.minecraft.class_1320, AttributeModifierTemplate>> all = new ArrayList<>();
                    for (Object o : real().entrySet()) {
                        Map.Entry e = (Map.Entry) o;
                        all.add(new SimpleImmutableEntry<>((net.minecraft.class_1320) ((net.minecraft.class_6880) e.getKey()).comp_349(),
                            new VanillaTemplate(effect, e.getValue())));
                    }
                    all.addAll(new ArrayList<>(legacy().entrySet()));
                    Iterator<Entry<net.minecraft.class_1320, AttributeModifierTemplate>> it = all.iterator();
                    return new Iterator<>() {
                        Entry<net.minecraft.class_1320, AttributeModifierTemplate> last;

                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Entry<net.minecraft.class_1320, AttributeModifierTemplate> next() {
                            return last = it.next();
                        }

                        @Override
                        public void remove() {
                            if (last == null) {
                                throw new IllegalStateException();
                            }
                            Templates.this.remove(last.getKey());
                            last = null;
                        }
                    };
                }

                @Override
                public int size() {
                    return real().size() + legacy().size();
                }
            };
        }

        @Override
        public int size() {
            return real().size() + legacy().size();
        }
    }

    /**
     * CbMobEffectMixin, createModifiers(amplifier, out) of a class overriding 1.20.4's getAttributeModifiers():
     * 1.20.4 listed an effect's modifiers (its tooltip) from that method, so its map answers. False for a
     * class that does not override it.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean listedOwn(net.minecraft.class_1291 effect, int amplifier, BiConsumer out) {
        if (!(effect instanceof ListsModifiers l)) {
            return false;
        }
        Map map = l.method_5565();
        if (map != null) {
            for (Object o : map.entrySet()) {
                Map.Entry e = (Map.Entry) o;
                out.accept(CbItemHolders.attributeHolder((net.minecraft.class_1320) e.getKey()),
                    ((AttributeModifierTemplate) e.getValue()).method_52522(amplifier));
            }
        }
        return true;
    }

    /** CbMobEffectMixin, createModifiers(amplifier, out) returning: a mod's own templates' modifiers too */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void listedLegacy(net.minecraft.class_1291 effect, int amplifier, BiConsumer out) {
        for (Map.Entry<net.minecraft.class_1320, AttributeModifierTemplate> e : slot(effect).cbS4$legacyTemplates().entrySet()) {
            out.accept(CbItemHolders.attributeHolder(e.getKey()), e.getValue().method_52522(amplifier));
        }
    }

    /** CbMobEffectMixin, addAttributeModifiers(map, amplifier) returning: 1.20.4's body for a mod's own templates */
    public static void addedLegacy(net.minecraft.class_1291 effect, net.minecraft.class_5131 attributes, int amplifier) {
        for (Map.Entry<net.minecraft.class_1320, AttributeModifierTemplate> e : slot(effect).cbS4$legacyTemplates().entrySet()) {
            net.minecraft.class_1324 instance = attributes.method_45329(CbItemHolders.attributeHolder(e.getKey()));
            if (instance != null) {
                instance.method_6200(e.getValue().method_52521());
                instance.method_26837(e.getValue().method_52522(amplifier));
            }
        }
    }

    /** CbMobEffectMixin, removeAttributeModifiers(map) returning: 1.20.4's body for a mod's own templates */
    public static void removedLegacy(net.minecraft.class_1291 effect, net.minecraft.class_5131 attributes) {
        for (Map.Entry<net.minecraft.class_1320, AttributeModifierTemplate> e : slot(effect).cbS4$legacyTemplates().entrySet()) {
            net.minecraft.class_1324 instance = attributes.method_45329(CbItemHolders.attributeHolder(e.getKey()));
            if (instance != null) {
                instance.method_6200(e.getValue().method_52521());
            }
        }
    }

    /** a fresh map for an effect's own templates of a mod's kind (CbMobEffectMixin's field) */
    public static Map<net.minecraft.class_1320, AttributeModifierTemplate> newLegacyTemplates() {
        return new LinkedHashMap<>();
    }

    // ---------------------------------------------------------------- builtInRegistryHolder

    /** effect.builtInRegistryHolder(): the class's own override, else the registry's reference */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_6880.class_6883 builtInRegistryHolder(net.minecraft.class_1291 effect) {
        return effect instanceof BuiltIn b ? b.method_53123() : superBuiltInRegistryHolder(effect);
    }

    /** MobEffect.builtInRegistryHolder()'s answer, past a class's own override */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880.class_6883 superBuiltInRegistryHolder(net.minecraft.class_1291 effect) {
        return CbItemHolders.builtIn(net.minecraft.class_7923.field_41174, effect);
    }

    private CbEffects() {
    }
}
