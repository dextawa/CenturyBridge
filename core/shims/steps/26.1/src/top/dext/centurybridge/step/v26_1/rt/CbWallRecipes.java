package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import top.dext.centurybridge.step.v26_1.facade.RecipeSerializer;

/**
 * 1.21.11 RecipeSerializers (an interface) against 26.1's (a record of the same codecs): each 1.21.11
 * serializer has one 26.1 record and each 26.1 record one 1.21.11 serializer -- a mod's own serializer and
 * the record made from it map to each other, a 26.1 serializer is given back through one wrapper -- so
 * getSerializer() == SERIALIZER keeps holding across the boundary.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbWallRecipes {

    private static final Map<RecipeSerializer<?>, net.minecraft.world.item.crafting.RecipeSerializer<?>> RECORDS = new IdentityHashMap<>();
    private static final Map<net.minecraft.world.item.crafting.RecipeSerializer<?>, RecipeSerializer<?>> SERIALIZERS = new IdentityHashMap<>();

    private CbWallRecipes() {
    }

    /** a 26.1 serializer seen as a 1.21.11 one */
    private record Vanilla(net.minecraft.world.item.crafting.RecipeSerializer record) implements RecipeSerializer {
        @Override
        public MapCodec method_53736() {
            return this.record.codec();
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ?> method_56104() {
            return this.record.streamCodec();
        }
    }

    /** the 26.1 record of a 1.21.11 serializer */
    public static synchronized net.minecraft.world.item.crafting.RecipeSerializer<?> record(RecipeSerializer<?> serializer) {
        if (serializer == null) {
            return null;
        }
        if (serializer instanceof Vanilla v) {
            return v.record();
        }
        net.minecraft.world.item.crafting.RecipeSerializer<?> r = RECORDS.get(serializer);
        if (r == null) {
            r = new net.minecraft.world.item.crafting.RecipeSerializer(serializer.method_53736(), serializer.method_56104());
            RECORDS.put(serializer, r);
            SERIALIZERS.put(r, serializer);
        }
        return r;
    }

    /** the 1.21.11 serializer of a 26.1 record */
    public static synchronized RecipeSerializer<?> serializer(net.minecraft.world.item.crafting.RecipeSerializer<?> record) {
        if (record == null) {
            return null;
        }
        return SERIALIZERS.computeIfAbsent(record, Vanilla::new);
    }

    /** a 1.21.11 constant: the serializer 26.1 registers under the id */
    public static RecipeSerializer<?> vanilla(String id) {
        net.minecraft.world.item.crafting.RecipeSerializer<?> r = BuiltInRegistries.RECIPE_SERIALIZER.getValue(Identifier.withDefaultNamespace(id));
        if (r == null) {
            throw new IllegalStateException("26.1 registers no recipe serializer '" + id + "'");
        }
        return serializer(r);
    }

    /** 1.21.11 RecipeSerializer.register(String, S) */
    public static <S extends RecipeSerializer<?>> S register(String id, S serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, record(serializer));
        return serializer;
    }

    /** A 1.21.11 Recipe implementation: its own assemble(input, registries) */
    public interface LegacyAssemble {
        net.minecraft.world.item.ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider registries);
    }

    /**
     * 26.1's assemble(input) of a 1.21.11 recipe: its own assemble with the registries 1.21.11's callers passed
     * (the level's registry access: the running server's, else the client level's -- the same registries)
     */
    public static net.minecraft.world.item.ItemStack assemble(LegacyAssemble recipe, net.minecraft.world.item.crafting.RecipeInput input) {
        return recipe.assemble(input, CbWallRegistries.access());
    }

    /** showNotification() of a 1.21.11 recipe not overriding it: 1.21.11's default, true */
    public static boolean showNotification(Recipe<?> recipe) {
        return true;
    }

    /** group() of a 1.21.11 recipe not overriding it: 1.21.11's default, the empty group */
    public static String group(Recipe<?> recipe) {
        return "";
    }

    /** 1.21.11 Recipe.getSerializer() */
    public static RecipeSerializer<?> getSerializer(Recipe<?> recipe) {
        return serializer(recipe.getSerializer());
    }

    /** 26.1's CustomRecipe keeping 1.21.11's constructor category (mixin/CbWallCustomRecipeMixin) */
    public interface CustomCategory {
        void cb$setCategory(net.minecraft.world.item.crafting.CraftingBookCategory category);

        /** the category a 1.21.11 constructor gave, null for a recipe 26.1 made */
        net.minecraft.world.item.crafting.CraftingBookCategory cb$legacyCategory();
    }

    /** post of a 1.21.11 CustomRecipe subclass's super(category): the category its category() answers, as 1.21.11's did */
    public static void customCategory(net.minecraft.world.item.crafting.CustomRecipe recipe, net.minecraft.world.item.crafting.CraftingBookCategory category) {
        ((CustomCategory) recipe).cb$setCategory(category);
    }

    /** a 1.21.11 crafting recipe subclass's own assemble(input, registries) (gen_wall_recipe_assemble.py hooks) */
    public interface LegacyAssembleCrafting {
        net.minecraft.world.item.ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput input, net.minecraft.core.HolderLookup.Provider registries);
    }

    /** a 1.21.11 single-item recipe subclass's own assemble(input, registries) */
    public interface LegacyAssembleSingle {
        net.minecraft.world.item.ItemStack assemble(net.minecraft.world.item.crafting.SingleRecipeInput input, net.minecraft.core.HolderLookup.Provider registries);
    }

    /** a 1.21.11 smithing recipe subclass's own assemble(input, registries) */
    public interface LegacyAssembleSmithing {
        net.minecraft.world.item.ItemStack assemble(net.minecraft.world.item.crafting.SmithingRecipeInput input, net.minecraft.core.HolderLookup.Provider registries);
    }

    /** 26.1's assemble(input) of such a recipe: its own assemble with the registries 1.21.11's callers passed (see {@link #assemble}) */
    public static net.minecraft.world.item.ItemStack assembleCrafting(LegacyAssembleCrafting recipe, net.minecraft.world.item.crafting.CraftingInput input) {
        return recipe.assemble(input, CbWallRegistries.access());
    }

    public static net.minecraft.world.item.ItemStack assembleSingle(LegacyAssembleSingle recipe, net.minecraft.world.item.crafting.SingleRecipeInput input) {
        return recipe.assemble(input, CbWallRegistries.access());
    }

    public static net.minecraft.world.item.ItemStack assembleSmithing(LegacyAssembleSmithing recipe, net.minecraft.world.item.crafting.SmithingRecipeInput input) {
        return recipe.assemble(input, CbWallRegistries.access());
    }

    // ---- 1.21.11 overrides of what 26.1's NormalCraftingRecipe made final (overrideRenames + mixin/CbWallNormalCraftingRecipeMixin) ----

    /** a 1.21.11 shaped / shapeless / transmute recipe subclass's own group() */
    public interface LegacyGroup {
        String cb$legacyGroup();
    }

    /** ... its own category() */
    public interface LegacyCategory {
        net.minecraft.world.item.crafting.CraftingBookCategory cb$legacyCategory();
    }

    /** ... its own showNotification() */
    public interface LegacyShowNotification {
        boolean cb$legacyShowNotification();
    }

    /** ... its own placementInfo() */
    public interface LegacyPlacementInfo {
        net.minecraft.world.item.crafting.PlacementInfo cb$legacyPlacementInfo();
    }

    /**
     * the recipes whose 1.21.11 override of a final member is running on this thread, per member: its
     * super call reaches the final 26.1 member again, which then runs its own body
     */
    private static final ThreadLocal<Map<String, java.util.Set<Object>>> IN_OVERRIDE = ThreadLocal.withInitial(java.util.HashMap::new);

    private static final StackWalker WALKER = StackWalker.getInstance();

    /**
     * enter a recipe's 1.21.11 override of the member (false: answer 26.1's body) -- false when the override is
     * already running for it, or when the final member was called straight from the renamed override (its super
     * call: a mod calling its own override reaches it directly, not through the final member)
     */
    public static boolean enterOverride(String member, String legacyName, Object recipe) {
        // frames: this method, the mixin handler, the final member, its caller
        if (WALKER.walk(frames -> frames.skip(3).findFirst().map(f -> f.getMethodName().equals(legacyName)).orElse(false))) {
            return false;
        }
        return enterOverride(member, recipe);
    }

    /** enter a recipe's 1.21.11 override of the member (false: it is already running, answer 26.1's body) */
    public static boolean enterOverride(String member, Object recipe) {
        return IN_OVERRIDE.get().computeIfAbsent(member, k -> java.util.Collections.newSetFromMap(new IdentityHashMap<>())).add(recipe);
    }

    public static void exitOverride(String member, Object recipe) {
        IN_OVERRIDE.get().get(member).remove(recipe);
    }

    /** 26.1's SingleItemRecipe keeping 1.21.11's constructor group (mixin/CbWallSingleItemRecipeMixin) */
    public interface SingleItemGroup {
        void cb$setGroup(String group);

        String cb$group();

        /** 1.21.11's result(): the recipe's result stack (26.1 keeps its template) */
        net.minecraft.world.item.ItemStack cb$resultStack();
    }
}
