package top.dext.centurybridge.step.v26_1.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.facade.ContainerListener;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's SimpleContainer.addListener / removeListener, given back (covers): the listeners are told, in the
 * order added, each time the container changes (setChanged, mixin/CbWallSimpleContainerReadsMixin), as 1.21.11's were.
 */
@Mixin(value = SimpleContainer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallSimpleContainerMixin implements CbWallLegacyCalls.ContainerListenersLegacy {

    @Unique
    private List<ContainerListener> cb$listeners;

    public void addListener(ContainerListener listener) {
        if (this.cb$listeners == null) {
            this.cb$listeners = new ArrayList<>();
        }
        this.cb$listeners.add(listener);
    }

    public void removeListener(ContainerListener listener) {
        if (this.cb$listeners != null) {
            this.cb$listeners.remove(listener);
        }
    }

    @Override
    public void cb$notifyListeners() {
        if (this.cb$listeners != null) {
            for (ContainerListener listener : this.cb$listeners) {
                listener.method_5453((Container) (Object) this);
            }
        }
    }
}
