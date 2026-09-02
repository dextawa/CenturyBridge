package top.dext.centurybridge.compat.fapi;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * fabric-item-api-v1's ModifyItemAttributeModifiersCallback (4 corpus mods). Item
 * attribute modifiers became the ItemAttributeModifiers data component in 1.20.5
 * and Fabric API 26.2 offers no per-stack runtime hook, so the event exists only
 * to keep initialisers alive: it never fires (documented loss).
 */
@FunctionalInterface
public interface CbModifyItemAttributeModifiersCallback {

    Event<CbModifyItemAttributeModifiersCallback> EVENT = EventFactory.createArrayBacked(CbModifyItemAttributeModifiersCallback.class,
        listeners -> (stack, slot, modifiers) -> {
        });

    void modifyAttributeModifiers(ItemStack stack, EquipmentSlot slot, Multimap<Attribute, AttributeModifier> attributeModifiers);
}
