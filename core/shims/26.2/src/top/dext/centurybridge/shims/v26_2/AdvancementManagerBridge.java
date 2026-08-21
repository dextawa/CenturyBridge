package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: AdvancementManager.get now returns PlacedAdvancement;
 * the legacy overload unwraps back to the Advancement record.
 */
@Mixin(net.minecraft.advancements.AdvancementTree.class)
public abstract class AdvancementManagerBridge {

    public net.minecraft.advancements.Advancement get(net.minecraft.resources.Identifier id) {
        net.minecraft.advancements.AdvancementNode placed = ((net.minecraft.advancements.AdvancementTree) (Object) this).get(id);
        return placed == null ? null : placed.advancement();
    }
}
