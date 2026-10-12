package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 bodies of entity methods 1.20.5 made abstract, removed, or stopped calling, for mod classes
 * written against 1.20.4 (the abstract fills and the arrow deflection hook of this step).
 */
public final class CbRestMobs {

    /**
     * Added to a mod arrow class that overrides 1.20.4's AbstractArrow.deflect(): 1.20.4 called it when
     * the arrow hit an entity that deflects arrows (the breeze); 1.20.5 deflects through the target's
     * ProjectileDeflection instead (mixin/CbRestProjectileMixin hands that to the mod's method).
     */
    public interface LegacyDeflect {
        void method_55052();
    }

    /** Animal.isFood(stack): 1.20.4's body, which 1.20.5 made abstract -- wheat */
    public static boolean isFood(net.minecraft.class_1429 animal, net.minecraft.class_1799 stack) {
        return stack.method_31574(net.minecraft.class_1802.field_8861);
    }

    /**
     * AbstractArrow.getDefaultPickupItem(), which 1.20.5 made abstract: 1.20.4 had no default pickup item
     * of its own -- an arrow kept the stack it was constructed with, and loading one without an item
     * tag left that stack as it was. So the default is the arrow's current pickup stack: 1.20.5's load
     * then sets the stack the arrow already has. While 1.20.5's constructor asks for it the arrow holds
     * none yet (the constructor taking a stack sets that one right after): the empty stack.
     */
    public static net.minecraft.class_1799 defaultPickupItem(net.minecraft.class_1665 arrow) {
        net.minecraft.class_1799 stack = arrow.method_54759();
        return stack != null ? stack : net.minecraft.class_1799.field_8037;
    }

    /**
     * ContainerEntity.getBoundingBox(), which 1.20.5 added to the interface (its own defaults measure
     * reach from it): an entity has its own; 1.20.4's ContainerEntity located a container by its
     * position() alone, so for an implementor that is no entity the box is that point.
     */
    public static net.minecraft.class_238 boundingBox(net.minecraft.class_7265 container) {
        net.minecraft.class_243 pos = container.method_19538();
        return new net.minecraft.class_238(pos, pos);
    }

    private CbRestMobs() {
    }
}
