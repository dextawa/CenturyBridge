package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_5151 (Equipable), removed at 1.21.2
 * (equipment handling moved to the Equippable data component). Extends the
 * Vanishable facade as the original did. Includes the interface's default
 * methods (equip sound, swap-to-equip action) so a mod that calls them resolves
 * -- the bodies degrade to "no sound / pass" since 26.2 drives equipping
 * through the data component.
 */
public interface class_5151 extends class_5150 {
    /** getEquipmentSlot() */
    net.minecraft.world.entity.EquipmentSlot method_7685();

    /** getEquipSound(): no sound on the facade (equipping is data-driven on 26.2) */
    default net.minecraft.sounds.SoundEvent method_31570() {
        return null;
    }

    /** swapWithEquipmentSlot(item, level, player, hand): pass-through, no swap */
    default class_1271<net.minecraft.world.item.ItemStack> method_48576(
            net.minecraft.world.item.Item item,
            net.minecraft.world.level.Level level,
            net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        return new class_1271<>(net.minecraft.world.InteractionResult.PASS,
                player.getItemInHand(hand));
    }
}
