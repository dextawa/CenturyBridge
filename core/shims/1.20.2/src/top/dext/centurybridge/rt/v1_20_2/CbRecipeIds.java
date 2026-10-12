package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_1860;
import net.minecraft.class_2960;
import net.minecraft.class_8786;

/**
 * A 1.20.1 recipe carried its own id: every recipe constructor took it and Recipe.getId()
 * returned it. 1.20.2 moved the id out of the recipe into the RecipeEntry (class_8786) that
 * wraps it. This keeps the id with the recipe object again, from every place a 1.20.1 world
 * would have had it:
 * <ul>
 * <li>every recipe a RecipeManager holds, server and client (RecipeManagerBridge);</li>
 * <li>every recipe built through a 1.20.1 constructor: the NEW-site factories record it, and a
 *     super(id, ...) delegation drops the id into {@link #stash} right before the 1.20.2
 *     constructor runs, which the recipe base class claims when its constructor returns
 *     (CbRecipeIdClaim);</li>
 * <li>every recipe a bridged 1.20.1 reader (read(id, json) / read(id, buf)) returns, and every
 *     recipe a RecipeEntry hands to converted code ({@link #value}).</li>
 * </ul>
 * Keys are compared by identity and held weakly (Guava MapMaker.weakKeys), so an id belongs to
 * one recipe object -- not to an equal-looking one -- and goes away with it.
 */
public final class CbRecipeIds {

    private static final ConcurrentMap<Object, class_2960> IDS = new MapMaker().weakKeys().makeMap();

    /** the id a 1.20.1 constructor call passed, waiting for the recipe base constructor to finish */
    private static final ThreadLocal<class_2960> PENDING = new ThreadLocal<>();

    /** records {@code id} as the recipe's own id and returns the recipe */
    public static <R> R remember(R recipe, class_2960 id) {
        if (recipe != null && id != null) {
            IDS.put(recipe, id);
        }
        return recipe;
    }

    /**
     * The id the recipe was built or loaded with -- what the id field of a 1.20.1 recipe held,
     * and so what the vanilla getId() implementations returned.
     */
    public static class_2960 storedId(Object recipe) {
        class_2960 id = IDS.get(recipe);
        if (id == null) {
            throw new IllegalStateException("CenturyBridge: no id is known for recipe " + recipe.getClass().getName()
                + " -- 1.20.2 keeps recipe ids in RecipeEntry, and this recipe was neither loaded by a RecipeManager"
                + " nor built through a 1.20.1 recipe constructor or reader");
        }
        return id;
    }

    /**
     * recipe.getId() the way 1.20.1 dispatched it: a recipe class that declares its own getId()
     * answers itself (adapterHooks give every such converted class {@link CbLegacyRecipe}); any
     * other recipe answers with its stored id.
     */
    public static class_2960 idOf(class_1860<?> recipe) {
        if (recipe instanceof CbLegacyRecipe own) {
            return own.method_8114();
        }
        return storedId(recipe);
    }

    /** a Recipe handed to a 1.20.2 method that takes the RecipeEntry: the entry vanilla holds for it (equal by id) */
    public static class_8786<?> entry(class_1860<?> recipe) {
        return recipe == null ? null : new class_8786<>(idOf(recipe), recipe);
    }

    /** a RecipeEntry handed to 1.20.1 code, which knows only the recipe: the recipe, its id kept with it */
    public static class_1860<?> value(class_8786<?> entry) {
        return entry == null ? null : remember(entry.comp_1933(), entry.comp_1932());
    }

    // ---- 1.20.1 constructors: the dropped id argument (superCtorReshapes "drop OWNER#stash")

    /** the id argument of a 1.20.1 recipe constructor whose 1.20.2 shape has none */
    public static void stash(class_2960 id) {
        PENDING.set(id);
    }

    /**
     * Called as a recipe base constructor returns: the object under construction takes the
     * stashed id, if a 1.20.1 constructor call left one. Nothing runs between the stash (just
     * before the constructor invocation) and the base constructor's return but vanilla 1.20.2
     * constructor bodies, which only assign fields -- no other recipe is built in between.
     */
    public static void claim(Object recipe) {
        class_2960 id = PENDING.get();
        if (id != null) {
            PENDING.remove();
            IDS.put(recipe, id);
        }
    }

    // ---- super.getId() in a converted subclass (superRedirects): the vanilla implementation
    // returned the recipe's id field, never the subclass's own override

    public static class_2960 storedId(net.minecraft.class_1852 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_1867 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_1869 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_1874 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_3972 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_8060 self) {
        return storedId((Object) self);
    }

    public static class_2960 storedId(net.minecraft.class_8062 self) {
        return storedId((Object) self);
    }

    // ---- the protected id fields a subclass read directly (instanceFieldGetters)

    /** AbstractCookingRecipe.id */
    public static class_2960 field_9060(net.minecraft.class_1874 self) {
        return storedId((Object) self);
    }

    /** CuttingRecipe (SingleItemRecipe).id */
    public static class_2960 field_17644(net.minecraft.class_3972 self) {
        return storedId((Object) self);
    }

    private CbRecipeIds() {
    }
}
