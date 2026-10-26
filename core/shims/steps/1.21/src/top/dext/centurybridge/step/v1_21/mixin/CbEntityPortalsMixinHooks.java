package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_9797;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * The injector into 1.21's own Entity.setAsInsidePortal that goes with {@link CbEntityPortalsMixin} (its restored
 * 1.20.6 members stay there, at {@code CbVanillaReads.PRIORITY}): 1.20.6's portalEntrancePos recorded where 1.20.6's
 * handleInsidePortal recorded it -- a nether portal, not on cooldown, server side, a new position.
 */
@Mixin(class_1297.class)
public abstract class CbEntityPortalsMixinHooks {

    @Shadow
    public abstract boolean method_30230();

    @Shadow
    public abstract class_1937 method_37908();

    @Inject(method = "method_60697(Lnet/minecraft/class_9797;Lnet/minecraft/class_2338;)V", at = @At("HEAD"))
    private void cb$recordEntrance(class_9797 portal, class_2338 pos, CallbackInfo ci) {
        CbEntityPortals.EntityPortalState state = (CbEntityPortals.EntityPortalState) (Object) this;
        if (portal == class_2246.field_10316 && !this.method_30230() && !this.method_37908().field_9236
                && !pos.equals(state.cb$portalEntrancePos())) {
            state.cb$setPortalEntrancePos(pos.method_10062());
        }
    }
}
