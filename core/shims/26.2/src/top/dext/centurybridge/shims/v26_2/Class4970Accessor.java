package top.dext.centurybridge.shims.v26_2;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Re-derivable materialized fields on AbstractBlock (see CbPending). */
@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.class)
public interface Class4970Accessor {

    @Accessor("drops")
    @Mutable
    void cbSetLootTableKey(Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> key);

    @Accessor("descriptionId")
    @Mutable
    void cbSetTranslationKey(String key);
}
