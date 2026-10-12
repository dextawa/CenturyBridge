package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1's ClientPackSource(path) on 1.20.2, which also takes the DirectoryValidator that vets symbolic links
 * among the packs it lists (1.20.1 listed them unvetted). A 1.20.1 caller gets the validator 1.20.2's own
 * client pack source is given: the client's, read from the game directory's allowed_symlinks.txt. Before the
 * client has one, the same file is read the same way (LevelStorageSource.parseValidator: no file, or one that
 * cannot be read, allows no links).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiPacks {

    /** superCtorReshapes push: the client's directory validator */
    public static net.minecraft.class_8580 cbDirectoryValidator() {
        net.minecraft.class_310 client = net.minecraft.class_310.method_1551();
        net.minecraft.class_8580 validator = client == null ? null : client.method_52702();
        if (validator != null) {
            return validator;
        }
        return net.minecraft.class_32.method_52235(
            net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("allowed_symlinks.txt"));
    }

    private CbGuiPacks() {
    }
}
