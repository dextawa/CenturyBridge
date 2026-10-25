package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_1937;
import net.minecraft.class_2680;
import net.minecraft.class_6862;
import net.minecraft.class_9334;
import net.minecraft.class_9886;
import top.dext.centurybridge.step.v1_21_5.facade.PickaxeItem;
import top.dext.centurybridge.step.v1_21_5.facade.SwordItem;

/**
 * 1.21.4's tool items on 1.21.5 (item area). 1.21.5 removed SwordItem, DiggerItem and PickaxeItem: its swords and
 * pickaxes are plain Items built from Item.Properties.sword / pickaxe, and what 1.21.4's SwordItem / DiggerItem
 * bodies did on a hit -- count as a weapon use (hurtEnemy answered true) and wear the item by 1 / 2
 * (postHurtEnemy) -- is the WEAPON component 1.21.5's ToolMaterial puts on the properties.
 * <p>
 * A 1.21.4 tool class keeps 1.21.4's bodies (the facades SwordItem / DiggerItem / PickaxeItem), so its properties
 * are 1.21.4's: ToolMaterial's tool and attribute components without WEAPON, which 1.21.4 had no notion of -- the
 * facade's own hurtEnemy / postHurtEnemy answer and wear the item as 1.21.4 did, and a subclass overriding them
 * decides as on 1.21.4 (with WEAPON 1.21.5 would wear the item a second time and ignore the override). The same
 * holds for a 1.21.4 mod's own calls of ToolMaterial.applySwordProperties / applyToolProperties (callReshapes):
 * on 1.21.4 they gave an item no weapon behaviour.
 */
public final class CbItemTools {

    private CbItemTools() {
    }

    /** 1.21.4's properties: what 1.21.5's ToolMaterial set, without the WEAPON component 1.21.4 did not have */
    public static class_1792.class_1793 legacyProps(class_1792.class_1793 props) {
        // Properties.component(type, null) removes the entry (DataComponentMap.Builder.set)
        return props.method_57349(class_9334.field_55878, null);
    }

    /** 1.21.4's ToolMaterial.applySwordProperties(props, damage, speed) */
    public static class_1792.class_1793 swordProps(class_9886 material, class_1792.class_1793 props, float damage, float speed) {
        return legacyProps(material.method_61662(props, damage, speed));
    }

    /**
     * 1.21.4's ToolMaterial.applyToolProperties(props, mineable, damage, speed): 1.21.5's with no shield-disabling
     * time (1.21.4 disabled shields by the AxeItem type, LivingEntity.canDisableShield; 1.21.5's AxeItem still sets
     * its own) and without WEAPON
     */
    public static class_1792.class_1793 toolProps(class_9886 material, class_1792.class_1793 props, class_6862<class_2248> mineable,
            float damage, float speed) {
        return legacyProps(material.method_61663(props, mineable, damage, speed, 0.0F));
    }

    /** {@code new SwordItem(material, damage, speed, props)} (ctorRedirects): the facade, 1.21.4's class */
    public static class_1792 newSwordItem(class_9886 material, float damage, float speed, class_1792.class_1793 props) {
        return new SwordItem(material, damage, speed, props);
    }

    /** {@code new PickaxeItem(material, damage, speed, props)} (ctorRedirects): the facade, 1.21.4's class */
    public static class_1792 newPickaxeItem(class_9886 material, float damage, float speed, class_1792.class_1793 props) {
        return new PickaxeItem(material, damage, speed, props);
    }

    /** a 1.21.4 tool's postHurtEnemy: 1.21.4's SwordItem (1) / DiggerItem (2) wear */
    public static void wear(class_1799 stack, int amount, class_1309 attacker) {
        stack.method_7970(amount, attacker, net.minecraft.class_1304.field_6173);
    }

    /**
     * the stack 1.21.4's canAttackBlock(state, level, pos, player) stood for, for 1.21.5's shape that takes it: every
     * 1.21.4 caller (ServerPlayerGameMode, MultiPlayerGameMode) asked the item of the player's main hand stack
     * (callReshapes: a 1.21.4 mod's own call)
     */
    public static class_1799 mainHand(class_1657 player) {
        return player.method_6047();
    }

    /** frameDefaults for canAttackBlock's stack: a 1.21.4 override entered directly asks about the main hand stack */
    public static class_1799 attackStack(class_1792 self, class_2680 state, class_1937 level, class_2338 pos, class_1657 player) {
        return player.method_6047();
    }

    /** applyToolProperties' disableBlockingForSeconds for a 1.21.4 call: none (1.21.4 had no such value; see toolProps) */
    public static float noBlockingDisable() {
        return 0.0F;
    }

    /** Tool's canDestroyBlocksInCreative for a 1.21.4 {@code new Tool(rules, speed, damagePerBlock)}: see the wiring basis */
    public static boolean destroysInCreative() {
        return true;
    }
}
