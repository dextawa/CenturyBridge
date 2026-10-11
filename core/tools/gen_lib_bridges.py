"""Library bridges (DFU 6 -> 7+, authlib record GameProfile) for the trees whose runtime
changed them. One generator so every tree carries the same faithful semantics; run from
the repo root. Re-running rewrites the files and the wiring entries idempotently."""
import json

PKG = {'1.21.1': 'v1_21_1', '1.21.4': 'v1_21_4', '1.21.8': 'v1_21_8', '1.21.11': 'v1_21_11', '26.2': 'v26_2'}
# DFU 7 (1.20.5) made DataResult a sealed interface: getOrThrow(boolean, Consumer), get() and
# PartialResult went. DFU 9 (1.21.9) dropped Codec.unit. authlib 7 (1.21.9) made GameProfile a record.
DFU_UNIT = {'1.21.11', '26.2'}
AUTHLIB_RECORD = {'1.21.11', '26.2'}

PARTIAL = '''package top.dext.centurybridge.facade.{pkg};

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * DFU 6's DataResult.PartialResult -- the error side of a 1.20.1 DataResult (get() handed it
 * out as the right of an Either, error() as an Optional), which DFU 7 replaced with the
 * DataResult.Error record. Same members, same behaviour as DFU 6.0.8 (read off its bytecode):
 * flatMap joins the two messages with "; ", toString is "DynamicException[message partial]".
 */
public class DfuPartialResult<R> {{

    private final Supplier<String> message;
    private final Optional<R> partialResult;

    public DfuPartialResult(Supplier<String> message, Optional<R> partialResult) {{
        this.message = message;
        this.partialResult = partialResult;
    }}

    public <R2> DfuPartialResult<R2> map(Function<? super R, ? extends R2> function) {{
        return new DfuPartialResult<>(message, partialResult.map(function));
    }}

    public <R2> DfuPartialResult<R2> flatMap(Function<R, DfuPartialResult<R2>> function) {{
        if (partialResult.isPresent()) {{
            DfuPartialResult<R2> result = function.apply(partialResult.get());
            return new DfuPartialResult<>(() -> message.get() + "; " + result.message.get(), result.partialResult);
        }}
        return new DfuPartialResult<>(message, Optional.empty());
    }}

    public String message() {{
        return message.get();
    }}

    @Override
    public boolean equals(Object o) {{
        if (this == o) {{
            return true;
        }}
        if (o == null || getClass() != o.getClass()) {{
            return false;
        }}
        DfuPartialResult<?> that = (DfuPartialResult<?>) o;
        return Objects.equals(message, that.message) && Objects.equals(partialResult, that.partialResult);
    }}

    @Override
    public int hashCode() {{
        return 31 * message.hashCode() + partialResult.hashCode();
    }}

    @Override
    public String toString() {{
        return "DynamicException[" + message() + " " + partialResult + "]";
    }}
}}
'''

DFU = '''package top.dext.centurybridge.rt.{pkg};

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import top.dext.centurybridge.facade.{pkg}.DfuPartialResult;

/**
 * DFU 6 (1.20.1) members the runtime's DFU no longer has, with DFU 6.0.8's semantics: the
 * converter routes a 1.20.1 mod's calls here (instance/static redirects). DataResult is a
 * sealed interface since DFU 7; its error side is DataResult.Error (message supplier,
 * partial value), which carries everything PartialResult did.
 */
public final class CbDfu {{

    /** DataResult.getOrThrow(allowPartial, onError): the error goes to onError first, then the partial (if allowed and present) or a RuntimeException */
    public static Object getOrThrow(DataResult<?> result, boolean allowPartial, Consumer<String> onError) {{
        Optional<? extends DataResult.Error<?>> error = result.error();
        if (error.isEmpty()) {{
            return result.getOrThrow();
        }}
        String message = error.get().message();
        onError.accept(message);
        if (allowPartial && error.get().partialValue().isPresent()) {{
            return error.get().partialValue().get();
        }}
        throw new RuntimeException(message);
    }}

    /** DataResult.get(): the value on the left, or the error as a PartialResult on the right */
    public static Either<Object, DfuPartialResult<Object>> get(DataResult<?> result) {{
        Optional<? extends DataResult.Error<?>> error = result.error();
        if (error.isEmpty()) {{
            return Either.left(result.getOrThrow());
        }}
        return Either.right(partial(error.get()));
    }}

    /** DataResult.error(): the error side as DFU 6 typed it */
    public static Optional<DfuPartialResult<Object>> error(DataResult<?> result) {{
        return result.error().map(CbDfu::partial);
    }}

    @SuppressWarnings("unchecked")
    private static DfuPartialResult<Object> partial(DataResult.Error<?> error) {{
        return new DfuPartialResult<>(error.messageSupplier(), (Optional<Object>) error.partialValue());
    }}
{unit}
    private CbDfu() {{
    }}
}}
'''

