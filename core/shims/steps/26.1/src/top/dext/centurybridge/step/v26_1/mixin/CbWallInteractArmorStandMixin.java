package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;

/**
 * 1.21.11's ArmorStand split between interactAt and interact, given back (see rt/CbWallInteract):
 * <ul>
 * <li>interactAt(player, location, hand) (cover): 1.21.11's body -- 26.1's interact with PASS where 26.1
 *     falls through to LivingEntity's interact.</li>
 * <li>interact(player, hand, location) of an armor stand of a 1.21.11 class, or under a mod's call of the
 *     old interact/interactOn: 1.21.11's ArmorStand had no interact of its own, so LivingEntity's
 *     (mixin/CbWallInteractArmorStandReadsMixin).</li>
 * </ul>
 */
@Mixin(value = ArmorStand.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallInteractArmorStandMixin extends LivingEntity {

    protected CbWallInteractArmorStandMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract boolean isMarker();

    @Shadow
    public abstract boolean showArms();

    @Shadow
    private EquipmentSlot getClickedSlot(Vec3 location) {
        throw new AssertionError();
    }

    @Shadow
    private boolean isDisabled(EquipmentSlot slot) {
        throw new AssertionError();
    }

    @Shadow
    private boolean swapItem(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
        throw new AssertionError();
    }

    public InteractionResult interactAt(Player player, Vec3 location, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isMarker() || stack.is(Items.NAME_TAG)) {
            return InteractionResult.PASS;
        }
        if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS_SERVER;
        }
        EquipmentSlot itemSlot = this.getEquipmentSlotForItem(stack);
        if (stack.isEmpty()) {
            EquipmentSlot clicked = this.getClickedSlot(location);
            EquipmentSlot slot = this.isDisabled(clicked) ? itemSlot : clicked;
            if (this.hasItemInSlot(slot) && this.swapItem(player, slot, stack, hand)) {
                return InteractionResult.SUCCESS_SERVER;
            }
        } else {
            if (this.isDisabled(itemSlot)) {
                return InteractionResult.FAIL;
            }
            if (itemSlot.getType() == EquipmentSlot.Type.HAND && !this.showArms()) {
                return InteractionResult.FAIL;
            }
            if (this.swapItem(player, itemSlot, stack, hand)) {
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return InteractionResult.PASS;
    }
}
