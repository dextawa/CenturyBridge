package top.dext.centurybridge.rt.v1_20_2;

import com.google.common.base.Splitter;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import net.minecraft.class_155;
import net.minecraft.class_2561;
import net.minecraft.class_3258;
import net.minecraft.class_3259;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3272;
import net.minecraft.class_3279;
import net.minecraft.class_3281;
import net.minecraft.class_3283;
import net.minecraft.class_3286;
import net.minecraft.class_3288;
import net.minecraft.class_5352;
import net.minecraft.class_7663;
import net.minecraft.class_7670;
import net.minecraft.class_7699;
import net.minecraft.class_8580;
import org.slf4j.Logger;

/**
 * 1.20.1 pack repository API on 1.20.2 (s1misc pack bridge).
 *
 * <p>Symlinks: 1.20.2's pack sources (folder, server, built-in) take a DirectoryValidator and skip
 * entries whose symlinks lead outside the allowed list; 1.20.1's validated nothing. The
 * 1.20.1-shaped sources are given a validator that allows every target, which is what 1.20.1 did.
 *
 * <p>Pack info: 1.20.1's Pack.Info was (description, pack format, features) and computed its
 * compatibility for a pack type when asked; 1.20.2's is (description, compatibility, features,
 * overlays), the compatibility fixed when the info is read for a pack type. The pack format a
 * 1.20.1 info carries is kept per info; an info built or read the 1.20.1 way gets its compatibility
 * computed 1.20.1's way (format against the pack type's current version) for the type it is used
 * with, and carries no overlays (1.20.1 had none).
 */
public final class CbPacks {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    // ---- symlinks

    private static final class_8580 ANY_TARGET = new class_8580(path -> true);

    /** a DirectoryValidator that allows every symlink target, as 1.20.1's pack sources did */
    public static class_8580 noSymlinkValidation() {
        return ANY_TARGET;
    }

    /** new FolderRepositorySource(folder, type, source) */
    public static class_3279 cbNewFolderRepositorySource(Path folder, class_3264 type, class_5352 source) {
        return new class_3279(folder, type, source, ANY_TARGET);
    }

    /** new ServerPacksSource() */
    public static class_3286 cbNewServerPacksSource() {
        return new class_3286(ANY_TARGET);
    }

    /** ServerPacksSource.createPackRepository(datapacksFolder) */
    public static class_3283 method_45286(Path datapacks) {
        return class_3286.method_45286(datapacks, ANY_TARGET);
    }

