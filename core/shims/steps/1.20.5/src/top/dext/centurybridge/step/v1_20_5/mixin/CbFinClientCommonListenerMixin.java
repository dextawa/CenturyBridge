package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * ClientCommonPacketListenerImpl: 1.20.4's protected abstract registryAccess(), back for a subclass's
 * call (1.20.5 dropped it from the common listener). Every vanilla listener answers its own --
 * ClientPacketListener's public one, ClientConfigurationPacketListenerImpl's (CbFinClientConfigListenerMixin)
 * -- and a 1.20.4 subclass implemented it, as 1.20.4 required; this body stands for the abstract
 * declaration and is reached by none of them.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8673.class)
public abstract class CbFinClientCommonListenerMixin {

    /** was registryAccess() (abstract) */
    protected net.minecraft.class_5455.class_6890 method_29091() {
        throw new AbstractMethodError(this.getClass().getName() + ".registryAccess() (abstract in 1.20.4's ClientCommonPacketListenerImpl)");
    }
}
