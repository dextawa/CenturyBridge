package top.dext.centurybridge.step.v1_20_3.rt;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * 1.20.2 utility API (Util, StringUtil, NbtIo, HttpUtil, ExtraCodecs, CommonLinks, BuiltInRegistries,
 * CauldronInteraction) on 1.20.3. Each static here is the target of a redirect keyed on the
 * 1.20.2 member of the same name, and does what 1.20.2's body did, built from 1.20.3's API.
 */
public final class CbUtil {

    private static final Logger LOGGER = LogUtils.getLogger();

    // ------------------------------------------------------------------ Util (class_156)

    /** Util.IdentityStrategy (class_156$class_157, gone in 1.20.3): identity hash, reference equality. */
    public enum IdentityStrategy implements Hash.Strategy<Object> {
        INSTANCE;

        @Override
        public int hashCode(Object o) {
            return System.identityHashCode(o);
        }

        @Override
        public boolean equals(Object a, Object b) {
            return a == b;
        }
    }

    /** was Util.identityStrategy() (method_655) */
    @SuppressWarnings("unchecked")
    public static <K> Hash.Strategy<K> method_655() {
        return (Hash.Strategy<K>) IdentityStrategy.INSTANCE;
    }

    /** was Util.safeReplaceFile(File, File, File) (method_27760): the Path overload on the files' paths */
    public static void method_27760(File current, File latest, File oldBackup) {
        net.minecraft.class_156.method_30626(current.toPath(), latest.toPath(), oldBackup.toPath());
    }

    /** was Util.safeReplaceOrMoveFile(File, File, File, boolean) (method_41203); 1.20.2 returned nothing */
    public static void method_41203(File current, File latest, File oldBackup, boolean restore) {
        net.minecraft.class_156.method_41204(current.toPath(), latest.toPath(), oldBackup.toPath(), restore);
    }

    /**
     * Util.shuffle(ObjectArrayList, RandomSource) became shuffle(List, RandomSource) with the same
     * algorithm: the call passes the same list, seen as the List it is.
     */
    public static <T> List<T> cbAsList(it.unimi.dsi.fastutil.objects.ObjectArrayList<T> list) {
        return list;
    }

    /** was Util.createIndexLookup(List, IntFunction) (method_44146): each element's index, in a map the factory sizes */
    public static <T> ToIntFunction<T> method_44146(List<T> values, IntFunction<Object2IntMap<T>> mapFactory) {
        Object2IntMap<T> map = mapFactory.apply(values.size());
        for (int i = 0; i < values.size(); i++) {
            map.put(values.get(i), i);
        }
        return map;
    }

    // ------------------------------------------------------------------ StringUtil (class_3544)

    /** was StringUtil.formatTickDuration(int) (method_15439(I)): 20 ticks a second, as 1.20.2 counted */
    public static String method_15439(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        int hours = minutes / 60;
        minutes %= 60;
        return hours > 0
            ? String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
            : String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }

    // ------------------------------------------------------------------ NbtIo (class_2507)