    /** FolderRepositorySource.discoverPacks(folder, builtin, output), 1.20.1 body */
    public static void method_45271(Path folder, boolean builtin, BiConsumer<Path, class_3288.class_7680> output) throws IOException {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
            for (Path entry : entries) {
                class_3288.class_7680 resources = method_45269(entry, builtin);
                if (resources != null) {
                    output.accept(entry, resources);
                }
            }
        }
    }

    /**
     * FolderRepositorySource.detectPackResources(path, builtin), 1.20.1 body: a directory with a
     * pack.mcmeta, or a .zip file on the default (or a link) file system; anything else is not a
     * pack. 1.20.2's own path / zip suppliers open them.
     */
    public static class_3288.class_7680 method_45269(Path path, boolean builtin) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (NoSuchFileException e) {
            return null;
        } catch (IOException e) {
            LOGGER.warn("Failed to read properties of '{}', ignoring", path, e);
            return null;
        }
        if (attributes.isDirectory() && Files.isRegularFile(path.resolve("pack.mcmeta"))) {
            return new class_3259.class_8619(path, builtin);
        }
        if (attributes.isRegularFile() && path.getFileName().toString().endsWith(".zip")) {
            FileSystem fs = path.getFileSystem();
            if (fs == FileSystems.getDefault() || fs instanceof class_7670) {
                return new class_3258.class_8615(path.toFile(), builtin);
            }
        }
        LOGGER.info("Found non-pack entry '{}', ignoring", path);
        return null;
    }

    // ---- FilePackResources

    /** FilePackResources.SPLITTER (1.20.1): '/', empty strings omitted, at most three parts */
    private static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);

    public static Splitter field_14183() {
        return SPLITTER;
    }

    /** new FilePackResources(name, file, builtin): 1.20.2's zip supplier opens the same pack */
    public static class_3258 cbNewFilePackResources(String name, File file, boolean builtin) {
        return (class_3258) new class_3258.class_8615(file, builtin).method_52424(name);
    }

    private static final MethodHandle SHARED_ZIP;

    static {
        try {
            Class<?> access = Class.forName("net.minecraft.class_3258$class_8616");
            SHARED_ZIP = MethodHandles.privateLookupIn(access, MethodHandles.lookup())
                .findConstructor(access, MethodType.methodType(void.class, File.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * The shared zip access a converted FilePackResources subclass's super(name, file, builtin)
     * hands 1.20.2's constructor (package-private type; the reshape casts it, the layer's access
     * widener opens it).
     */
    public static Object sharedZip(File file) {
        try {
            return SHARED_ZIP.invoke(file);
        } catch (Throwable t) {
            throw CbSuper.rethrow(t);
        }
    }

    /** the prefix of a FilePackResources over the zip's root (1.20.2's overlays use others) */
    public static String rootPrefix() {
        return "";
    }

    // ---- PackMetadataSection

    /** 1.20.1 pack metadata declared no supported-format range */
    public static Optional<?> noSupportedFormats() {
        return Optional.empty();
    }

    // ---- PackCompatibility

    /** PackCompatibility.forFormat(format, type), 1.20.1 body */
    public static class_3281 method_14436(int format, class_3264 type) {
        int current = class_155.method_16673().method_48017(type);
        if (format < current) {
            return class_3281.field_14223;
        }
        if (format > current) {
            return class_3281.field_14220;
        }
        return class_3281.field_14224;
    }

    // ---- Pack.Info

    /** the 1.20.1 pack format of every info CenturyBridge saw made */
    private static final Map<class_3288.class_7679, Integer> FORMAT = Collections.synchronizedMap(new WeakHashMap<>());
    /** infos made the 1.20.1 way: their compatibility is computed for the pack type they are used with */
    private static final Map<class_3288.class_7679, Boolean> LEGACY = Collections.synchronizedMap(new WeakHashMap<>());

    /** a 1.20.2 info read by vanilla: its declared format, seen while it is read (CbPackInfoFormat) */
    private static final ThreadLocal<Integer> DECLARED = new ThreadLocal<>();

    public static void seeDeclaredFormat(int format) {
        DECLARED.set(format);
    }

    public static void claimDeclaredFormat(class_3288.class_7679 info) {
        Integer format = DECLARED.get();
        DECLARED.remove();
        if (format != null && info != null) {
            FORMAT.put(info, format);
        }
    }

    private static class_3288.class_7679 legacyInfo(class_2561 description, int format, class_7699 features) {
        // the compatibility stored here is never what a 1.20.1 caller sees: every 1.20.1 path that
        // turns an info into a pack or a compatibility answer computes it for its pack type
        class_3288.class_7679 info = new class_3288.class_7679(description, class_3281.field_14224, features, List.of());
        FORMAT.put(info, format);
        LEGACY.put(info, Boolean.TRUE);
        return info;
    }

    /** new Pack.Info(description, format, features) */
    public static class_3288.class_7679 cbNewPackInfo(class_2561 description, int format, class_7699 features) {
        return legacyInfo(description, format, features);
    }

    /** Info.format() */
    public static int comp_1000(class_3288.class_7679 info) {
        Integer format = FORMAT.get(info);
        if (format == null) {
            throw new IllegalStateException("CenturyBridge: pack info " + info + " was not read or built where its pack format could be seen");
        }
        return format;
    }

    /** Info.compatibility(type): 1.20.1's for an info made the 1.20.1 way, else the one 1.20.2 judged */
    public static class_3281 method_45277(class_3288.class_7679 info, class_3264 type) {
        Integer format = FORMAT.get(info);
        if (format != null && LEGACY.containsKey(info)) {
            return method_14436(format, type);
        }
        return info.comp_1583();
    }

    /**
     * Pack.readPackInfo(id, resources), 1.20.1 body: the pack's metadata section (missing: no
     * info) and feature flags, read from its primary resources.
     */
    public static class_3288.class_7679 method_45274(String id, class_3288.class_7680 resources) {
        try (class_3262 pack = resources.method_52424(id)) {
            class_3272 meta = pack.method_14407(class_3272.field_14202);
            if (meta == null) {
                LOGGER.warn("Missing metadata in pack {}", id);
                return null;
            }
            class_7663 features = pack.method_14407(class_7663.field_39996);
            class_7699 requested = features != null ? features.comp_994() : class_7699.method_45397();
            return legacyInfo(meta.comp_1580(), meta.comp_1581(), requested);
        } catch (Exception e) {
            LOGGER.warn("Failed to read pack metadata", e);
            return null;
        }
    }

    /**
     * Pack.readMetaAndCreate(id, title, required, resources, info, type, position, fixedPosition,
     * source): 1.20.1 took the pack type to judge the info's compatibility; 1.20.2's info carries it.
     */
    public static class_3288 method_14456(String id, class_2561 title, boolean required, class_3288.class_7680 resources,
                                          class_3288.class_7679 info, class_3264 type, class_3288.class_3289 position,
                                          boolean fixedPosition, class_5352 source) {
        class_3288.class_7679 used = info;
        Integer format = FORMAT.get(info);
        if (format != null && LEGACY.containsKey(info)) {
            used = new class_3288.class_7679(info.comp_999(), method_14436(format, type), info.comp_1001(), info.comp_1584());
            FORMAT.put(used, format);
        }
        return class_3288.method_14456(id, title, required, resources, used, position, fixedPosition, source);
    }

    // ---- Pack.ResourcesSupplier

    /** a 1.20.1 supplier lambda as a 1.20.2 supplier: its open(id) is the pack, primary and full alike */
    public static class_3288.class_7680 resourcesSupplier(CbLegacyResourcesSupplier legacy) {
        return new class_3288.class_7680() {
            @Override
            public class_3262 method_52424(String id) {
                return legacy.open(id);
            }

            @Override
            public class_3262 method_52425(String id, class_3288.class_7679 info) {
                return legacy.open(id);
            }
        };
    }

    /** supplier.open(id) on any supplier: a 1.20.1 one's own, else 1.20.2's primary resources */
    public static class_3262 open(class_3288.class_7680 supplier, String id) {
        if (supplier instanceof CbLegacyResourcesSupplier legacy) {
            return legacy.open(id);
        }
        return supplier.method_52424(id);
    }

    /** 1.20.2 openPrimary(id) of a converted 1.20.1 supplier */
    public static class_3262 openPrimary(CbLegacyResourcesSupplier supplier, String id) {
        return supplier.open(id);
    }

    /** 1.20.2 openFull(id, info) of a converted 1.20.1 supplier: 1.20.1 packs had no overlays */
    public static class_3262 openFull(CbLegacyResourcesSupplier supplier, String id, class_3288.class_7679 info) {
        return supplier.open(id);
    }

    private CbPacks() {
    }
}
