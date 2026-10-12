package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Values whose erased type stayed while their type arguments changed in 1.20.5 (generic drift):
 * the JVM links the old call, and the first element read is of the other kind. Each converter is
 * adaptive -- a value already of the wanted kind passes through -- because erasure cannot tell
 * where a value came from.
 */
public final class CbBlockGeneric {

    /**
     * CrafterBlock.getPotentialResults answers Optional&lt;RecipeHolder&lt;CraftingRecipe&gt;&gt; where 1.20.4
     * answered Optional&lt;CraftingRecipe&gt; (what 1.20.4's RecipeCache.get held): the holder's recipe.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Optional recipeValues(java.util.Optional results) {
        return results == null ? null
            : results.map(r -> r instanceof net.minecraft.class_8786 holder ? holder.comp_1933() : r);
    }

    private CbBlockGeneric() {
    }
}
