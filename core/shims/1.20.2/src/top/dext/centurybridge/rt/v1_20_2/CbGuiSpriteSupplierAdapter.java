package top.dext.centurybridge.rt.v1_20_2;

/**
 * A 1.20.1 SpriteSupplier lambda as 1.20.2's SpriteSupplier: apply(opener) answers the lambda's get(), the
 * opener unused as the 1.20.1 lambda had none; it stays a Supplier for 1.20.1 code holding it as one.
 * discard() is the interface's default, as a 1.20.1 lambda's was.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
final class CbGuiSpriteSupplierAdapter
        implements net.minecraft.class_7948.class_7950, java.util.function.Supplier<net.minecraft.class_7764> {

    private final CbGuiLegacySpriteSupplier legacy;

    CbGuiSpriteSupplierAdapter(CbGuiLegacySpriteSupplier legacy) {
        this.legacy = legacy;
    }

    @Override
    public net.minecraft.class_7764 apply(net.minecraft.class_8684 opener) {
        return (net.minecraft.class_7764) this.legacy.get();
    }

    @Override
    public net.minecraft.class_7764 get() {
        return (net.minecraft.class_7764) this.legacy.get();
    }
}
