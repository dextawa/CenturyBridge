package top.dext.centurybridge.rt.v1_21_4;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.util.UndashedUuid;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import top.dext.centurybridge.facade.v1_21_4.CbPartialResult;

/**
 * Library-API bridges: members of the game's bundled libraries (DFU, authlib,
 * MixinExtras) that old mods call and the target runtime no longer has.
 * These sit outside the Minecraft stub diffs entirely -- the converter's audit
 * never examined them; link-check found them. Same wiring rules as Statics:
 * a redirect resolves to the same-named static here (instance redirects take
 * the receiver first).
 */
public final class CbLibs {
    private CbLibs() {
    }

    // ---- DFU 6 -> 8: DataResult became an interface; PartialResult -> Error record

    /** DFU 6 getOrThrow(allowPartial, onError): report, maybe fall back to the partial, else throw */
    public static Object getOrThrow(DataResult<?> r, boolean allowPartial, Consumer<String> onError) {
        Optional<?> ok = r.result();
        if (ok.isPresent()) {
            return ok.get();
        }
        String msg = r.error().map(DataResult.Error::message).orElse("unknown error");
        onError.accept(msg);
        if (allowPartial) {
            Optional<?> partial = r.resultOrPartial();
            if (partial.isPresent()) {
                return partial.get();
            }
        }
        throw new RuntimeException(msg);
    }

    /** DFU 6 get(): Either.left(result) or Either.right(PartialResult) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Either get(DataResult<?> r) {
        Optional<?> ok = r.result();
        if (ok.isPresent()) {
            return Either.left(ok.get());
        }
        DataResult.Error<?> err = r.error().orElseThrow();
        return Either.right(new CbPartialResult(err::message, (Optional) err.resultOrPartial()));
    }

    /** DFU 6 error(): Optional of PartialResult (DFU 8 hands out the Error record) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional error(DataResult<?> r) {
        return r.error().map(e -> new CbPartialResult(e::message, (Optional) e.resultOrPartial()));
    }

    /** DFU 6 OptionalFieldCodec(name, codec): DFU 8 adds lenient; old behaviour is strict */
    public static <A> OptionalFieldCodec<A> cbNewOptionalFieldCodec(String name, Codec<A> codec) {
        return new OptionalFieldCodec<>(name, codec, false);
    }

    /** push for super(name, codec) in OptionalFieldCodec subclasses (superCtorReshapes) */
    public static boolean cbFalse() {
        return false;
    }

    /** DFU 6 ListCodec(element): DFU 8 adds size bounds; old behaviour is unbounded */
    public static <E> ListCodec<E> cbNewListCodec(Codec<E> element) {
        return new ListCodec<>(element, 0, Integer.MAX_VALUE);
    }

    /**
     * DFU 6 KeyDispatchCodec.unsafe(name, keyCodec, type, decoder, encoder): per-key
     * Decoder plus per-value Encoder over the whole map. DFU 8 wants one MapCodec per
     * key; assumeMapUnsafe is DFU's own adapter for exactly this whole-map shape.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static KeyDispatchCodec unsafe(String typeKey, Codec keyCodec, Function type, Function decoder, Function encoder) {
        Encoder<Object> byValue = new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(Object input, DynamicOps<T> ops, T prefix) {
                return ((DataResult<Encoder<Object>>) encoder.apply(input)).flatMap(e -> e.encode(input, ops, prefix));
            }
        };
        Function codecFor = key -> ((DataResult<Decoder<Object>>) decoder.apply(key))
            .map(d -> MapCodec.assumeMapUnsafe(Codec.of(byValue, d)));
        return new KeyDispatchCodec(typeKey, keyCodec, type, codecFor);
    }

    /** DFU 6 buildOptimized(types, executor): DFU 8 builds, then optimizes in the background */
    public static DataFixer buildOptimized(DataFixerBuilder builder, Set<com.mojang.datafixers.DSL.TypeReference> types, Executor executor) {
        DataFixerBuilder.Result result = builder.build();
        result.optimize(types, executor);
        return result.fixer();
    }

    // ---- authlib 4 -> 6

    /** authlib 4 joinServer(GameProfile, token, serverId): authlib 6 takes the profile id */
    public static void joinServer(MinecraftSessionService service, GameProfile profile, String token, String serverId)
            throws AuthenticationException {
        service.joinServer(profile.getId(), token, serverId);
    }

    /** authlib 4 getters on what became records */
    public static Map<?, ?> getTextures(MinecraftTexturesPayload payload) {
        return payload.textures();
    }

    public static Set<UUID> getBlockedProfiles(BlockListResponse response) {
        return response.blockedProfiles();
    }

    /** authlib 4 UUIDTypeAdapter's static helpers moved to UndashedUuid */
    public static UUID fromString(String undashed) {
        return UndashedUuid.fromString(undashed);
    }

    public static String fromUUID(UUID uuid) {
        return UndashedUuid.toString(uuid);
    }

    // ---- MixinExtras 0.1 -> 0.5

    /** MixinExtras 0.1 initialize(runtime): 0.2+ bootstraps itself; init() is the remaining entry */
    public static void initialize(boolean runtime) {
        com.llamalad7.mixinextras.MixinExtrasBootstrap.init();
    }
}
