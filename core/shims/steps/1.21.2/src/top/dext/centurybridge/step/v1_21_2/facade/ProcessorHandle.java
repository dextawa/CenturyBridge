package top.dext.centurybridge.step.v1_21_2.facade;

import com.mojang.datafixers.util.Either;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 1.21.1's ProcessorHandle (net/minecraft/class_3906): a named message sink. 1.21.2 replaced it with
 * TaskScheduler, which takes only Runnables; this is 1.21.1's interface with its own bodies
 * (classRenames).
 */
public interface ProcessorHandle<Msg> extends AutoCloseable {

    /** name() */
    String method_16898();

    /** tell(message) */
    void method_16901(Msg message);

    @Override
    default void close() {
    }

    /** ask(factory): a future completed by the handle handed to the message the factory makes */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <Source> CompletableFuture<Source> method_17345(Function<? super ProcessorHandle<Source>, ? extends Msg> factory) {
        CompletableFuture future = new CompletableFuture();
        Msg message = factory.apply(method_17344("ask future procesor handle", future::complete));
        this.method_16901(message);
        return future;
    }

    /** askEither(factory): as ask, completed exceptionally for a Right */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <Source> CompletableFuture<Source> method_27918(Function<? super ProcessorHandle<Either<Source, Exception>>, ? extends Msg> factory) {
        CompletableFuture future = new CompletableFuture();
        Msg message = factory.apply(method_17344("ask future procesor handle", (Either<Source, Exception> e) -> {
            e.ifLeft(future::complete);
            e.ifRight(future::completeExceptionally);
        }));
        this.method_16901(message);
        return future;
    }

    /** of(name, consumer) */
    static <Msg> ProcessorHandle<Msg> method_17344(final String name, final Consumer<Msg> consumer) {
        return new ProcessorHandle<Msg>() {
            @Override
            public String method_16898() {
                return name;
            }

            @Override
            public void method_16901(Msg message) {
                consumer.accept(message);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }
}
