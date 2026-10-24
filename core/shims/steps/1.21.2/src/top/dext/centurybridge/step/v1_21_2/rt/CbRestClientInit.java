package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** s7rest's client entrypoint: receivers of the payloads its 1.21.1 APIs need */
@Environment(EnvType.CLIENT)
public final class CbRestClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CbRestRecipesClient.init();
        CbRestAttachmentSyncClient.init();
        CbRestRecipeBookClient.init();
    }
}
