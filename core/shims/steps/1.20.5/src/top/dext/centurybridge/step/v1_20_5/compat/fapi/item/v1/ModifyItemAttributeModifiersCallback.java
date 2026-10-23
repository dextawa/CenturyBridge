package top.dext.centurybridge.step.v1_20_5.compat.fapi.item.v1;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Fabric API 0.97.3's (1.20.4) ModifyItemAttributeModifiersCallback, removed in 1.20.5: fired for a stack's
 * attribute modifiers in a slot, on a modifiable copy, when the stack has none of its own (1.20.4: no
 * AttributeModifiers NBT; 1.20.5: its component patch sets none). Fired as there by impl/item/CbFapiItems
 * (CbFapiItemStackMixin, ItemStack.applyAttributeModifiers). The invoker as in 1.20.4 (Fabric API, Apache-2.0);
 * its factory notes whether any listener is registered, so stacks of items 1.20.4 code has no say in keep
 * vanilla's path.
 */
@FunctionalInterface
public interface ModifyItemAttributeModifiersCallback {

    void modifyAttributeModifiers(net.minecraft.class_1799 stack, net.minecraft.class_1304 slot,
                                  Multimap<net.minecraft.class_1320, net.minecraft.class_1322> attributeModifiers);

    Event<ModifyItemAttributeModifiersCallback> EVENT = EventFactory.createArrayBacked(
        ModifyItemAttributeModifiersCallback.class,
        callbacks -> {
            top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.item.CbFapiItems.modifierListeners(callbacks.length > 0);
            return (stack, slot, attributeModifiers) -> {
                for (ModifyItemAttributeModifiersCallback callback : callbacks) {
                    callback.modifyAttributeModifiers(stack, slot, attributeModifiers);
                }
            };
        }
    );
}
