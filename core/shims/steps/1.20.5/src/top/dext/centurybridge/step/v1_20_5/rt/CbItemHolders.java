package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Values of the item area that 1.20.4 passed as themselves and 1.20.5 passes in another form --
 * attribute holders, registry keys, slot groups, map ids, registration info, lookup providers, byte
 * buffers -- converted at the call sites and override hooks the step's wiring reshapes (gen_s4item.py
 * emits most of them). Each pair converts both ways: the old form to the new one where a 1.20.4 call
 * hands a value to 1.20.5, the new form to the old one where 1.20.5 hands a value to 1.20.4 code.
 * Potions, effects, sounds, enchantments and instruments are the step's shared CbHolders.
 *
 * A registered attribute becomes its registry reference (Registry.wrapAsHolder answers the reference
 * of a registered value, a direct holder of an unregistered one), so the holder 1.20.5 sees is the one
 * its own code would have used; a holder becomes the value it holds, the object 1.20.4 handed out.
 */
public final class CbItemHolders {

    // ---------------------------------------------------------------- attributes (Attribute <-> Holder<Attribute>)

    public static net.minecraft.class_6880<net.minecraft.class_1320> attributeHolder(net.minecraft.class_1320 attribute) {
        return attribute == null ? null : net.minecraft.class_7923.field_41190.method_47983(attribute);
    }

    public static net.minecraft.class_1320 attribute(net.minecraft.class_6880<net.minecraft.class_1320> holder) {
        return holder == null ? null : holder.comp_349();
    }

    // ---------------------------------------------------------------- ids (ResourceLocation <-> ResourceKey)

    /** a loot table named by its location, as 1.20.5's key of the loot table registry */
    public static net.minecraft.class_5321<net.minecraft.class_52> lootTableKey(net.minecraft.class_2960 location) {
        return location == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_50079, location);
    }

    /** an item modifier (loot function) named by its location, as a key of the item modifier registry */
    public static net.minecraft.class_5321<net.minecraft.class_117> itemModifierKey(net.minecraft.class_2960 location) {
        return location == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_50080, location);
    }

    /** a predicate (loot condition) named by its location, as a key of the predicate registry */
    public static net.minecraft.class_5321<net.minecraft.class_5341> predicateKey(net.minecraft.class_2960 location) {
        return location == null ? null : net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_50081, location);
    }

    /** the location a key names: the id 1.20.4 used */
    public static net.minecraft.class_2960 location(net.minecraft.class_5321<?> key) {
        return key == null ? null : key.method_29177();
    }

    // ---------------------------------------------------------------- the rest

    /** a 1.20.4 equipment slot where 1.20.5 takes a slot group: the group of exactly that slot */
    public static net.minecraft.class_9274 slotGroup(net.minecraft.class_1304 slot) {
        return slot == null ? null : net.minecraft.class_9274.method_59524(slot);
    }

    /** a 1.20.4 map number (null: no map) as 1.20.5's map id */
    public static net.minecraft.class_9209 mapId(Integer id) {
        return id == null ? null : new net.minecraft.class_9209(id);
    }

    /** a registration lifecycle as 1.20.5's registration info, with no known pack (what 1.20.4 had) */
    public static net.minecraft.class_9248 registrationInfo(com.mojang.serialization.Lifecycle lifecycle) {
        return lifecycle == null ? null : new net.minecraft.class_9248(Optional.empty(), lifecycle);
    }

    public static com.mojang.serialization.Lifecycle lifecycle(net.minecraft.class_9248 info) {
        return info == null ? null : info.comp_2355();
    }

    /** a 1.20.4 stream of stacks where 1.20.5 takes an iterable: the same elements, consumed once */
    public static Iterable<net.minecraft.class_1799> iterable(Stream<net.minecraft.class_1799> stream) {
        return stream == null ? null : stream::iterator;
    }

    // ---------------------------------------------------------------- registries

    /**
     * A lookup provider 1.20.5 hands where 1.20.4 handed a RegistryAccess (recipe results and
     * assembly): every 1.20.5 caller passes a level's or the server's RegistryAccess, which goes on
     * unchanged; any other provider is answered with the running world's registries (CbRegistries).
     */
    public static net.minecraft.class_5455 access(net.minecraft.class_7225.class_7874 provider) {
        if (provider == null || provider instanceof net.minecraft.class_5455) {
            return (net.minecraft.class_5455) provider;
        }
        return CbRegistries.currentAccess();
    }

    /**
     * A 1.20.4 FriendlyByteBuf where 1.20.5 reads or writes a RegistryFriendlyByteBuf: one that is
     * already registry-aware goes on as it is, a plain one is read and written through a
     * registry-aware view of the same bytes carrying the running world's registries (CbRegistries).
     */
    public static net.minecraft.class_9129 registryBuf(net.minecraft.class_2540 buf) {
        if (buf == null || buf instanceof net.minecraft.class_9129) {
            return (net.minecraft.class_9129) buf;
        }
        return new net.minecraft.class_9129(buf, CbRegistries.currentAccess());
    }

    // ---------------------------------------------------------------- builtInRegistryHolder()

    /** the stand-ins for values not (yet) registered, one per value as 1.20.4's intrusive holder was */
    private static final java.util.Map<Object, net.minecraft.class_6880.class_6883<?>> UNREGISTERED =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /**
     * value.builtInRegistryHolder() of a registry 1.20.4 made intrusive and 1.20.5 does not (potions, mob
     * effects): the registry's reference once the value is registered, as 1.20.4's was bound by then;
     * before that, an unbound intrusive reference holding the value, the same one each time, as 1.20.4's
     * was (value() answers, key() throws "unbound").
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880.class_6883 builtIn(net.minecraft.class_2378 registry, Object value) {
        Optional key = registry.method_29113(value);
        if (key.isPresent()) {
            Optional ref = registry.method_40264((net.minecraft.class_5321) key.get());
            if (ref.isPresent()) {
                return (net.minecraft.class_6880.class_6883) ref.get();
            }
        }
        return UNREGISTERED.computeIfAbsent(value,
            v -> net.minecraft.class_6880.class_6883.method_40233(registry.method_46770(), v));
    }

    private CbItemHolders() {
    }
}
