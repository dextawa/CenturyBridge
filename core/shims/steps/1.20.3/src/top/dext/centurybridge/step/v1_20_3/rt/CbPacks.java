package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.common.hash.Hashing;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.20.2's DownloadedPackSource on 1.20.3. 1.20.2 kept ONE server resource pack, the "server" pack it
 * offered as a RepositorySource: downloadAndSelectResourcePack(url, hash, required) downloaded it (with a
 * progress screen) into its directory and selected it, setServerPack(file, source) selected a local one,
 * loadBundledResourcePack(level) the level's resources.zip, clearServerPack() dropped it -- each answering
 * the resource reload it started. 1.20.3 replaced all of that by a queue of UUID-keyed packs under the
 * server's control, which still runs as before; this is the 1.20.2 slot, kept on each DownloadedPackSource
 * (the mixin), with 1.20.2's code ported onto the 1.20.3 pack, screen and HTTP API.
 */
@Environment(EnvType.CLIENT)
public final class CbPacks {

    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/DownloadedPackSource");
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private static final int MAX_PACK_SIZE_BYTES = 262144000;
    private static final int MAX_KEPT_PACKS = 10;
    private static final String SERVER_ID = "server";

    /** the 1.20.2 slot a DownloadedPackSource carries (the mixin) */
    public static final class Slot {
        final ReentrantLock lock = new ReentrantLock();
        File dir;
        CompletableFuture<?> currentDownload;
        net.minecraft.class_3288 serverPack;
    }

    /**
     * what the mixin gives DownloadedPackSource: the slot, and the 1.20.2 methods the slot's code calls on
     * the source itself (virtually, as 1.20.2's did, so a subclass's override is reached)
     */
    public interface CbLegacyPackSlot {
        Slot cb$slot();

        /** clearServerPack() */
        CompletableFuture<Void> method_4642();

        /** setServerPack(file, source) */
        CompletableFuture<Void> method_4638(File file, net.minecraft.class_5352 source);
    }

    private static CbLegacyPackSlot ops(net.minecraft.class_1066 source) {
        return (CbLegacyPackSlot) source;
    }

    private static Slot slot(net.minecraft.class_1066 source) {
        Slot s = ((CbLegacyPackSlot) source).cb$slot();
        if (s.dir == null) {
            // the instance 1.20.3's Minecraft made: 1.20.2's Minecraft gave its pack source this directory
            s.dir = new File(net.minecraft.class_310.method_1551().field_1697, "server-resource-packs");
        }
        return s;
    }

    // ------------------------------------------------------------------ construction

