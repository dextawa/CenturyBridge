package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's ProjectileDispenseBehavior: an abstract dispense behaviour whose subclass makes the projectile
 * (getProjectile) and may change its inaccuracy and power; the behaviour shoots it out of the dispenser's face and
 * plays the launch sound. 1.20.5 turned the class into a concrete behaviour over a ProjectileItem's dispense
 * config, which a 1.20.4 subclass has none of; a 1.20.4 subclass extends this instead (superRenames), 1.20.4's
 * bodies over 1.20.5's dispenser API. It is a 1.20.5 ProjectileDispenseBehavior too (wherever 1.20.5 types a value
 * as one), built over the arrow item to satisfy its constructor; the arrow's dispense config is never read, both
 * methods that would read it being 1.20.4's here.
 */
public abstract class ProjectileDispenseBehavior extends net.minecraft.class_2965 {

    public ProjectileDispenseBehavior() {
        super(net.minecraft.class_1802.field_8107);
    }

    /** execute(source, stack): the projectile shot 0.1 upward of the facing, by getPower() and getUncertainty(); one item used */
    @Override
    public net.minecraft.class_1799 method_10135(net.minecraft.class_2342 source, net.minecraft.class_1799 stack) {
        net.minecraft.class_1937 level = source.comp_1967();
        net.minecraft.class_2374 pos = top.dext.centurybridge.step.v1_20_5.rt.CbFinWorld.dispensePosition(source);
        net.minecraft.class_2350 direction = source.comp_1969().method_11654(net.minecraft.class_2315.field_10918);
        net.minecraft.class_1676 projectile = this.method_12844(level, pos, stack);
        projectile.method_7485(direction.method_10148(), direction.method_10164() + 0.1F, direction.method_10165(),
            this.method_12846(), this.method_12845());
        level.method_8649(projectile);
        stack.method_7934(1);
        return stack;
    }

    /** playSound(source): the projectile launch event */
    @Override
    protected void method_10136(net.minecraft.class_2342 source) {
        source.comp_1967().method_20290(1002, source.comp_1968(), 0);
    }

    /** getProjectile(level, position, stack) */
    protected abstract net.minecraft.class_1676 method_12844(net.minecraft.class_1937 level, net.minecraft.class_2374 position, net.minecraft.class_1799 stack);

    /** getUncertainty() */
    protected float method_12845() {
        return 6.0F;
    }

    /** getPower() */
    protected float method_12846() {
        return 1.1F;
    }
}
