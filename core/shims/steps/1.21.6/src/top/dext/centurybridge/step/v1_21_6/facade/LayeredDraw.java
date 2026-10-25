package top.dext.centurybridge.step.v1_21_6.facade;

/**
 * 1.21.5's LayeredDraw (class_9080), which 1.21.6 removed: Gui draws its HUD pieces itself and Fabric API orders them
 * through HudElementRegistry. Members as 1.21.5's (javap): add(Layer) appends, add(LayeredDraw, BooleanSupplier) appends
 * a layer drawing the other's layers while the condition holds, render pushes the pose, draws every layer and pops.
 * 1.21.5 translated z by Z_SEPARATION (200) after each layer so later layers drew over earlier ones; 1.21.6's GUI has no
 * depth (what is submitted later is on top), so the order alone does that and the z step is dropped, as everywhere in
 * this layer's GUI bridge (CbGuiPose, CbGuiFill).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class LayeredDraw {

    /** Z_SEPARATION */
    public static final float field_47848 = 200.0F;

    /** layers (private in 1.21.5; public here: a facade member stands in for the class's, which access wideners and accessors reached) */
    public final java.util.List<Layer> field_47849 = new java.util.ArrayList<>();

    public LayeredDraw() {
    }

    /** add(Layer) */
    public LayeredDraw method_55810(Layer layer) {
        this.field_47849.add(layer);
        return this;
    }

    /** add(LayeredDraw, BooleanSupplier) */
    public LayeredDraw method_55811(LayeredDraw other, java.util.function.BooleanSupplier condition) {
        return this.method_55810((graphics, delta) -> {
            if (condition.getAsBoolean()) {
                other.method_55813(graphics, delta);
            }
        });
    }

    /** render(GuiGraphics, DeltaTracker) */
    public void method_55809(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta) {
        graphics.method_51448().pushMatrix();
        this.method_55813(graphics, delta);
        graphics.method_51448().popMatrix();
    }

    /** renderInner(GuiGraphics, DeltaTracker) (private in 1.21.5; public for the same reason) */
    public void method_55813(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta) {
        for (Layer layer : this.field_47849) {
            layer.render(graphics, delta);
        }
    }

    /** LayeredDraw.Layer (class_9080$class_9081) */
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public interface Layer {
        void render(net.minecraft.class_332 graphics, net.minecraft.class_9779 delta);
    }
}
