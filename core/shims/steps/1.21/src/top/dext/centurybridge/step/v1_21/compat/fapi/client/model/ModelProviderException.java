package top.dext.centurybridge.step.v1_21.compat.fapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Fabric API 1.20.6's ModelProviderException (fabric-models-v0): a provider's failure. Renamed here. */
@Environment(EnvType.CLIENT)
public class ModelProviderException extends Exception {
    public ModelProviderException(String message) {
        super(message);
    }

    public ModelProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
