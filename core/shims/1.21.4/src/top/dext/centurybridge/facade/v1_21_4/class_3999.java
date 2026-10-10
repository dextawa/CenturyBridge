package top.dext.centurybridge.facade.v1_21_4;

/**
 * Facade for 1.20.1 net/minecraft/class_3999 (ParticleRenderType), an INTERFACE
 * mods implement to define a custom particle render type (begin/end draw calls).
 * On 1.21.4 it became a final record, so a legacy `implements class_3999` fails to
 * load ("implements a class"). This facade re-supplies it as an interface via
 * superRenames so the implementing class loads and the mod's non-particle content
 * works; the two draw hooks are re-declared against the surviving 1.21.4 render
 * types (BufferBuilder class_287 / TextureManager class_1060 / Tesselator class_289).
 *
 * Note: actual particle rendering on a 1.21.4 client drives off the real record
 * type, so a facade-interface instance handed to the particle engine is a client-
 * render degrade (ledgered) pending the client-fixture test; on a server the type
 * is never exercised and the mod loads clean. Member names are intermediary tokens.
 */
public interface class_3999 {

    /** begin(BufferBuilder, TextureManager) */
    void method_18130(net.minecraft.class_287 buffer, net.minecraft.class_1060 textureManager);

    /** end(Tesselator) */
    void method_18131(net.minecraft.class_289 tesselator);
}
