package top.dext.centurybridge.shims.v26_2;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v26_2.CbPending;

/**
 * Legacy construct-then-register order: the constructor's two key-derived
 * reads get a placeholder key first (see CbPending). Redirects rather than
 * an inject because constructors only accept injection at RETURN.
 */
@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.class)
public abstract class AbstractBlockCtorBridge {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;effectiveDrops()Ljava/util/Optional;"))
    private Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable>> cb$lootKey(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        CbPending.ensureBlockKey(this, settings);
        return ((Class2251Accessor) settings).cbLootTableKey();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;effectiveDescriptionId()Ljava/lang/String;"))
    private String cb$translationKey(net.minecraft.world.level.block.state.BlockBehaviour.Properties settings) {
        CbPending.ensureBlockKey(this, settings);
        return ((Class2251Accessor) settings).cbTranslationKey();
    }
}
