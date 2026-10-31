package top.dext.centurybridge.step.v1_21_11.mixin;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_10232;
import net.minecraft.class_4337;
import net.minecraft.class_4429;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 Realms FileUpload.upload() / cancel() / isFinished() (class_4337.method_20973 / 20970 / 20978), gone from
 * 1.21.11 (javap -c -p both): 1.21.11's FileUpload starts the upload asynchronously (startUpload, method_76241, a
 * CompletableFuture over java.net.http) and is AutoCloseable; RealmsWorldUpload cancels the future and closes it.
 * Given back with 1.21.10's contract: upload() starts the upload once (a second call answers an empty result, as
 * 1.21.10's did), waits for it and closes the client after (1.21.10 had no close: the upload was the whole lifecycle);
 * cancelled before or during, it answers an empty result or throws RealmsUploadCanceledException as 1.21.10's request
 * loop did; any other failure is logged and answers an empty result (status -1), as 1.21.10's loop did. cancel() cancels the running future and closes the client (1.21.10 set a flag its loop polled; 1.21.11's
 * loop polls none). isFinished(): the future is done or cancelled. 1.21.11 never calls these.
 */
@Mixin(value = class_4337.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFileUploadRestoredMixin {
    @Unique
    private CompletableFuture<class_4429> cb$upload;

    @Unique
    private final AtomicBoolean cb$cancelled = new AtomicBoolean();

    /** FileUpload.upload() */
    public class_4429 method_20973() {
        if (this.cb$upload != null) {
            return new class_4429.class_4430().method_21541();
        }
        if (this.cb$cancelled.get()) {
            return new class_4429.class_4430().method_21541();
        }
        class_4337 self = (class_4337) (Object) this;
        this.cb$upload = self.method_76241();
        try {
            return this.cb$upload.join();
        } catch (CancellationException e) {
            throw new class_10232();
        } catch (CompletionException e) {
            if (this.cb$cancelled.get()) {
                throw new class_10232();
            }
            // 1.21.10's request loop logged any other failure and answered the result built so far (status -1)
            org.slf4j.LoggerFactory.getLogger(class_4337.class).error("Caught exception while uploading: ", e.getCause());
            return new class_4429.class_4430().method_21541();
        } finally {
            self.close();
        }
    }

    /** FileUpload.cancel() */
    public void method_20970() {
        this.cb$cancelled.set(true);
        CompletableFuture<class_4429> f = this.cb$upload;
        if (f != null) {
            f.cancel(true);
        }
        ((class_4337) (Object) this).close();
    }

    /** FileUpload.isFinished() */
    public boolean method_20978() {
        CompletableFuture<class_4429> f = this.cb$upload;
        return f != null && (f.isDone() || f.isCancelled());
    }
}
