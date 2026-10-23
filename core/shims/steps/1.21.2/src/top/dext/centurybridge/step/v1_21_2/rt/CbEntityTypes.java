package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_52;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityTypeAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityTypeBuilderAccess;

/**
 * 1.21.1's EntityType.Builder.build(String id) on 1.21.2, through {@link CbPendingIds}.
 *
 * <p>1.21.2's build(ResourceKey) bakes the type's descriptionId (the builder's descriptionId function, by
 * default "entity.&lt;ns&gt;.&lt;path&gt;" of the key) and its loot table (the builder's lootTable function, by
 * default Optional.of(entities/&lt;path&gt;) unless noLootTable) into final fields of the EntityType it
 * constructs. 1.21.1's build(String) took no key; the type derived both lazily from the registry:
 * getDescriptionId = Util.makeDescriptionId("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this)),
 * getDefaultLootTable = getKey(this).withPrefix("entities/"). Its id argument went only to the data fixer
 * schema lookup (Util.fetchChoiceType, the result discarded; a mod's id has no schema either way), which
 * 1.21.2's build makes with the key.
 *
 * <p>So the type is built under a placeholder key, remembered as pending with the builder's two functions,
 * and when it registers (MappedRegistry.register, CbPendingIdsRegistryMixin) both fields are remade from the
 * real key with those functions, and the cached description component is dropped so getDescription
 * re-derives it. Nothing else in 1.21.2's EntityType or Builder reads the key.
 */
public final class CbEntityTypes {

    /** the builder's functions a pending type was built with */
    private record Derive(class_10162<class_1299<?>, String> descriptionId,
                          class_10162<class_1299<?>, Optional<class_5321<class_52>>> lootTable) {
    }

    private static final Map<Object, Derive> TYPES = new IdentityHashMap<>();

    static {
        CbPendingIds.listen(class_7924.field_41266, CbEntityTypes::backfill);
    }

    /** 1.21.1 EntityType.Builder.build(id) */
    public static <T extends class_1297> class_1299<T> build(class_1299.class_1300<T> builder, String id) {
        class_5321<class_1299<?>> key = CbPendingIds.placeholder(class_7924.field_41266);
        class_1299<T> type = builder.method_5905(key);
        CbEntityTypeBuilderAccess b = (CbEntityTypeBuilderAccess) builder;
        synchronized (CbEntityTypes.class) {
            TYPES.put(type, new Derive(b.cb$descriptionIdFn(), b.cb$lootTableFn()));
        }
        CbPendingIds.pending(type, key);
        return type;
    }

    // ------------------------------------------------------------------------------------------ constructor

    /** the placeholder of the EntityType being constructed through 1.21.1's constructor on this thread */
    private static final ThreadLocal<class_5321<class_1299<?>>> CONSTRUCTING = new ThreadLocal<>();

    /**
     * 1.21.1's EntityType constructor had no descriptionId or loot table: the type derived them from the
     * registry as for build(String) (the class javadoc), with no builder to change the defaults. The reshaped
     * constructor call takes these two values, made from a fresh placeholder: Builder's defaults
     * (Util.makeDescriptionId("entity", id); entities/&lt;path&gt;), and {@link #constructed} marks the type pending.
     */
    public static String ctorDescriptionId() {
        class_5321<class_1299<?>> key = CbPendingIds.placeholder(class_7924.field_41266);
        CONSTRUCTING.set(key);
        return DEFAULT_DESCRIPTION.get(key);
    }

    public static Optional<class_5321<class_52>> ctorLootTable() {
        return DEFAULT_LOOT.get(CONSTRUCTING.get());
    }

    /** after the constructor: the type waits for its real key */
    public static void constructed(class_1299<?> type) {
        class_5321<class_1299<?>> key = CONSTRUCTING.get();
        CONSTRUCTING.remove();
        synchronized (CbEntityTypes.class) {
            TYPES.put(type, new Derive(DEFAULT_DESCRIPTION, DEFAULT_LOOT));
        }
        CbPendingIds.pending(type, key);
    }

    /**
     * A type something built under {@code key} (a placeholder of {@link CbPendingIds}) with EntityType.Builder's default
     * key functions -- Fabric API's FabricEntityTypeBuilder.build(key) makes a vanilla Builder of its own -- waits for its
     * real key like one of build(String).
     */
    public static void pendingWithDefaults(class_1299<?> type, class_5321<class_1299<?>> key) {
        synchronized (CbEntityTypes.class) {
            TYPES.put(type, new Derive(DEFAULT_DESCRIPTION, DEFAULT_LOOT));
        }
        CbPendingIds.pending(type, key);
    }

    /** a fresh placeholder key of the entity type registry */
    public static class_5321<class_1299<?>> placeholder() {
        return CbPendingIds.placeholder(class_7924.field_41266);
    }

    /** EntityType.Builder's defaults (its field initializers in 1.21.2) */
    private static final class_10162<class_1299<?>, String> DEFAULT_DESCRIPTION =
        k -> net.minecraft.class_156.method_646("entity", k.method_29177());

    private static final class_10162<class_1299<?>, Optional<class_5321<class_52>>> DEFAULT_LOOT =
        k -> Optional.of(class_5321.method_29179(class_7924.field_50079, k.method_29177().method_45138("entities/")));

    @SuppressWarnings("unchecked")
    private static void backfill(Object value, class_5321<?> realKey) {
        Derive d;
        synchronized (CbEntityTypes.class) {
            d = TYPES.remove(value);
        }
        if (d == null || !(value instanceof class_1299<?> type)) {
            return;
        }
        class_5321<class_1299<?>> key = (class_5321<class_1299<?>>) realKey;
        CbEntityTypeAccess f = (CbEntityTypeAccess) type;
        f.cb$setDescriptionId(d.descriptionId().get(key));
        f.cb$setLootTable(d.lootTable().get(key));
        f.cb$setDescription(null);
    }

    private CbEntityTypes() {
    }
}
