package top.dext.centurybridge.shims.v1_20_4;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 gave Element.mouseScrolled a horizontal-amount parameter (DDD -> DDDD). MouseScrollBridge
 * restores the three-arg shape as an interface default on class_364, which the JVM resolves for every
 * implementor -- but the build gate walks only superclass chains, so the concrete GUI classes the
 * corpus calls through (Screen, HandledScreen, widgets, lists) each need the shape declared on the
 * class itself. One multi-target mixin does that for all of them; every target implements class_364,
 * so the body simply delegates through the interface.
 */
@Mixin({
    net.minecraft.class_339.class,   // ClickableWidget
    net.minecraft.class_342.class,   // TextFieldWidget
    net.minecraft.class_362.class,   // AbstractParentElement
    net.minecraft.class_4265.class,  // EntryListWidget
    net.minecraft.class_437.class,   // Screen
    net.minecraft.class_465.class,   // HandledScreen
    net.minecraft.class_8208.class   // ImageWidget
})
public abstract class MouseScrollSubclassBridge {

    public boolean method_25401(double mouseX, double mouseY, double amount) {
        return ((net.minecraft.class_364) (Object) this).method_25401(mouseX, mouseY, 0.0D, amount);
    }
}
