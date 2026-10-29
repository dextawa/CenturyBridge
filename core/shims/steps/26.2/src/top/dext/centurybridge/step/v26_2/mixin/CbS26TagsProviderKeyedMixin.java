package top.dext.centurybridge.step.v26_2.mixin;

import java.util.function.Function;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.PotionTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * The 26.2 providers that 26.1.2 derived from a provider of elements, which 26.2 deleted: vanilla's EntityTypeTagsProvider,
 * FluidTagsProvider, VanillaBlockTagsProvider and VanillaItemTagsProvider (IntrinsicHolderTagsProvider) and
 * PotionTagsProvider (HolderTagProvider); Fabric API's Block / Item / Fluid / EntityType / BlockEntityType providers
 * (FabricIntrinsicHolderTagsProvider). They keep their 26.1.2 conversion for the restored tag / valueLookupBuilder
 * (CbS26TagsProviderRestoredMixin, CbS26FapiTagsProviderRestoredMixin), so a 26.1.2 mod extending one of them adds
 * elements as 26.1.2 did. The conversions are 26.1.2's constructor lambdas (CbS26TagProviders.builtInKey). Priority
 * below every mod's.
 */
@Mixin(value = {EntityTypeTagsProvider.class, FluidTagsProvider.class, VanillaBlockTagsProvider.class, VanillaItemTagsProvider.class,
        PotionTagsProvider.class, FabricTagsProvider.EntityTypeTagsProvider.class, FabricTagsProvider.FluidTagsProvider.class,
        FabricTagsProvider.BlockTagsProvider.class, FabricTagsProvider.ItemTagsProvider.class,
        FabricTagsProvider.BlockEntityTypeTagsProvider.class}, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26TagsProviderKeyedMixin implements CbS26TagProviders.Keyed {

    @Override
    public Function<?, ?> cb$v26_2$elementKey() {
        return CbS26TagProviders.builtInKey(this);
    }
}