    /** was NbtIo.readCompressed(File) (method_30613): the file's stream, read with an unlimited accounter */
    public static net.minecraft.class_2487 method_30613(File file) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return net.minecraft.class_2507.method_10629(in, net.minecraft.class_2505.method_53898());
        }
    }

    /** was NbtIo.readCompressed(InputStream) (method_10629): an unlimited accounter, as 1.20.2 used */
    public static net.minecraft.class_2487 method_10629(InputStream in) throws IOException {
        return net.minecraft.class_2507.method_10629(in, net.minecraft.class_2505.method_53898());
    }

    /** was NbtIo.parseCompressed(File, StreamTagVisitor, NbtAccounter) (method_40057) */
    public static void method_40057(File file, net.minecraft.class_6836 visitor, net.minecraft.class_2505 accounter)
            throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            net.minecraft.class_2507.method_40058(in, visitor, accounter);
        }
    }

    /** was NbtIo.writeCompressed(CompoundTag, File) (method_30614) */
    public static void method_30614(net.minecraft.class_2487 tag, File file) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            net.minecraft.class_2507.method_10634(tag, out);
        }
    }

    /** was NbtIo.write(CompoundTag, File) (method_10630): uncompressed, straight to the file */
    public static void method_10630(net.minecraft.class_2487 tag, File file) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             DataOutputStream out = new DataOutputStream(fileOut)) {
            net.minecraft.class_2507.method_10628(tag, out);
        }
    }

    /** was NbtIo.read(File) (method_10633): null when the file does not exist, else uncompressed, unlimited */
    public static net.minecraft.class_2487 method_10633(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fileIn = new FileInputStream(file);
             DataInputStream in = new DataInputStream(fileIn)) {
            return net.minecraft.class_2507.method_10625(in, net.minecraft.class_2505.method_53898());
        }
    }

    // ------------------------------------------------------------------ HttpUtil (class_3521)

    private static final class Downloads {
        /** HttpUtil.DOWNLOAD_EXECUTOR as 1.20.2 built it (gone in 1.20.3) */
        static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setDaemon(true)
                .setUncaughtExceptionHandler(new net.minecraft.class_140(LOGGER))
                .setNameFormat("Downloader %d").build()));
    }

    /** was HttpUtil.DOWNLOAD_EXECUTOR (field_15664) */
    public static ListeningExecutorService field_15664() {
        return Downloads.EXECUTOR;
    }

    /**
     * was HttpUtil.downloadTo(File, URL, Map, int, ProgressListener, Proxy) (method_15301): 1.20.2's
     * asynchronous download on its download executor, with its progress reporting, size limit and
     * "file already there with the same length" shortcut. 1.20.3's replacement (downloadFile) is a
     * synchronous, hash-addressed cache download with another contract, so this is 1.20.2's own logic.
     */
    public static CompletableFuture<?> method_15301(File file, URL url, Map<String, String> headers, int maxSize,
                                                    net.minecraft.class_3536 progress, Proxy proxy) {
        return CompletableFuture.supplyAsync(() -> download(file, url, headers, maxSize, progress, proxy), Downloads.EXECUTOR);
    }

    private static Object download(File file, URL url, Map<String, String> headers, int maxSize,
                                   net.minecraft.class_3536 progress, Proxy proxy) {
        HttpURLConnection connection = null;
        InputStream in = null;
        OutputStream out = null;
        if (progress != null) {
            progress.method_15413(net.minecraft.class_2561.method_43471("resourcepack.downloading"));
            progress.method_15414(net.minecraft.class_2561.method_43471("resourcepack.requesting"));
        }
        try {
            try {
                byte[] buffer = new byte[4096];
                connection = (HttpURLConnection) url.openConnection(proxy);
                connection.setInstanceFollowRedirects(true);
                float done = 0.0F;
                float total = headers.entrySet().size();
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    connection.setRequestProperty(e.getKey(), e.getValue());
                    if (progress != null) {
                        progress.method_15410((int) (++done / total * 100.0F));
                    }
                }
                in = connection.getInputStream();
                total = connection.getContentLength();
                int length = connection.getContentLength();
                if (progress != null) {
                    progress.method_15414(net.minecraft.class_2561.method_43469("resourcepack.progress",
                        String.format(Locale.ROOT, "%.2f", total / 1000.0F / 1000.0F)));
                }
                if (file.exists()) {
                    long have = file.length();
                    if (have == length) {
                        if (progress != null) {
                            progress.method_15411();
                        }
                        return null;
                    }
                    LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", file, length, have);
                    FileUtils.deleteQuietly(file);
                } else if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                out = new DataOutputStream(new FileOutputStream(file));
                if (maxSize > 0 && total > maxSize) {
                    if (progress != null) {
                        progress.method_15411();
                    }
                    throw new IOException("Filesize is bigger than maximum allowed (file is " + done + ", limit is " + maxSize + ")");
                }
                int read;
                while ((read = in.read(buffer)) >= 0) {
                    done += read;
                    if (progress != null) {
                        progress.method_15410((int) (done / total * 100.0F));
                    }
                    if (maxSize > 0 && done > maxSize) {
                        if (progress != null) {
                            progress.method_15411();
                        }
                        throw new IOException("Filesize was bigger than maximum allowed (got >= " + done + ", limit was " + maxSize + ")");
                    }
                    if (Thread.interrupted()) {
                        LOGGER.error("INTERRUPTED");
                        if (progress != null) {
                            progress.method_15411();
                        }
                        return null;
                    }
                    out.write(buffer, 0, read);
                }
                if (progress != null) {
                    progress.method_15411();
                }
            } catch (Throwable t) {
                LOGGER.error("Failed to download file", t);
                if (connection != null) {
                    try {
                        InputStream err = connection.getErrorStream();
                        LOGGER.error("HTTP response error: {}", IOUtils.toString(err, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        LOGGER.error("Failed to read response from server");
                    }
                }
                if (progress != null) {
                    progress.method_15411();
                }
            }
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        return null;
    }

    // ------------------------------------------------------------------ ExtraCodecs (class_5699)

    /**
     * was ExtraCodecs.adaptJsonSerializer(Function, Function) (method_53054): a codec through the JSON
     * passthrough, a parse failure (JsonParseException) and an encode failure (IllegalArgumentException)
     * reported as errors, as 1.20.2 did.
     */
    public static <T> Codec<T> method_53054(Function<JsonElement, T> fromJson, Function<T, JsonElement> toJson) {
        return net.minecraft.class_5699.field_40721.flatXmap(json -> {
            try {
                return DataResult.success(fromJson.apply(json));
            } catch (JsonParseException e) {
                return DataResult.error(e::getMessage);
            }
        }, value -> {
            try {
                return DataResult.success(toJson.apply(value));
            } catch (IllegalArgumentException e) {
                return DataResult.error(e::getMessage);
            }
        });
    }

    /** ExtraCodecs.recursive(Function) gained a name in 1.20.3 (it only shows in toString): the function's own */
    public static String cbRecursiveName(Function<?, ?> wrapped) {
        return String.valueOf(wrapped);
    }

    // ------------------------------------------------------------------ constants gone from 1.20.3

    /** was CommonLinks.ACCOUNT_SETTINGS... (field_43134), 1.20.2's value */
    public static final String field_43134 = "https://aka.ms/UpdateMojangAccount";

    private static final class Root {
        static final net.minecraft.class_2960 NAME = new net.minecraft.class_2960("root");
    }

    /** was BuiltInRegistries.ROOT_REGISTRY_NAME (field_41154): minecraft:root */
    public static net.minecraft.class_2960 field_41154() {
        return Root.NAME;
    }

    // ------------------------------------------------------------------ CauldronInteraction (class_5620)

    /** was CauldronInteraction.EMPTY (field_27775): 1.20.3 wraps the same map in an InteractionMap record */
    public static Map<net.minecraft.class_1792, net.minecraft.class_5620> field_27775() {
        return net.minecraft.class_5620.field_27775.comp_1982();
    }

    /** was CauldronInteraction.WATER (field_27776) */
    public static Map<net.minecraft.class_1792, net.minecraft.class_5620> field_27776() {
        return net.minecraft.class_5620.field_27776.comp_1982();
    }

    /** was CauldronInteraction.LAVA (field_27777) */
    public static Map<net.minecraft.class_1792, net.minecraft.class_5620> field_27777() {
        return net.minecraft.class_5620.field_27777.comp_1982();
    }

    /** was CauldronInteraction.POWDER_SNOW (field_28011) */
    public static Map<net.minecraft.class_1792, net.minecraft.class_5620> field_28011() {
        return net.minecraft.class_5620.field_28011.comp_1982();
    }

    /**
     * was CauldronInteraction.newInteractionMap() (method_32206): a fresh map that answers PASS for an
     * item without an interaction. 1.20.3's newInteractionMap(String) also files the map under a name
     * for its codec; 1.20.2 had no such registry, so the map stays unnamed, as it was.
     */
    public static Object2ObjectOpenHashMap<net.minecraft.class_1792, net.minecraft.class_5620> method_32206() {
        Object2ObjectOpenHashMap<net.minecraft.class_1792, net.minecraft.class_5620> map = new Object2ObjectOpenHashMap<>();
        map.defaultReturnValue((state, level, pos, player, hand, stack) -> net.minecraft.class_1269.field_5811);
        return map;
    }

    // ------------------------------------------------------------------ StringRepresentable.EnumCodec (class_3542$class_7292)

    /**
     * was StringRepresentable.EnumCodec.encode(E, ops, prefix) (method_42631): 1.20.3's EnumCodec inherits
     * StringRepresentableCodec's encode -- the value's serialized name, or its index when the ops compress
     * maps, as 1.20.2's did -- reached through Codec.encode
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> com.mojang.serialization.DataResult<T> method_42631(net.minecraft.class_3542.class_7292 codec, Enum value,
                                                                         com.mojang.serialization.DynamicOps<T> ops, T prefix) {
        return ((com.mojang.serialization.Codec) codec).encode(value, ops, prefix);
    }

    /** a 1.20.2 EnumCodec subclass's encode(E, ..) takes the enum the 1.20.3 encode(S, ..) is handed */
    public static Enum<?> cbAsEnum(net.minecraft.class_3542 value) {
        return (Enum<?>) value;
    }

    private CbUtil() {
    }
}
