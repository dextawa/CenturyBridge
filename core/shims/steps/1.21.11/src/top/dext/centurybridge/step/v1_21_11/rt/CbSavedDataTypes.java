package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_10741;
import net.minecraft.class_18;
import net.minecraft.class_273;
import net.minecraft.class_3218;
import net.minecraft.class_4284;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.facade.SavedDataContext;

/**
 * 1.21.10's SavedDataType(id, Function&lt;Context, T&gt; constructor, Function&lt;Context, Codec&lt;T&gt;&gt; codec, fixType)
 * (class_10741) takes a Supplier and a Codec in 1.21.11, and SavedData.Context (facade/SavedDataContext) is gone (javap
 * -p both). 1.21.10's storage (DimensionDataStorage) applied its own Context, the one ServerChunkCache built for the
 * server's one storage: Context(the overworld) -- the storage is the overworld's, every level reads it (ServerLevel
 * method_17985). The Function form is given back as a 1.21.11 type whose supplier and codec apply that Context when
 * the storage uses them (the running server's overworld, recorded by mixin/CbSavedDataServerMixin); the accessors
 * answer the Functions the mod gave (or, for a 1.21.11 type, Functions over its supplier / codec that ignore the
 * Context, as 1.21.10's Supplier constructor did). A storage a mod builds itself had its own Context in 1.21.10; in
 * 1.21.11 every type reads the server's (a loss recorded in release_notes_known).
 */
public final class CbSavedDataTypes {
    private CbSavedDataTypes() {
    }

    private record Forms(Function<SavedDataContext, ?> constructor, Function<SavedDataContext, ? extends Codec<?>> codec) {
    }

    private static final Map<class_10741<?>, Forms> FORMS = new MapMaker().weakKeys().makeMap();

    private static volatile MinecraftServer server;

    /** MinecraftServer.runServer began (CbSavedDataServerMixin) */
    public static void started(MinecraftServer s) {
        server = s;
    }

    /** MinecraftServer.stopServer finished (CbSavedDataServerMixin) */
    public static void stopped(MinecraftServer s) {
        if (server == s) {
            server = null;
        }
    }

    /** the Context 1.21.10's server storage applied: Context(overworld) */
    public static SavedDataContext current() {
        MinecraftServer s = server;
        class_3218 level = s == null ? null : s.method_30002();
        if (level == null) {
            throw new IllegalStateException("1.21.10 saved data needs the server's level (SavedData.Context); no server is running");
        }
        return new SavedDataContext(level);
    }

    /** new SavedDataType(id, constructor, codec, fixType) in 1.21.10's Function form */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_10741 cbNew(String id, Function constructor, Function codec, class_4284 fixType) {
        Function<SavedDataContext, class_18> ctor = constructor;
        Function<SavedDataContext, Codec<class_18>> codecs = codec;
        class_10741 type = new class_10741(id, () -> ctor.apply(current()), new Codec<class_18>() {
            @Override
            public <T> DataResult<Pair<class_18, T>> decode(DynamicOps<T> ops, T input) {
                return codecs.apply(current()).decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(class_18 data, DynamicOps<T> ops, T prefix) {
                return codecs.apply(current()).encode(data, ops, prefix);
            }
        }, fixType);
        FORMS.put(type, new Forms(ctor, codecs));
        return type;
    }

    /** SavedDataType.constructor() in 1.21.10's Function form */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Function comp_3642(class_10741 type) {
        Forms f = FORMS.get(type);
        return f != null ? f.constructor() : ctx -> type.comp_3642().get();
    }

    /** SavedDataType.codec() in 1.21.10's Function form */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Function comp_3643(class_10741 type) {
        Forms f = FORMS.get(type);
        return f != null ? f.codec() : ctx -> type.comp_3643();
    }

    /** ServerScoreboard.TYPE (1.21.10 field_56414) is ScoreboardSaveData's type in 1.21.11 (class_273.field_63438) */
    public static class_10741<class_273> field_56414() {
        return class_273.field_63438;
    }
}
