package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1's sprite loading API on 1.20.2. 1.20.2 hands every sprite supplier a SpriteOpener (which metadata
 * sections a sprite's resource is read with) where 1.20.1's suppliers took nothing and
 * SpriteLoader.loadSprite read the animation section only. That is 1.20.2's default opener: the one
 * SpriteLoader.loadAndStitch(manager, atlas, mipLevel, executor) uses, from DEFAULT_METADATA_SECTIONS
 * (the animation section alone).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiSprites {

    /** 1.20.2's default opener: sprites read with the animation section, as 1.20.1 read them */
    private static net.minecraft.class_8684 cbDefaultOpener() {
        return net.minecraft.class_8684.create(net.minecraft.class_7766.field_45632);
    }

    /**
     * was net/minecraft/class_7766.method_45829 SpriteLoader.loadSprite(id, resource): the sprite's image and
     * frame size with its animation section, null (logged) when the metadata or image cannot be read or the
     * image is not a whole number of frames
     */
    public static net.minecraft.class_7764 method_45829(net.minecraft.class_2960 id, net.minecraft.class_3298 resource) {
        return cbDefaultOpener().loadSprite(id, resource);
    }

    /**
     * was net/minecraft/class_7766.method_47664 SpriteLoader.runSpriteSuppliers(suppliers, executor): every
     * supplier run on the executor, the contents that are not null in order. 1.20.1 code passes its own
     * Suppliers or the list SpriteResourceLoader.list(manager) answers, which on 1.20.2 holds openers'
     * functions; both run through 1.20.2's runSpriteSuppliers with the default opener.
     */
    public static java.util.concurrent.CompletableFuture<java.util.List<net.minecraft.class_7764>> method_47664(
            java.util.List<?> suppliers, java.util.concurrent.Executor executor) {
        java.util.List<java.util.function.Function<net.minecraft.class_8684, net.minecraft.class_7764>> loaders =
            new java.util.ArrayList<>(suppliers.size());
        for (Object s : suppliers) {
            loaders.add(cbLoader(s));
        }
        return net.minecraft.class_7766.method_47664(cbDefaultOpener(), loaders, executor);
    }

    /** a 1.20.2 loader (every SpriteSupplier is one) as it is; a 1.20.1 Supplier, which takes no opener */
    @SuppressWarnings("unchecked")
    private static java.util.function.Function<net.minecraft.class_8684, net.minecraft.class_7764> cbLoader(Object s) {
        if (s instanceof java.util.function.Function<?, ?> f) {
            return (java.util.function.Function<net.minecraft.class_8684, net.minecraft.class_7764>) f;
        }
        java.util.function.Supplier<?> supplier = (java.util.function.Supplier<?>) s;
        return opener -> (net.minecraft.class_7764) supplier.get();
    }

    /** lambdaAdapters adapt: a 1.20.1 SpriteSupplier lambda, spun as CbGuiLegacySpriteSupplier */
    public static net.minecraft.class_7948.class_7950 cbSpriteSupplier(CbGuiLegacySpriteSupplier legacy) {
        return new CbGuiSpriteSupplierAdapter(legacy);
    }

    /** adapterHooks synth: apply(opener) of a 1.20.1 SpriteSupplier class answers its get() */
    public static Object cbApplyLegacy(CbGuiLegacySpriteSupplier legacy, Object opener) {
        return legacy.get();
    }

    /**
     * instanceRedirects: 1.20.1 SpriteSupplier.get() (Supplier.get through the SpriteSupplier type). A 1.20.1
     * supplier (an adapted lambda, a class given CbGuiLegacySpriteSupplier) answers its own get(); a 1.20.2
     * one is applied to the default opener.
     */
    public static Object get(net.minecraft.class_7948.class_7950 supplier) {
        if (supplier instanceof java.util.function.Supplier<?> s) {
            return s.get();
        }
        return supplier.apply(cbDefaultOpener());
    }

    /**
     * superCtorReshapes conv: 1.20.1 SpriteContents took the animation section itself; 1.20.2 takes the
     * sprite's metadata and reads the animation section from it. The metadata answers that section for the
     * animation serializer and nothing else, as a sprite loaded with the default opener does.
     */
    public static net.minecraft.class_7368 cbAnimationMetadata(net.minecraft.class_1079 animation) {
        return new net.minecraft.class_7368() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> java.util.Optional<T> method_43041(net.minecraft.class_3270<T> serializer) {
                return serializer == net.minecraft.class_1079.field_5337
                    ? java.util.Optional.of((T) animation) : java.util.Optional.empty();
            }
        };
    }

    private CbGuiSprites() {
    }
}
