package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

/**
 * A 1.21.4 OutputStateShard a mod built with its own setup / clear runnables (rt/CbRenderTypes.newOutputStateShard):
 * 1.21.5's shard with the main render target as its target, running the mod's runnables at setup / clear as 1.21.4's
 * RenderStateShard did.
 */
@Environment(EnvType.CLIENT)
final class CbOutputStateShard extends net.minecraft.class_4668.class_4678 {
    private final Runnable setup;
    private final Runnable clear;

    CbOutputStateShard(String name, Runnable setup, Runnable clear) {
        super(name, () -> class_310.method_1551().method_1522());
        this.setup = setup;
        this.clear = clear;
    }

    @Override
    public void method_23516() {
        this.setup.run();
    }

    @Override
    public void method_23518() {
        this.clear.run();
    }
}
