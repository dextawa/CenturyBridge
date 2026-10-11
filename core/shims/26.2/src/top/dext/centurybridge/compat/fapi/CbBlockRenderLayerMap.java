package top.dext.centurybridge.compat.fapi;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * fabric-blockrenderlayer-v1's BlockRenderLayerMap as 1.20.x mods call it. The
 * API no longer exists in the 26.x Fabric API: a block's chunk layer is a
 * property of its model (26.x materials), not a runtime map, and vanilla keeps
 * no ItemBlockRenderTypes table to write into. The converter renames the
 * interface here so client initialisers no longer die with NoClassDefFoundError;
 * the registrations themselves are a documented no-op -- declare the layer in
 * the model JSON instead.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbBlockRenderLayerMap {

    CbBlockRenderLayerMap INSTANCE = new CbBlockRenderLayerMap() {
    };

    default void putBlock(Block block, RenderType layer) {
        Warn.once();
    }

    default void putBlocks(RenderType layer, Block... blocks) {
        Warn.once();
    }

    default void putFluid(Fluid fluid, RenderType layer) {
        Warn.once();
    }

    default void putFluids(RenderType layer, Fluid... fluids) {
        Warn.once();
    }

    default void putItem(Item item, RenderType layer) {
        Warn.once();
    }

    default void putItems(RenderType layer, Item... items) {
        Warn.once();
    }

    final class Warn {
        private static final java.util.concurrent.atomic.AtomicBoolean DONE = new java.util.concurrent.atomic.AtomicBoolean();

        private Warn() {
        }

        static void once() {
            if (DONE.compareAndSet(false, true)) {
                System.err.println("[CenturyBridge] BlockRenderLayerMap is a no-op on 26.x: block render layers come from the model (declare render_type in the model JSON)");
            }
        }
    }
}
