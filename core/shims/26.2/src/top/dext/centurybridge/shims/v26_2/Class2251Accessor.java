package top.dext.centurybridge.shims.v26_2;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Key plumbing on Block Settings for the pending-key repair (see CbPending). */
@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.Properties.class)
public interface Class2251Accessor {

    @Accessor("id")
    net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.Block> cbGetKey();

    @Accessor("id")
    void cbSetKey(net.minecraft.resources.ResourceKey<net.minecraft.world.level.block.Block> key);

    @Invoker("effectiveDrops")
    Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> cbLootTableKey();

    @Invoker("effectiveDescriptionId")
    String cbTranslationKey();
}