    /**
     * new DownloadedPackSource(File serverPackDir) of 1.20.2. 1.20.3's constructor reads the user and the
     * proxy from the user data (for its download queue) and opens that queue in the directory it is given --
     * a log.json and a vacuum to its 20 newest entries; the 1.20.2 directory holds the 1.20.2 slot's packs
     * under 1.20.2's own cleanup, so the queue, which a 1.20.2 caller never uses, gets a directory inside it.
     */
    public static net.minecraft.class_542.class_547 cbUserData() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return new net.minecraft.class_542.class_547(mc.method_1548(), new PropertyMap(), new PropertyMap(), mc.method_1487());
    }

    public static java.nio.file.Path cbQueueDir(File serverPackDir) {
        return serverPackDir.toPath().resolve("centurybridge-1.20.3-queue");
    }

    public static void cbPackSourceBuilt(net.minecraft.class_1066 source, File serverPackDir) {
        ((CbLegacyPackSlot) source).cb$slot().dir = serverPackDir;
    }

    // ------------------------------------------------------------------ RepositorySource

    /** loadPacks(Consumer) (method_14453): the slot's pack, when there is one */
    public static void loadPacks(net.minecraft.class_1066 source, Consumer<net.minecraft.class_3288> consumer) {
        net.minecraft.class_3288 pack = slot(source).serverPack;
        if (pack != null) {
            consumer.accept(pack);
        }
    }

    // ------------------------------------------------------------------ the 1.20.2 methods

    /** getDownloadHeaders(), 1.20.2's */
    private static Map<String, String> downloadHeaders() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_6489 version = net.minecraft.class_155.method_16673();
        return Map.of(
            "X-Minecraft-Username", mc.method_1548().method_1676(),
            "X-Minecraft-UUID", com.mojang.util.UndashedUuid.toString(mc.method_1548().method_44717()),
            "X-Minecraft-Version", version.method_48019(),
            "X-Minecraft-Version-ID", version.method_48018(),
            "X-Minecraft-Pack-Format", String.valueOf(version.method_48017(net.minecraft.class_3264.field_14188)),
            "User-Agent", "Minecraft Java/" + version.method_48019());
    }

    /** downloadAndSelectResourcePack(url, hash, required) (method_4640), 1.20.2's body */
    public static CompletableFuture<?> download(net.minecraft.class_1066 source, URL url, String hash, boolean required) {
        Slot s = slot(source);
        String name = Hashing.sha1().hashString(url.toString(), StandardCharsets.UTF_8).toString();
        String expected = SHA1.matcher(hash).matches() ? hash : "";
        s.lock.lock();
        try {
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            File file = new File(s.dir, name);
            CompletableFuture<?> download;
            if (file.exists()) {
                download = CompletableFuture.completedFuture("");
            } else {
                net.minecraft.class_435 progress = new net.minecraft.class_435(required);
                Map<String, String> headers = downloadHeaders();
                mc.method_19537(() -> mc.method_1507(progress));
                download = CbUtil.method_15301(file, url, headers, MAX_PACK_SIZE_BYTES, progress, mc.method_1487());
            }
            s.currentDownload = download
                .thenCompose(ignored -> {
                    if (!checkHash(expected, file)) {
                        return CompletableFuture.failedFuture(new RuntimeException("Hash check failure for file " + file + ", see log"));
                    }
                    mc.execute(() -> {
                        if (!required) {
                            mc.method_1507(new net.minecraft.class_424(net.minecraft.class_2561.method_43471("multiplayer.applyingPack")));
                        }
                    });
                    return ops(source).method_4638(file, net.minecraft.class_5352.field_25350);
                })
                .exceptionallyCompose(t -> ops(source).method_4642()
                    .thenAcceptAsync(v -> {
                        LOGGER.warn("Pack application failed: {}, deleting file {}", t.getMessage(), file);
                        delete(file);
                    }, net.minecraft.class_156.method_27958())
                    .thenAcceptAsync(v -> mc.method_1507(new net.minecraft.class_410(proceed -> {
                        if (proceed) {
                            mc.method_1507(null);
                        } else {
                            net.minecraft.class_634 listener = mc.method_1562();
                            if (listener != null) {
                                listener.method_48296().method_10747(net.minecraft.class_2561.method_43471("connect.aborted"));
                            }
                        }
                    }, net.minecraft.class_2561.method_43471("multiplayer.texturePrompt.failure.line1"),
                        net.minecraft.class_2561.method_43471("multiplayer.texturePrompt.failure.line2"),
                        net.minecraft.class_5244.field_24338, net.minecraft.class_2561.method_43471("menu.disconnect"))), mc))
                .thenAcceptAsync(v -> clearOldDownloads(s), net.minecraft.class_156.method_27958());
            return s.currentDownload;
        } finally {
            s.lock.unlock();
        }
    }

    private static void delete(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            LOGGER.warn("Failed to delete file {}: {}", file, e.getMessage());
        }
    }

    /** clearServerPack() (method_4642), 1.20.2's body */
    public static CompletableFuture<Void> clearServerPack(net.minecraft.class_1066 source) {
        Slot s = slot(source);
        s.lock.lock();
        try {
            if (s.currentDownload != null) {
                s.currentDownload.cancel(true);
            }
            s.currentDownload = null;
            if (s.serverPack != null) {
                s.serverPack = null;
                return net.minecraft.class_310.method_1551().method_1513();
            }
        } finally {
            s.lock.unlock();
        }
        return CompletableFuture.completedFuture(null);
    }

    /** checkHash(expected, file), 1.20.2's */
    private static boolean checkHash(String expected, File file) {
        try {
            String actual = com.google.common.io.Files.asByteSource(file).hash(Hashing.sha1()).toString();
            if (expected.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", file);
                return true;
            }
            if (actual.toLowerCase(Locale.ROOT).equals(expected.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", file, expected);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", file, expected, actual);
        } catch (IOException e) {
            LOGGER.warn("File {} couldn't be hashed.", file, e);
        }
        return false;
    }

    /** clearOldDownloads(), 1.20.2's: the slot's directory keeps its ten newest files */
    private static void clearOldDownloads(Slot s) {
        if (!s.dir.isDirectory()) {
            return;
        }
        try {
            List<File> files = new ArrayList<>(org.apache.commons.io.FileUtils.listFiles(
                s.dir, org.apache.commons.io.filefilter.TrueFileFilter.TRUE, null));
            files.sort(org.apache.commons.io.comparator.LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int i = 0;
            for (File f : files) {
                if (i++ >= MAX_KEPT_PACKS) {
                    LOGGER.info("Deleting old server resource pack {}", f.getName());
                    org.apache.commons.io.FileUtils.deleteQuietly(f);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while deleting old server resource pack : {}", e.getMessage());
        }
    }

    /** setServerPack(file, source) (method_4638), 1.20.2's body */
    public static CompletableFuture<Void> setServerPack(net.minecraft.class_1066 source, File file, net.minecraft.class_5352 packSource) {
        net.minecraft.class_3288.class_7680 resources = new net.minecraft.class_3258.class_8615(file, false);
        int format = net.minecraft.class_155.method_16673().method_48017(net.minecraft.class_3264.field_14188);
        net.minecraft.class_3288.class_7679 info = net.minecraft.class_3288.method_45274(SERVER_ID, resources, format);
        if (info == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid pack metadata at " + file));
        }
        LOGGER.info("Applying server pack {}", file);
        slot(source).serverPack = net.minecraft.class_3288.method_14456(SERVER_ID,
            net.minecraft.class_2561.method_43471("resourcePack.server.name"), true, resources, info,
            net.minecraft.class_3288.class_3289.field_14280, true, packSource);
        return net.minecraft.class_310.method_1551().method_1513();
    }

    /** loadBundledResourcePack(level) (method_43339), 1.20.2's body: the level's resources.zip */
    public static CompletableFuture<Void> loadBundledResourcePack(net.minecraft.class_1066 source, net.minecraft.class_32.class_5143 level) {
        java.nio.file.Path path = level.method_27010(net.minecraft.class_5218.field_24187);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            return ops(source).method_4638(path.toFile(), net.minecraft.class_5352.field_25349);
        }
        return CompletableFuture.completedFuture(null);
    }

    /** the repository source 1.20.3's Minecraft registers, offering the 1.20.2 slot's pack too */
    public static net.minecraft.class_3285 cbWithSlot(net.minecraft.class_1066 source, net.minecraft.class_3285 queued) {
        return consumer -> {
            queued.method_14453(consumer);
            ((net.minecraft.class_3285) source).method_14453(consumer);
        };
    }

    private CbPacks() {
    }
}
