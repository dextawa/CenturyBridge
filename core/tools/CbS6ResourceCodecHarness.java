import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.CbS6ResourceCtorProbe;
import net.minecraft.class_6880;
import net.minecraft.class_7157;
import net.minecraft.class_9433;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6ResourceCodec;

/**
 * Focused JVM behavior check for the 1.21.5 -> 1.21.6 ResourceOrIdArgument codec adapter. The default run checks codec
 * behavior. With --ctors it also checks CbS6ResourceCtorProbe after conversion; CB's frames-stripped intermediary-code
 * analysis jar needs -Xverify:none for that fixture-only execution (a real Fabric runtime has valid frames).
 */
public final class CbS6ResourceCodecHarness {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void checkArgumentCodec(class_9433<String> argument, Codec<class_6880<String>> legacy)
            throws ReflectiveOperationException {
        Field codecField = class_9433.class.getDeclaredField("field_50042");
        codecField.setAccessible(true);
        Object stored = codecField.get(argument);
        check(stored != legacy, "the constructor must replace the old holder codec");
        Object decoded = ((Codec<?>) stored).parse(JsonOps.INSTANCE, new JsonPrimitive("argument")).getOrThrow();
        check("argument".equals(decoded), "the stored codec must answer T, not Holder<T>: " + decoded);
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        Codec<class_6880<String>> holders = Codec.STRING.xmap(class_6880::method_40223, class_6880::comp_349);
        Codec<String> values = CbS6ResourceCodec.valueCodec(holders);
        check(CbS6ResourceCodec.valueCodec((Codec) values) == values, "an adapted codec must pass through unchanged");

        JsonElement encoded = values.encodeStart(JsonOps.INSTANCE, "stone").getOrThrow();
        check(encoded.equals(new JsonPrimitive("stone")), "encode must use the old codec: " + encoded);
        check(values.parse(JsonOps.INSTANCE, encoded).getOrThrow().equals("stone"), "encode/parse round trip");
        check(values.parse(JsonOps.INSTANCE, new JsonPrimitive("dirt")).getOrThrow().equals("dirt"),
            "inline value parse");

        Codec<class_6880<String>> failing = new Codec<>() {
            @Override
            public <T> DataResult<T> encode(class_6880<String> value, DynamicOps<T> ops, T prefix) {
                return DataResult.error(() -> "encode sentinel");
            }

            @Override
            public <T> DataResult<Pair<class_6880<String>, T>> decode(DynamicOps<T> ops, T input) {
                return DataResult.error(() -> "decode sentinel");
            }
        };
        Codec<String> failedValues = CbS6ResourceCodec.valueCodec(failing);
        DataResult<?> encodeFailure = failedValues.encodeStart(JsonOps.INSTANCE, "x");
        DataResult<?> decodeFailure = failedValues.parse(JsonOps.INSTANCE, new JsonPrimitive("x"));
        check(encodeFailure.error().orElseThrow().message().equals("encode sentinel"), "encode error was lost");
        check(decodeFailure.error().orElseThrow().message().equals("decode sentinel"), "decode error was lost");
        check(encodeFailure.result().isEmpty() && decodeFailure.result().isEmpty(), "errors must not become values");

        Codec<StringBuilder> mutableValues = Codec.STRING.xmap(StringBuilder::new, StringBuilder::toString);
        AtomicReference<class_6880<StringBuilder>> emitted = new AtomicReference<>();
        Codec<class_6880<StringBuilder>> mutableHolders = new Codec<>() {
            @Override
            public <T> DataResult<T> encode(class_6880<StringBuilder> value, DynamicOps<T> ops, T prefix) {
                return mutableValues.encode(value.comp_349(), ops, prefix);
            }

            @Override
            public <T> DataResult<Pair<class_6880<StringBuilder>, T>> decode(DynamicOps<T> ops, T input) {
                return mutableValues.decode(ops, input).map(pair -> {
                    class_6880<StringBuilder> holder = class_6880.method_40223(pair.getFirst());
                    emitted.set(holder);
                    return Pair.of(holder, pair.getSecond());
                });
            }
        };
        StringBuilder mutable = CbS6ResourceCodec.valueCodec(mutableHolders)
            .parse(JsonOps.INSTANCE, new JsonPrimitive("before")).getOrThrow();
        check(mutable == emitted.get().comp_349(), "decode must not copy the holder's value");
        mutable.append("-after");
        check(emitted.get().comp_349().toString().equals("before-after"), "the same mutable value must be observed");

        if (args.length == 1 && args[0].equals("--ctors")) {
            class_7157 lookup = (class_7157) Proxy.newProxyInstance(
                CbS6ResourceCodecHarness.class.getClassLoader(), new Class<?>[] {class_7157.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("method_46759")) {
                        return Optional.empty();
                    }
                    throw new UnsupportedOperationException(method.toString());
                });
            checkArgumentCodec(CbS6ResourceCtorProbe.direct(lookup, null, holders), holders);
            checkArgumentCodec(new CbS6ResourceCtorProbe.Super<>(lookup, null, holders), holders);
        }

        System.out.println("CB-S6RESOURCE ok checks=" + checks);
    }
}
