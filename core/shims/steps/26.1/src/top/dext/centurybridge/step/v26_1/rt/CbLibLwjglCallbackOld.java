package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.libffi.FFICIF;

/**
 * LWJGL 3.3.3's CallbackI.getCallInterface(), for a 1.21.11 class implementing CallbackI itself: the wiring's adapter hook adds
 * this interface to it (its own getCallInterface implements it) and synthesizes 3.4.1's getDescriptor() through
 * CbLibLwjglMisc.getDescriptor.
 */
@Environment(EnvType.CLIENT)
public interface CbLibLwjglCallbackOld {
    FFICIF getCallInterface();
}