UNIT = '''
    /** Codec.unit(value): DFU 9 moved it to MapCodec.unitCodec */
    public static <A> com.mojang.serialization.Codec<A> unit(A value) {
        return com.mojang.serialization.MapCodec.unitCodec(value);
    }

    /** Codec.unit(supplier) */
    public static <A> com.mojang.serialization.Codec<A> unit(java.util.function.Supplier<A> value) {
        return com.mojang.serialization.MapCodec.unitCodec(value);
    }
'''

AUTH = '''package top.dext.centurybridge.rt.{pkg};

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;

/**
 * authlib 7 (1.21.9) made GameProfile a record: getName()/getId()/getProperties() became the
 * accessors name()/id()/properties(), the same values. The converter routes a 1.20.1 mod's
 * calls here.
 */
public final class CbAuthlib {{

    public static String getName(GameProfile profile) {{
        return profile.name();
    }}

    public static UUID getId(GameProfile profile) {{
        return profile.id();
    }}

    public static PropertyMap getProperties(GameProfile profile) {{
        return profile.properties();
    }}

    private CbAuthlib() {{
    }}
}}
'''

for v, pkg in PKG.items():
    base = f'core/shims/{v}/src/top/dext/centurybridge'
    open(f'{base}/facade/{pkg}/DfuPartialResult.java', 'w', encoding='utf-8', newline='\n').write(PARTIAL.format(pkg=pkg))
    open(f'{base}/rt/{pkg}/CbDfu.java', 'w', encoding='utf-8', newline='\n').write(
        DFU.format(pkg=pkg, unit=UNIT if v in DFU_UNIT else ''))
    if v in AUTHLIB_RECORD:
        open(f'{base}/rt/{pkg}/CbAuthlib.java', 'w', encoding='utf-8', newline='\n').write(AUTH.format(pkg=pkg))
    sp = f'core/segments/shims-{v}.json'
    d = json.load(open(sp, encoding='utf-8'))
    rt = f'top/dext/centurybridge/rt/{pkg}/'
    ir = d.setdefault('instanceRedirects', {})
    ir['com/mojang/serialization/DataResult.getOrThrow(ZLjava/util/function/Consumer;)Ljava/lang/Object;'] = rt + 'CbDfu'
    ir['com/mojang/serialization/DataResult.get()Lcom/mojang/datafixers/util/Either;'] = rt + 'CbDfu'
    ir['com/mojang/serialization/DataResult.error()Ljava/util/Optional;'] = rt + 'CbDfu'
    d.setdefault('classRenames', {})['com/mojang/serialization/DataResult$PartialResult'] = \
        f'top/dext/centurybridge/facade/{pkg}/DfuPartialResult'
    if v in DFU_UNIT:
        sr = d.setdefault('staticRedirects', {})
        sr['com/mojang/serialization/Codec.unit(Ljava/lang/Object;)Lcom/mojang/serialization/Codec;'] = rt + 'CbDfu'
        sr['com/mojang/serialization/Codec.unit(Ljava/util/function/Supplier;)Lcom/mojang/serialization/Codec;'] = rt + 'CbDfu'
    if v in AUTHLIB_RECORD:
        ir['com/mojang/authlib/GameProfile.getName()Ljava/lang/String;'] = rt + 'CbAuthlib'
        ir['com/mojang/authlib/GameProfile.getId()Ljava/util/UUID;'] = rt + 'CbAuthlib'
        ir['com/mojang/authlib/GameProfile.getProperties()Lcom/mojang/authlib/properties/PropertyMap;'] = rt + 'CbAuthlib'
    open(sp, 'w', encoding='utf-8', newline='\n').write(json.dumps(d, indent=1, ensure_ascii=False) + '\n')
    print(v, 'ok')
