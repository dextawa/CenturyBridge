package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.RealmsObjectSelectionList;
import top.dext.centurybridge.step.v1_21_2.facade.WorldGenerationInfo;

/**
 * 1.21.1's Realms client API on 1.21.2.
 *
 * 1.21.2 removed regenerating a Realms world from a seed (and creating a snapshot realm from one): the
 * screen, the tasks and RealmsClient.resetWorldWithSeed are gone; a world is reset from a template only.
 * The screen and tasks are back as facades so 1.21.1 classes using them load and the screen works, but the
 * four members that would send the seed-reset or snapshot-creation requests answer with
 * {@link #SEED_RESET_REMOVED} instead (RealmsClient.resetWorldWithSeed, ResettingGeneratedWorldTask's
 * request, CreateSnapshotRealmTask.run, RealmsCreateRealmScreen(lastScreen, parentWorldId)): CenturyBridge
 * does not replay a request to an online service whose acceptance by that service it cannot verify
 * (ledgered with this reason).
 */
@Environment(EnvType.CLIENT)
public final class CbRestRealms {

    private CbRestRealms() {
    }

    /** the message every removed seed-reset / snapshot request answers with */
    public static final String SEED_RESET_REMOVED = "Regenerating a Realms world from a seed (and creating a snapshot realm"
        + " from one) was removed from Minecraft 1.21.2; CenturyBridge does not send that request to the Realms service"
        + " for a 1.21.1 mod. Reset the world from a template instead.";

    /** the removed request as the Realms service error the Realms screens show */
    public static net.minecraft.class_4355 seedResetRemoved() {
        return new net.minecraft.class_4355(new net.minecraft.class_4345.class_8653(-1, net.minecraft.class_2561.method_43470(SEED_RESET_REMOVED)));
    }

    /** RealmsClient.resetWorldWithSeed(worldId, info): the removed request (see above) */
    public static Boolean resetWorldWithSeed(net.minecraft.class_4341 client, long worldId, WorldGenerationInfo info)
            throws net.minecraft.class_4355 {
        throw seedResetRemoved();
    }

    /** new RealmsCreateRealmScreen(lastScreen, parentWorldId): the snapshot-creation screen, whose only action is the removed request */
    public static net.minecraft.class_4390 newCreateSnapshotRealmScreen(net.minecraft.class_4325 lastScreen, long parentWorldId) {
        throw new UnsupportedOperationException(SEED_RESET_REMOVED);
    }

    /**
     * new RealmsCreateRealmScreen(lastScreen, server): creating the realm of a new (uninitialised) server,
     * not a snapshot -- 1.21.2's constructor with snapshot false
     */
    public static boolean notSnapshot() {
        return false;
    }

    /**
     * RealmsClient.requestUploadInfo(worldId, token): 1.21.2 reads the token from the upload token cache
     * itself; the given token is put there first, so the request carries the token 1.21.1 sent
     */
    public static net.minecraft.class_4888 requestUploadInfo(net.minecraft.class_4341 client, long worldId, String token)
            throws net.minecraft.class_4355 {
        net.minecraft.class_4453.method_21586(worldId, token);
        return client.method_21026(worldId);
    }

    /**
     * FileUpload.upload(callback): 1.21.1 ran the upload on another thread and handed its result to the
     * callback; 1.21.2's upload() runs it and answers the result. Run on another thread, the result to the
     * callback.
     */
    public static void upload(net.minecraft.class_4337 upload, java.util.function.Consumer<net.minecraft.class_4429> callback) {
        java.util.concurrent.CompletableFuture.supplyAsync(upload::method_20973).thenAccept(callback);
    }

    /** a 1.21.1 RealmsClient subclass overriding requestUploadInfo(worldId, token) (bridge adapter) */
    public interface LegacyUploadInfo {
        net.minecraft.class_4888 method_21026(long worldId, String token) throws net.minecraft.class_4355;
    }

    /** super's 1.21.2 requestUploadInfo(worldId), from a 1.21.1 subclass's super call */
    public interface UploadInfoSuper {
        net.minecraft.class_4888 cb$super$method_21026(long worldId) throws net.minecraft.class_4355;
    }

