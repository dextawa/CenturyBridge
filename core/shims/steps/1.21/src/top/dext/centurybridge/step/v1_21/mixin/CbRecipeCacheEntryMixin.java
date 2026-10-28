package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21.rt.CbRecipeCacheEntries;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * RecipeCache.Entry (class_8884$class_8885): restores 1.20.6's matches(List<ItemStack>) (method_54472(List)) with its body
 * over the entry's key. 1.21's matches(CraftingInput) tests the input's width and height first and then the same items
 * pairwise; CbRecipeCacheMixin routes RecipeCache.get's test through the restored method after 1.21's size test, so a
 * 1.20.6 mod's mixin on matches decides whether the cached recipe is reused.
 */
@Mixin(targets = "net.minecraft.class_8884$class_8885", priority = top.dext.centurybridge.step.v1_21.rt.CbVanillaReads.PRIORITY)
public abstract class CbRecipeCacheEntryMixin implements CbRecipeCacheEntries.Restored {

    @Shadow
    @Final
    private net.minecraft.class_2371<net.minecraft.class_1799> comp_2001;

    @Shadow
    public abstract int comp_2816();

    @Shadow
    public abstract int comp_2817();

    /** 1.20.6 RecipeCache.Entry.matches(inputs) */
    @Override
    @CbVanillaReads(value = "CbRecipeCacheMixin: RecipeCache.get (method_54470) decides whether a cached entry serves the input with "
        + "this method, after 1.21's width/height test (cb$matches replaces 1.21's matches(CraftingInput)). The body is 1.20.6's: "
        + "NonNullList.size / NonNullList.get / List.size / List.get / ItemStack.isSameItemSameComponents stand as in 1.20.6. The list "
        + "is 1.21's CraftingInput items (the recipe area trimmed to its bounding box, row by row), where 1.20.6 passed the whole grid; "
        + "the key it is compared with is trimmed the same way",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyArgs@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE"})
    public boolean method_54472(java.util.List<net.minecraft.class_1799> inputs) {
        if (this.comp_2001.size() != inputs.size()) {
            return false;
        }
        for (int i = 0; i < this.comp_2001.size(); i++) {
            if (!net.minecraft.class_1799.method_31577(this.comp_2001.get(i), inputs.get(i))) {
                return false;
            }
        }
        return true;
    }
}
