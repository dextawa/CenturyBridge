package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Facade for 1.20.1 net/minecraft/class_1766 (DiggerItem), the common parent of
 * the pickaxe/axe/shovel/hoe item classes, deleted when tool behaviour moved to
 * data components. Extends the TieredItem facade (hence the real Item). The old
 * mining-speed / attribute constructor and accessors are kept so legacy tool
 * subclasses load and their overrides resolve; the attribute map degrades to
 * empty (26.2 drives tool attributes through data components -- any override is
 * orphaned, which is acceptable: the class loads).
 */
public class class_1766 extends class_1831 implements class_5150 {

    public final float field_7940; // attackDamage

    public class_1766(float attackDamage, float attackSpeed, class_1832 tier,
                         TagKey<Block> blocks, Item.Properties props) {
        super(tier, props);
        this.field_7940 = attackDamage;
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

    /** getDefaultAttributeModifiers(slot): empty (data-driven on 26.2) */
    public Multimap<Attribute, AttributeModifier> method_7844(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }

    /** getAttackDamage() */
    public float method_26366() {
        return field_7940;
    }

    /** isCorrectToolForDrops(state) */
    public boolean method_7856(BlockState state) {
        return false;
    }
}