    /** 1.21.2's requestUploadInfo(worldId) of a 1.21.1 subclass: its override, with the token 1.21.1's callers passed (the cached one) */
    public static net.minecraft.class_4888 legacyUploadInfo(LegacyUploadInfo client, long worldId) throws net.minecraft.class_4355 {
        return client.method_21026(worldId, net.minecraft.class_4453.method_21585(worldId));
    }

    /** a 1.21.1 subclass's super.requestUploadInfo(worldId, token): the token cached, then 1.21.2's request (as requestUploadInfo) */
    public static net.minecraft.class_4888 superUploadInfo(UploadInfoSuper client, long worldId, String token) throws net.minecraft.class_4355 {
        net.minecraft.class_4453.method_21586(worldId, token);
        return client.cb$super$method_21026(worldId);
    }

    /** a 1.21.1 FileUpload subclass overriding upload(callback) (bridge adapter) */
    public interface LegacyUpload {
        void method_20973(java.util.function.Consumer<net.minecraft.class_4429> callback);
    }

    /** super's 1.21.2 upload(), from a 1.21.1 subclass's super.upload(callback) */
    public interface UploadSuper {
        net.minecraft.class_4429 cb$super$method_20973();
    }

    /** 1.21.2's upload() of a 1.21.1 subclass: its upload(callback), the result it hands the callback answered when it comes */
    public static net.minecraft.class_4429 legacyUpload(LegacyUpload upload) {
        java.util.concurrent.CompletableFuture<net.minecraft.class_4429> result = new java.util.concurrent.CompletableFuture<>();
        upload.method_20973(result::complete);
        return result.join();
    }

    /** a 1.21.1 subclass's super.upload(callback): 1.21.2's upload on another thread, its result to the callback */
    public static void superUpload(UploadSuper upload, java.util.function.Consumer<net.minecraft.class_4429> callback) {
        java.util.concurrent.CompletableFuture.supplyAsync(upload::cb$super$method_20973).thenAccept(callback);
    }

    // ------------------------------------------------------------------ RealmsWorldOptions (class_4883)

    /** RealmsWorldOptions.spawnAnimals: 1.21.2's realm options no longer have it; true, 1.21.1's value when the service sent none */
    public static boolean spawnAnimals(net.minecraft.class_4883 options) {
        return true;
    }

    /** RealmsWorldOptions.spawnNPCs: as spawnAnimals */
    public static boolean spawnNpcs(net.minecraft.class_4883 options) {
        return true;
    }

    /** a 1.21.1 RealmsWorldOptions is not hardcore: 1.21.1's options had no hardcore flag */
    public static boolean notHardcore() {
        return false;
    }

    /** RealmsWorldOptions.parse(json): 1.21.2 also takes the realm's settings, which for a 1.21.1 caller say not hardcore */
    public static net.minecraft.class_4883 method_25078(com.google.gson.JsonObject json) {
        return net.minecraft.class_4883.method_25078(json, new net.minecraft.class_10327(false));
    }

    // ------------------------------------------------------------------ RowButton

    /** RowButton.drawButtonsInRow(graphics, buttons, list, ...): 1.21.2 takes any selection list */
    public static void method_21113(net.minecraft.class_332 graphics, java.util.List<net.minecraft.class_4371> buttons,
            RealmsObjectSelectionList<?> list, int x, int y, int mouseX, int mouseY) {
        net.minecraft.class_4371.method_21113(graphics, buttons, list, x, y, mouseX, mouseY);
    }

    /** RowButton.rowButtonMouseClicked(list, entry, buttons, button, mouseX, mouseY) */
    public static void method_21114(RealmsObjectSelectionList<?> list, net.minecraft.class_4280.class_4281<?> entry,
            java.util.List<net.minecraft.class_4371> buttons, int button, double mouseX, double mouseY) {
        net.minecraft.class_4371.method_21114(list, entry, buttons, button, mouseX, mouseY);
    }
}
