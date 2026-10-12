package top.dext.centurybridge.step.v1_20_3.mixin;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbPacks;

/**
 * DownloadedPackSource's 1.20.2 surface on 1.20.3 ({@link CbPacks}): the single "server" pack slot, its
 * methods (downloadAndSelectResourcePack, setServerPack, loadBundledResourcePack, clearServerPack) and the
 * RepositorySource the class was in 1.20.2 (loadPacks offers the slot's pack). The repository source
 * 1.20.3's Minecraft registers (createRepositorySource) offers it too, after 1.20.3's own packs.
 */
@Mixin(net.minecraft.class_1066.class)
public abstract class CbDownloadedPackSourceMixin implements net.minecraft.class_3285, CbPacks.CbLegacyPackSlot {

    @Unique
    private final CbPacks.Slot cb$slot = new CbPacks.Slot();

    @Override
    public CbPacks.Slot cb$slot() {
        return cb$slot;
    }

    private net.minecraft.class_1066 cb$self() {
        return (net.minecraft.class_1066) (Object) this;
    }

    @Override
    public void method_14453(Consumer<net.minecraft.class_3288> consumer) {
        CbPacks.loadPacks(cb$self(), consumer);
    }

    public CompletableFuture<?> method_4640(URL url, String hash, boolean required) {
        return CbPacks.download(cb$self(), url, hash, required);
    }

    @Override
    public CompletableFuture<Void> method_4642() {
        return CbPacks.clearServerPack(cb$self());
    }

    @Override
    public CompletableFuture<Void> method_4638(File file, net.minecraft.class_5352 source) {
        return CbPacks.setServerPack(cb$self(), file, source);
    }

    public CompletableFuture<Void> method_43339(net.minecraft.class_32.class_5143 level) {
        return CbPacks.loadBundledResourcePack(cb$self(), level);
    }

    @Inject(method = "method_55514", at = @At("RETURN"), cancellable = true)
    private void cb$offerSlot(CallbackInfoReturnable<net.minecraft.class_3285> cir) {
        cir.setReturnValue(CbPacks.cbWithSlot(cb$self(), cir.getReturnValue()));
    }
}
