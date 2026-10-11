package top.dext.centurybridge.facade.v26_2;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;

/** Facade for 1.20.1 net/minecraft/class_806 (ItemOverrides), deleted on 26.2
 *  (item model overrides are data-driven). Legacy mods extend it to resolve a
 *  model per item/state. The old (ModelBaker, BlockModel, List<ItemOverride>)
 *  constructor is kept (ItemOverride=class_799 deleted, but it only appears in
 *  the erased List parameter); resolve(method_3495) defaults to returning the
 *  input model. Member names are intermediary tokens. */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_806 {
    public class_806() {}

    @SuppressWarnings("rawtypes")
    public class_806(ModelBaker baker, BlockModel model, java.util.List overrides) {}

    /** resolve(model, stack, level, entity, seed) (method_3495) -> input model */
    public net.minecraft.client.renderer.block.dispatch.BlockStateModel method_3495(
            net.minecraft.client.renderer.block.dispatch.BlockStateModel model,
            ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        return model;
    }
}
