package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10442;
import net.minecraft.class_327;
import net.minecraft.class_5599;
import net.minecraft.class_5614;
import net.minecraft.class_776;
import net.minecraft.class_824;
import net.minecraft.class_898;
import net.minecraft.class_918;

/**
 * BlockEntityRendererProvider.Context as 1.21.8 code builds it. 1.21.9 made it a record with two more components, the
 * MaterialSet and the PlayerSkinRenderCache, which 1.21.9's BlockEntityRenderDispatcher hands to every context it
 * builds from its own fields (javap: 1.21.9 BlockEntityRenderDispatcher.onResourceManagerReload); a 1.21.8 context
 * gets the ones of the dispatcher it was given. The accessors are the record's (methodRenames).
 */
@Environment(EnvType.CLIENT)
public final class CbBerContext {

    private CbBerContext() {
    }

    /** new BlockEntityRendererProvider.Context(dispatcher, blockRenderer, itemModelResolver, itemRenderer, entityRenderer, modelSet, font) */
    public static class_5614.class_5615 context(class_824 dispatcher, class_776 blockRenderer, class_10442 itemModelResolver,
                                                class_918 itemRenderer, class_898 entityRenderer, class_5599 modelSet, class_327 font) {
        return new class_5614.class_5615(dispatcher, blockRenderer, itemModelResolver, itemRenderer, entityRenderer, modelSet, font,
            dispatcher.field_61783, dispatcher.field_62250);
    }
}
