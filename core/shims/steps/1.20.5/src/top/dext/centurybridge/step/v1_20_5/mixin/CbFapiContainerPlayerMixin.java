package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container.CbContainerProviderImpl;

/**
 * 1.20.4's fabric-containers-v0 ServerPlayerEntitySyncHook and accessor on 1.20.5's ServerPlayerEntity: the
 * next screen handler sync id (incrementScreenHandlerSyncId, then screenHandlerSyncId) and
 * onScreenHandlerOpened, for opening a 1.20.4 container (CbContainerProviderImpl).
 */
@Mixin(net.minecraft.class_3222.class)
public abstract class CbFapiContainerPlayerMixin implements CbContainerProviderImpl.SyncHook {

    @Shadow
    private int field_13986;

    @Shadow
    protected abstract void method_14237();

    @Shadow
    protected abstract void method_14235(net.minecraft.class_1703 handler);

    @Override
    public int cb$incrementSyncId() {
        method_14237();
        return field_13986;
    }

    @Override
    public void cb$onScreenHandlerOpened(net.minecraft.class_1703 handler) {
        method_14235(handler);
    }
}
