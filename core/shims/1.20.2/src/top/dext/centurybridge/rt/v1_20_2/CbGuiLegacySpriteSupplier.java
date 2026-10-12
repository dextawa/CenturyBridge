package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 SpriteSource.SpriteSupplier's own shape: a Supplier of SpriteContents. 1.20.2 made the interface a
 * Function of a SpriteOpener (apply(opener)). A 1.20.1 lambda for it is spun as this interface and handed on
 * as a 1.20.2 SpriteSupplier by {@link CbGuiSprites#cbSpriteSupplier} (lambdaAdapters); a 1.20.1 class
 * implementing it gets this interface added and an apply(opener) answering its get()
 * ({@link CbGuiSprites#cbApplyLegacy}, adapterHooks). Either way the object is a Supplier again, as in 1.20.1.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@SuppressWarnings("rawtypes")
public interface CbGuiLegacySpriteSupplier extends java.util.function.Supplier {

    /** 1.20.1 get(): the sprite contents, or null when there are none */
    @Override
    Object get();
}
