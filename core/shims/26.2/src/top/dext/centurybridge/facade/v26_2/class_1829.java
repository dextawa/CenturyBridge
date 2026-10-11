package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Facade for 1.20.1 net/minecraft/class_1829 (SwordItem), deleted on 26.2
 * (weapons are data-driven). Extends the TieredItem facade (real Item), old
 * (Tier,int,float,Properties) constructor forwarding to it; attack damage/speed
 * dropped. Legacy sword subclasses load and register as real Items.
 */
public class class_1829 extends class_1831 implements class_5150 {

    public final float field_8920; // attackDamage

    public class_1829(class_1832 tier, int attackDamage, float attackSpeed, Item.Properties props) {
        super(tier, props);
        this.field_8920 = attackDamage;
    }

    /** getDamage() */
    public float method_8020() {
        return field_8920;
    }

    /** canAttackBlock(state, level, pos, player) */
    public boolean method_7885(BlockState state, net.minecraft.world.level.Level level,
                               net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player) {
        return true;
    }

    /** getDestroySpeed(stack, state) */
    public float method_7865(ItemStack stack, BlockState state) {
        return 1.0F;
    }

    /** hurtEnemy(stack, target, attacker) */
    public boolean method_7873(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    /** mineBlock(stack, level, state, pos, miner) */
    public boolean method_7879(ItemStack stack, net.minecraft.world.level.Level level,
                               BlockState state, net.minecraft.core.BlockPos pos, LivingEntity miner) {
        return true;
    }

    /** isCorrectToolForDrops(state) */
    public boolean method_7856(BlockState state) {
        return false;
    }

    /** getDefaultAttributeModifiers(slot) */
    public Multimap<Attribute, AttributeModifier> method_7844(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }
}
