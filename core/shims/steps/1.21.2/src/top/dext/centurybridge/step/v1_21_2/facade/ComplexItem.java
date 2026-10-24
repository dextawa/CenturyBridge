package top.dext.centurybridge.step.v1_21_2.facade;

import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemMembers;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemSmall;

/**
 * 1.21.1's ComplexItem (net/minecraft/class_1762, classRenames), gone in 1.21.2 (MapItem extends Item; the server
 * synchronises an inventory stack by its map id). An item whose isComplex() is true: every tick 1.21.1's ServerPlayer
 * sent the player what getUpdatePacket(stack, level, player) answered for each such stack in its inventory --
 * CbItemComplexMixin does so on 1.21.2. Its own getUpdatePacket answers null.
 */
public class ComplexItem extends class_1792 implements CbItemMembers.LegacyComplex, CbItemSmall.Legacy_getUpdatePacket {

    public ComplexItem(class_1792.class_1793 properties) {
        super(properties);
    }

    /** isComplex(): true */
    @Override
    public boolean method_16698() {
        return true;
    }

    /** getUpdatePacket(stack, level, player): none */
    @Override
    public class_2596<?> method_7757(class_1799 stack, class_1937 level, class_1657 player) {
        return null;
    }
}
