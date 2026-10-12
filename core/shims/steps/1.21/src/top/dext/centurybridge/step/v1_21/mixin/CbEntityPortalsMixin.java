package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import java.util.Optional;
import net.minecraft.class_243;
import net.minecraft.class_9797;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPortals;

/**
 * Entity: 1.20.6's portalEntrancePos (1.21 keeps the entry position only while its PortalProcessor lives; 1.20.6
 * kept the last nether portal entrance, which findDimensionEntryPoint and getExitPortal read), recorded where
 * 1.20.6's handleInsidePortal recorded it -- a nether portal, not on cooldown, server side, a new position --
 * and handlePortal() for the layer's statics (CbEntityPortals).
 */
@Mixin(class_1297.class)
public abstract class CbEntityPortalsMixin implements CbEntityPortals.EntityPortalState {

    @Unique
    private class_2338 cb$portalEntrancePos;

    @Shadow
    protected abstract void method_60698();

    @Shadow
    public abstract boolean method_30230();

    @Shadow
    public abstract class_1937 method_37908();

    @Override
    public class_2338 cb$portalEntrancePos() {
        return this.cb$portalEntrancePos;
    }

    @Override
    public void cb$setPortalEntrancePos(class_2338 pos) {
        this.cb$portalEntrancePos = pos;
    }

    @Override
    public void cb$handlePortal() {
        this.method_60698();
    }

    /**
     * 1.20.6 getPassengerClosestTo(pos) (a cover; 1.21 dropped it with the wind charge's use of it): the passenger
     * nearest the position, none when there are none.
     */
    public Optional<class_1297> method_59657(class_243 pos) {
        return ((class_1297) (Object) this).method_5685().stream()
            .filter(p -> p != (Object) this)
            .min(java.util.Comparator.comparingDouble(p -> p.method_19538().method_1025(pos)));
    }

    @Inject(method = "method_60697(Lnet/minecraft/class_9797;Lnet/minecraft/class_2338;)V", at = @At("HEAD"))
    private void cb$recordEntrance(class_9797 portal, class_2338 pos, CallbackInfo ci) {
        if (portal == class_2246.field_10316 && !this.method_30230() && !this.method_37908().field_9236
                && !pos.equals(this.cb$portalEntrancePos)) {
            this.cb$portalEntrancePos = pos.method_10062();
        }
    }
}
