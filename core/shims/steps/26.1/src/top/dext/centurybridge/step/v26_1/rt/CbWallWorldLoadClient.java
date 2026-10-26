package top.dext.centurybridge.step.v26_1.rt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.worldselection.CreateWorldCallback;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;

/**
 * The client half of 1.21.11's world loading on 26.1 (rt/CbWallWorldLoad holds the WorldData pairing):
 * <ul>
 * <li>CreateWorldCallback.create(screen, registries, levelData, path): 26.1's takes the WorldDataAndGenSettings
 *     and the new world's initial game rules. A 1.21.11 callback (lambda or class) gets the data, which keeps both
 *     (its getGameRules() answers the rules, and handed back to createLevelFromExistingSettings it brings both
 *     back); a 1.21.11 call of a 26.1 callback hands over the pair and rules the data stands for.</li>
 * <li>WorldOpenFlows.createLevelFromExistingSettings(access, resources, registries, worldData): the same pairing
 *     for calls (callReshapes), and for a 1.21.11 subclass's override, which 26.1 calls with the pair.</li>
 * <li>EditWorldScreen.makeBackupAndShowToast(access): 1.21.11's answered once the backup was written (and its toast
 *     shown); 26.1's runs it in the background behind a waiting screen and answers a future. A 1.21.11 caller gets
 *     1.21.11's body: the backup on the calling thread, the same toasts, true when it was written.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallWorldLoadClient {

    private CbWallWorldLoadClient() {
    }

    // ---- CreateWorldCallback ----

    /** 1.21.11's CreateWorldCallback shape */
    @FunctionalInterface
    public interface LegacyCreateWorldCallback {
        boolean create(CreateWorldScreen screen, LayeredRegistryAccess<RegistryLayer> registries, PrimaryLevelData levelData, Path path);
    }

    private static PrimaryLevelData primary(LevelDataAndDimensions.WorldDataAndGenSettings pair, Optional<GameRules> rules) {
        WorldData data = CbWallWorldLoad.data(pair, rules);
        if (data instanceof PrimaryLevelData primary) {
            return primary;
        }
        throw new IllegalStateException(data + ": a 1.21.11 CreateWorldCallback takes a PrimaryLevelData, which every 26.1 world"
            + " creation builds; this world data is " + data.getClass().getName());
    }

    /** a 1.21.11 lambda / method reference as 26.1's callback (lambdaAdapters) */
    public static CreateWorldCallback adapt(LegacyCreateWorldCallback callback) {
        return (screen, registries, pair, rules, path) -> callback.create(screen, registries, primary(pair, rules), path);
    }

    /** 26.1's create on a 1.21.11 class implementing the callback (adapterHooks) */
    public static boolean create(LegacyCreateWorldCallback callback, CreateWorldScreen screen, LayeredRegistryAccess<RegistryLayer> registries,
                                 LevelDataAndDimensions.WorldDataAndGenSettings pair, Optional<GameRules> rules, Path path) {
        return callback.create(screen, registries, primary(pair, rules), path);
    }

    /** a 1.21.11 call of create(screen, registries, levelData, path) on a 26.1 callback (interfaceCallRedirects) */
    public static boolean create(CreateWorldCallback callback, CreateWorldScreen screen, LayeredRegistryAccess<RegistryLayer> registries,
                                 PrimaryLevelData levelData, Path path) {
        return callback.create(screen, registries, CbWallWorldLoad.withGenSettings(registries, levelData),
            CbWallWorldLoad.initialRules(levelData), path);
    }

    // ---- WorldOpenFlows.createLevelFromExistingSettings ----

    /** 1.21.11's WorldOpenFlows.createLevelFromExistingSettings shape, as a 1.21.11 subclass overrides it */
    public interface LegacyWorldOpenFlows {
        void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess access, ReloadableServerResources resources,
                                             LayeredRegistryAccess<RegistryLayer> registries, WorldData worldData);
    }

    /** 26.1's call of the method on a 1.21.11 subclass overriding the old shape (overrideHooks bridge) */
    public static void createLevelFromExistingSettings(LegacyWorldOpenFlows flows, LevelStorageSource.LevelStorageAccess access,
                                                       ReloadableServerResources resources, LayeredRegistryAccess<RegistryLayer> registries,
                                                       LevelDataAndDimensions.WorldDataAndGenSettings pair, Optional<GameRules> rules) {
        flows.createLevelFromExistingSettings(access, resources, registries, CbWallWorldLoad.data(pair, rules));
    }

    // ---- Minecraft.doWorldLoad ----

    /**
     * the initial rules of 1.21.11's doWorldLoad(access, packs, stem, newWorld) entered directly (frameDefaults): the
     * rules the stem's world data stands for (rt/CbWallWorldLoad.initialRules), empty for a world read from disk
     */
    public static Optional<GameRules> doWorldLoadRules(Minecraft minecraft, LevelStorageSource.LevelStorageAccess access, PackRepository packs,
                                                       WorldStem stem, boolean newWorld) {
        return CbWallWorldLoad.initialRules(stem);
    }

    // ---- EditWorldScreen ----

    /** 1.21.11 EditWorldScreen.makeBackupAndShowToast(access) (method_2701), its body on 26.1's members */
    public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess access) {
        long size = 0L;
        IOException failure = null;
        try {
            size = access.makeWorldBackup();
        } catch (IOException e) {
            failure = e;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (failure != null) {
            minecraft.getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP,
                Component.translatable("selectWorld.edit.backupFailed"), Component.literal(failure.getMessage())));
            return false;
        }
        minecraft.getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP,
            Component.translatable("selectWorld.edit.backupCreated", access.getLevelId()),
            Component.translatable("selectWorld.edit.backupSize", Mth.ceil(size / 1048576.0))));
        return true;
    }
}
