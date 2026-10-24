package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import top.dext.centurybridge.step.v26_1.facade.LootItemConditionType;
import top.dext.centurybridge.step.v26_1.facade.LootItemFunctionType;
import top.dext.centurybridge.step.v26_1.facade.LootNbtProviderType;
import top.dext.centurybridge.step.v26_1.facade.LootNumberProviderType;
import top.dext.centurybridge.step.v26_1.facade.LootPoolEntryType;
import top.dext.centurybridge.step.v26_1.facade.LootScoreProviderType;

/**
 * The values of the registries whose value type 26.1 changed, read back by 1.21.11 code. 1.21.11's loot
 * function / condition / pool entry / number, nbt and score provider types were records around a MapCodec;
 * 26.1's registries (BuiltInRegistries.LOOT_FUNCTION_TYPE ...) hold the MapCodec itself. A mod registers its
 * type through CbWallRegistry (the codec goes in) and every value it reads back -- getValue, iteration, a
 * holder's value(), a lambda's argument -- reaches a cast to the 1.21.11 type: there a codec registered in
 * that registry becomes its type (the facade's canonical instance, so identity holds), as 1.21.11 handed out
 * the record it held. Anything else is left to the cast, which fails as 1.21.11's would have.
 */
public final class CbWallRegistryValues {

    private CbWallRegistryValues() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean registered(Registry registry, Object o) {
        return o instanceof MapCodec<?> c && registry.getKey(c) != null;
    }

    /** a cast to 1.21.11's LootItemFunctionType */
    public static Object castFunctionType(Object o) {
        return registered(BuiltInRegistries.LOOT_FUNCTION_TYPE, o) ? LootItemFunctionType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootItemFunctionType */
    public static boolean isFunctionType(Object o) {
        return o instanceof LootItemFunctionType<?> || registered(BuiltInRegistries.LOOT_FUNCTION_TYPE, o);
    }

    /** a cast to 1.21.11's LootItemConditionType */
    public static Object castConditionType(Object o) {
        return registered(BuiltInRegistries.LOOT_CONDITION_TYPE, o) ? LootItemConditionType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootItemConditionType */
    public static boolean isConditionType(Object o) {
        return o instanceof LootItemConditionType<?> || registered(BuiltInRegistries.LOOT_CONDITION_TYPE, o);
    }

    /** a cast to 1.21.11's LootPoolEntryType */
    public static Object castEntryType(Object o) {
        return registered(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, o) ? LootPoolEntryType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootPoolEntryType */
    public static boolean isEntryType(Object o) {
        return o instanceof LootPoolEntryType<?> || registered(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE, o);
    }

    /** a cast to 1.21.11's LootNumberProviderType */
    public static Object castNumberProviderType(Object o) {
        return registered(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, o) ? LootNumberProviderType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootNumberProviderType */
    public static boolean isNumberProviderType(Object o) {
        return o instanceof LootNumberProviderType<?> || registered(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, o);
    }

    /** a cast to 1.21.11's LootNbtProviderType */
    public static Object castNbtProviderType(Object o) {
        return registered(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, o) ? LootNbtProviderType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootNbtProviderType */
    public static boolean isNbtProviderType(Object o) {
        return o instanceof LootNbtProviderType<?> || registered(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE, o);
    }

    /** a cast to 1.21.11's LootScoreProviderType */
    public static Object castScoreProviderType(Object o) {
        return registered(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, o) ? LootScoreProviderType.cb$of((MapCodec<?>) o) : o;
    }

    /** instanceof 1.21.11's LootScoreProviderType */
    public static boolean isScoreProviderType(Object o) {
        return o instanceof LootScoreProviderType<?> || registered(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE, o);
    }

    /**
     * a cast to 1.21.11's RecipeSerializer (an interface there, 26.1's a final record): a 26.1 record read back
     * from BuiltInRegistries.RECIPE_SERIALIZER, a recipe's getSerializer() or a holder is the 1.21.11 serializer it
     * was registered for (CbWallRecipes, one each way)
     */
    public static Object castRecipeSerializer(Object o) {
        return o instanceof net.minecraft.world.item.crafting.RecipeSerializer<?> r ? CbWallRecipes.serializer(r) : o;
    }

    /** instanceof 1.21.11's RecipeSerializer */
    public static boolean isRecipeSerializer(Object o) {
        return o instanceof top.dext.centurybridge.step.v26_1.facade.RecipeSerializer<?>
            || o instanceof net.minecraft.world.item.crafting.RecipeSerializer<?>;
    }

    /**
     * a value read out of a loot context (LootContext.getParameter / getOptionalParameter, ContextMap's
     * getters): 26.1's TOOL is a ContextKey of ItemInstance and vanilla puts an ItemStackTemplate there where
     * it has no stack (Block.getDrops, /loot mine with a template), 1.21.11's was a key of ItemStack -- a
     * template is read as its stack. Every other value passes as it is (1.21.11 code never held a template).
     */
    public static Object contextValue(Object o) {
        return o instanceof net.minecraft.world.item.ItemStackTemplate t ? t.create() : o;
    }
}
