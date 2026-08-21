package top.dext.centurybridge.compat.fapi;

import java.util.function.Consumer;

/** Stand-in for fabric-api's removed ExtraModelProvider (fabric-models-v0). */
public interface CbExtraModelProvider {
    void provideExtraModels(net.minecraft.server.packs.resources.ResourceManager manager,
                            Consumer<net.minecraft.resources.Identifier> out);
}
