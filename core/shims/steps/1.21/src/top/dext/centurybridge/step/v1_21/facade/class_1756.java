package top.dext.centurybridge.step.v1_21.facade;

/**
 * 1.20.6's BowlFoodItem on 1.21 (classRenames net/minecraft/class_1756; 1.21's stews are plain items whose
 * food converts to a bowl): 1.20.6's body -- eaten as any food, then a bowl in its place unless the eater
 * has infinite materials.
 */
public class class_1756 extends net.minecraft.class_1792 {

    public class_1756(net.minecraft.class_1792.class_1793 properties) {
        super(properties);
    }

    /** finishUsingItem */
    @Override
    public net.minecraft.class_1799 method_7861(net.minecraft.class_1799 stack, net.minecraft.class_1937 level, net.minecraft.class_1309 entity) {
        net.minecraft.class_1799 eaten = super.method_7861(stack, level, entity);
        if (entity instanceof net.minecraft.class_1657 player && player.method_56992()) {
            return eaten;
        }
        return new net.minecraft.class_1799(net.minecraft.class_1802.field_8428);
    }
}
