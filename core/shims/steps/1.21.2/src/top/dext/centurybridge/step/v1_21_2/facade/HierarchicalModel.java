package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelBase;

/**
 * 1.21.1's HierarchicalModel (net/minecraft/class_5597, classRenames), gone in 1.21.2 (every model is built around a
 * root there). 1.21.1's members (all public: an access widener never reaches a CenturyBridge class) and bodies: the subclass answers root(), renderToBuffer draws it, the part lookup and
 * the keyframe animation helpers work on it. Built on 1.21.2's EntityModel with an empty root (CbRenderModelBase: this
 * model draws itself); 1.21.2's part lookup (getAnyDescendantWithName, which 1.21.2's KeyframeAnimations uses) answers
 * from root(), so the animation helpers -- 1.21.2's own Model.animate &amp; co., the same formulas -- move this model's
 * parts.
 */
@Environment(EnvType.CLIENT)
public abstract class HierarchicalModel<E extends net.minecraft.class_1297> extends net.minecraft.class_583<net.minecraft.class_10017> {

    /** ANIMATION_VECTOR_CACHE (private in 1.21.1; 1.21.2's Model.animate uses its own) */
    public static final org.joml.Vector3f field_39195 = new org.joml.Vector3f();

    public HierarchicalModel() {
        this(net.minecraft.class_1921::method_23578);
    }

    public HierarchicalModel(Function<net.minecraft.class_2960, net.minecraft.class_1921> renderType) {
        super(CbRenderModelBase.emptyRoot(), renderType);
    }

    /** renderToBuffer: root().render(...) */
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        this.method_32008().method_22699(pose, consumer, light, overlay, color);
    }

    /** root() */
    public abstract net.minecraft.class_630 method_32008();

    /** getAnyDescendantWithName(name): "root" is root(), else the first part under root() with such a child, its child */
    public Optional<net.minecraft.class_630> method_41909(String name) {
        if (name.equals("root")) {
            return Optional.of(this.method_32008());
        }
        return this.method_32008().method_32088().filter(part -> part.method_41919(name)).findFirst().map(part -> part.method_32086(name));
    }

    /** 1.21.2's part lookup, as 1.21.1's getAnyDescendantWithName (the keyframe animations find this model's parts) */
    @Override
    public Optional<net.minecraft.class_630> method_62104(String name) {
        return this.method_41909(name);
    }

    /** animate(state, definition, ageInTicks) = animate(state, definition, ageInTicks, 1) */
    public void method_43781(net.minecraft.class_7094 state, net.minecraft.class_7184 definition, float ageInTicks) {
        this.method_43782(state, definition, ageInTicks, 1.0F);
    }

    /** animate(state, definition, ageInTicks, speed): 1.21.2's Model.animate on this model's parts */
    public void method_43782(net.minecraft.class_7094 state, net.minecraft.class_7184 definition, float ageInTicks, float speed) {
        this.method_62099(state, definition, ageInTicks, speed);
    }

    /** animateWalk(definition, walkPos, walkSpeed, timeMultiplier, speedMultiplier): 1.21.1 and 1.21.2 compute it alike */
    public void method_48741(net.minecraft.class_7184 definition, float walkPos, float walkSpeed, float timeMultiplier, float speedMultiplier) {
        this.method_62102(definition, walkPos, walkSpeed, timeMultiplier, speedMultiplier);
    }

    /** applyStatic(definition) */
    public void method_52185(net.minecraft.class_7184 definition) {
        this.method_62101(definition);
    }
}
