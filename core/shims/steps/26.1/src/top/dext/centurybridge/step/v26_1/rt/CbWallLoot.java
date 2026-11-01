package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import top.dext.centurybridge.step.v26_1.facade.LootDataType$Validator;
import top.dext.centurybridge.step.v26_1.mixin.CbWallValidationContextAccess;

/**
 * 1.21.11's LootDataType(registryKey, codec, Validator) on 26.1's LootDataType(registryKey, codec, ContextGetter).
 *
 * <p>1.21.11's two vanilla validators (createSimpleValidator, shared by PREDICATE and MODIFIER, and
 * createLootTableValidator) are {@link #SIMPLE} and {@link #TABLE}, the instances validator() answers for vanilla's
 * types; a type built with one of them is built with vanilla's own 26.1 getter and nothing is recorded. Any other
 * validator makes a type with ContextGetter.constant(ALL_PARAMS) (the key set 1.21.11's reload validated with) and is
 * recorded against that type instance: 1.21.11 code calling runValidation runs it, and 26.1's runValidation runs it
 * for that type (mixin/CbWallLootDataTypeHooks). Vanilla's reload validates only its three types, on 1.21.11 as on
 * 26.1, so a recorded type is validated where code asks for it.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallLoot {

    private CbWallLoot() {
    }

    /** 1.21.11 createSimpleValidator(): the value validates under the caller's context, entered at its key */
    public static final LootDataType$Validator<Object> SIMPLE = (context, key, value) ->
        ((Validatable) value).validate(context.enterElement(new ProblemReporter.RootElementPathElement(key), key));

    /** 1.21.11 createLootTableValidator(): the table validates under its own parameter set, entered at its key */
    public static final LootDataType$Validator<Object> TABLE = (context, key, value) -> {
        LootTable table = (LootTable) value;
        table.validate(withKeys(context, table.getParamSet()).enterElement(new ProblemReporter.RootElementPathElement(key), key));
    };

    /** validators of types built by 1.21.11 code with a validator of its own */
    private static final Map<LootDataType<?>, LootDataType$Validator<?>> RECORDED = Collections.synchronizedMap(new IdentityHashMap<>());

    /** validator() of the types 26.1 code built (a getter other than vanilla's two): one adapter per type */
    private static final Map<LootDataType<?>, LootDataType$Validator<?>> ADAPTERS = Collections.synchronizedMap(new IdentityHashMap<>());

    /** the getter an adapter of {@link #ADAPTERS} stands for, so a type built with it takes that getter back */
    private static final Map<LootDataType$Validator<?>, LootDataType.ContextGetter<?>> ADAPTED = Collections.synchronizedMap(new IdentityHashMap<>());

    /** 1.21.11 new LootDataType(registryKey, codec, validator) */
    public static LootDataType lootDataType(ResourceKey<? extends Registry> registryKey, Codec codec, LootDataType$Validator validator) {
        if (validator == SIMPLE) {
            return new LootDataType(registryKey, codec, LootDataType.PREDICATE.contextGetter());
        }
        if (validator == TABLE) {
            return new LootDataType(registryKey, codec, LootDataType.TABLE.contextGetter());
        }
        LootDataType.ContextGetter adapted = validator == null ? null : ADAPTED.get(validator);
        if (adapted != null) {
            return new LootDataType(registryKey, codec, adapted);
        }
        LootDataType type = new LootDataType(registryKey, codec, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
        RECORDED.put(type, validator);
        return type;
    }

    /** 1.21.11 LootDataType.validator() */
    public static LootDataType$Validator validator(LootDataType type) {
        LootDataType$Validator recorded = RECORDED.get(type);
        if (recorded != null) {
            return recorded;
        }
        LootDataType.ContextGetter getter = type.contextGetter();
        if (getter == LootDataType.PREDICATE.contextGetter() || getter == LootDataType.MODIFIER.contextGetter()) {
            return SIMPLE;
        }
        if (getter == LootDataType.TABLE.contextGetter()) {
            return TABLE;
        }
        synchronized (ADAPTERS) {
            LootDataType$Validator a = ADAPTERS.get(type);
            if (a == null) {
                a = (context, key, value) -> ((Validatable) value).validate(
                    withKeys(context, getter.context(value)).enterElement(new ProblemReporter.RootElementPathElement(key), key));
                ADAPTERS.put(type, a);
                ADAPTED.put(a, getter);
            }
            return a;
        }
    }

    /** 1.21.11 LootDataType.runValidation(context, key, value): validator().run(context, key, value) */
    public static void runValidation(LootDataType type, ValidationContext context, ResourceKey key, Object value) {
        validator(type).run(context, key, value);
    }

    /**
     * 26.1 runValidation(source, key, value) of a recorded type (mixin/CbWallLootDataTypeHooks): its validator, under
     * the context 26.1 builds for the type (source.context(contextGetter.context(value)), here ALL_PARAMS as 1.21.11's
     * reload had it); false for any other type, which 26.1 validates itself.
     */
    public static boolean runRecorded(LootDataType type, ValidationContextSource source, ResourceKey key, Object value) {
        LootDataType$Validator validator = RECORDED.get(type);
        if (validator == null) {
            return false;
        }
        validator.run(source.context(type.contextGetter().context(value)), key, value);
        return true;
    }

    /** 26.1 runValidation(source, lookup) of a recorded type: {@link #runRecorded} for each element */
    public static boolean runRecordedAll(LootDataType type, ValidationContextSource source, HolderLookup lookup) {
        if (!RECORDED.containsKey(type)) {
            return false;
        }
        lookup.listElements().forEach(h -> {
            Holder.Reference ref = (Holder.Reference) h;
            runRecorded(type, source, ref.key(), ref.value());
        });
        return true;
    }

    /** 1.21.11 ValidationContext.setContextKeySet(keys): the same context under another key set */
    static ValidationContext withKeys(ValidationContext context, ContextKeySet keys) {
        CbWallValidationContextAccess access = (CbWallValidationContextAccess) (Object) context;
        return CbWallValidationContextAccess.cb$new(context.reporter(), keys, access.cb$resolver(), access.cb$visitedElements());
    }
}
