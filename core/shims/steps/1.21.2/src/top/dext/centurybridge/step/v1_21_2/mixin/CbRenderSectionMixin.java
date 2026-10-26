package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 SectionRenderDispatcher.RenderSection members over 1.21.2's, which addresses a section by its SectionPos node
 * where 1.21.1 kept its block origin and the six neighbouring origins:
 * - setOrigin(x, y, z): 1.21.2's setSectionNode of the section holding that block origin (the same reset, origin and
 *   bounding box);
 * - getRelativeOrigin(direction): the origin of the neighbouring section, 16 blocks along the direction (1.21.1's
 *   precomputed relativeOrigins);
 * - isAxisAlignedWith(x, y, z): 1.21.1's body, on the section coordinates of the origin;
 * - resortTransparency(renderType, dispatcher): 1.21.1 resorted (and answered true) only when the section had blocks of
 *   that render type, and its resort task always sorted the translucent buffer -- what 1.21.2's resortTransparency(
 *   dispatcher) does;
 * - cancelTasks(): 1.21.1 answered whether there was a compile or resort task to cancel; 1.21.2's cancels the same two
 *   and answers nothing (cb$cancelTasks: whether either was there, then 1.21.2's).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_846.class_851.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderSectionMixin implements top.dext.centurybridge.step.v1_21_2.rt.CbRenderSections.LegacySection {

    @Unique
    private net.minecraft.class_846.class_851 cb$self() {
        return (net.minecraft.class_846.class_851) (Object) this;
    }

    /** 1.21.1 setOrigin(x, y, z) */
    public void method_3653(int x, int y, int z) {
        cb$self().method_62973(net.minecraft.class_4076.method_18685(net.minecraft.class_4076.method_18675(x),
            net.minecraft.class_4076.method_18675(y), net.minecraft.class_4076.method_18675(z)));
    }

    /** 1.21.1 getRelativeOrigin(direction) */
    public net.minecraft.class_2338 method_3676(net.minecraft.class_2350 direction) {
        return cb$self().method_3670().method_10079(direction, 16);
    }

    /** 1.21.1 isAxisAlignedWith(x, y, z) */
    public boolean method_52841(int x, int y, int z) {
        net.minecraft.class_2338 origin = cb$self().method_3670();
        return x == net.minecraft.class_4076.method_18675(origin.method_10263()) || z == net.minecraft.class_4076.method_18675(origin.method_10260())
            || y == net.minecraft.class_4076.method_18675(origin.method_10264());
    }

    /** 1.21.1 resortTransparency(renderType, dispatcher) */
    public boolean method_22773(net.minecraft.class_1921 renderType, net.minecraft.class_846 dispatcher) {
        if (cb$self().method_3677().method_3641(renderType)) {
            return false;
        }
        cb$self().method_22773(dispatcher);
        return true;
    }

    @Shadow
    private net.minecraft.class_846.class_851.class_4578 field_20834;

    @Shadow
    private net.minecraft.class_846.class_851.class_4579 field_4461;

    @Shadow
    protected abstract void method_3663();

    /** 1.21.1 cancelTasks(): whether a task was there to cancel */
    @Override
    public boolean cb$cancelTasks() {
        boolean any = this.field_20834 != null || this.field_4461 != null;
        this.method_3663();
        return any;
    }
}
