package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11954;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_9.rt.CbBlockEntityRenders;

/** BlockEntityRenderState keeps what 1.21.8's render was handed (CbBlockEntityRenders). */
@Mixin(class_11954.class)
public abstract class CbBlockEntityStateMixin implements CbBlockEntityRenders.Info {

    @Unique
    private class_2586 cb$blockEntity;

    @Unique
    private float cb$partialTick;

    @Unique
    private class_243 cb$cameraPos;

    @Override
    public void cb$legacy(class_2586 blockEntity, float partialTick, class_243 cameraPos) {
        this.cb$blockEntity = blockEntity;
        this.cb$partialTick = partialTick;
        this.cb$cameraPos = cameraPos;
    }

    @Override
    public class_2586 cb$blockEntity() {
        return this.cb$blockEntity;
    }

    @Override
    public float cb$partialTick() {
        return this.cb$partialTick;
    }

    @Override
    public class_243 cb$cameraPos() {
        return this.cb$cameraPos;
    }
}
