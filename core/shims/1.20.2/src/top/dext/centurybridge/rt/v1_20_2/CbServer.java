package top.dext.centurybridge.rt.v1_20_2;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.class_2168;
import net.minecraft.class_2487;
import net.minecraft.class_3222;
import net.minecraft.class_3312;
import net.minecraft.class_7448;
import net.minecraft.class_7620;
import net.minecraft.class_8791;
import net.minecraft.class_8792;

/**
 * 1.20.1 server-side calls on 1.20.2's API: command sources, function execution, player list,
 * the profile cache.
 */
public final class CbServer {

    // ---- CommandSourceStack: 1.20.2 folded withSigningContext(ctx) and withMessageChainTaskQueue(chainer)
    // into withSigningContext(ctx, chainer)

    /** source.withSigningContext(ctx): the source's own task chainer carries over, as in 1.20.1 */
    public static class_2168 method_43735(class_2168 self, class_7448 signing) {
        return self.method_43735(signing, self.method_44909());
    }

    /** source.withMessageChainTaskQueue(chainer): the source's own override when it declares one */
    public static class_2168 method_44908(class_2168 self, class_7620 chainer) {
        if (self instanceof CbLegacyCommandSourceChainer own) {
            return own.method_44908(chainer);
        }
        return super_method_44908(self, chainer);
    }

    /** super.withMessageChainTaskQueue(chainer): vanilla's copy with the chainer replaced, the signing context kept */
    public static class_2168 super_method_44908(class_2168 self, class_7620 chainer) {
        try {
            return (class_2168) CbSuper.special(class_2168.class, "method_43735",
                MethodType.methodType(class_2168.class, class_7448.class, class_7620.class))
                .invoke(self, self.method_43738(), chainer);
        } catch (Throwable t) {
            throw CbSuper.rethrow(t);
        }
    }

    // ---- ServerFunctionManager.execute(function, source, tracer): 1.20.2 added macro arguments

    /** functions.execute(function, source, tracer): a 1.20.1 call carries no macro arguments */
    public static int method_36341(net.minecraft.class_2991 self, net.minecraft.class_2158 function, class_2168 source,
            net.minecraft.class_2991.class_6346 tracer) {
        try {
            return self.method_36341(function, source, tracer, (class_2487) null);
        } catch (net.minecraft.class_8643 e) {
            // a macro function run without arguments: nothing runs, as 1.20.2's own execute(function, source)
            // answers it (macros did not exist in 1.20.1)
            return 0;
        }
    }

    // ---- PlayerList: 1.20.2 hands placeNewPlayer the connection's client data and createPlayer the client options

    /** playerList.onPlayerConnect(connection, player): the client data of a player joining with defaults, its own profile */
    public static void method_14570(net.minecraft.class_3324 self, net.minecraft.class_2535 connection, class_3222 player) {
        self.method_14570(connection, player, class_8792.method_53824(player.method_7334()));
    }

    /** playerList.createPlayer(profile): default client options, as a 1.20.1 player started with */
    public static class_3222 method_14613(net.minecraft.class_3324 self, GameProfile profile) {
        return self.method_14613(profile, class_8791.method_53821());
    }

    // ---- UserCache.findByNameAsync(name, consumer): 1.20.2 returns the future instead

    /** cache.findByNameAsync(name, consumer): the cache's own override when it declares one */
    public static void method_37156(class_3312 self, String name, Consumer<Optional<GameProfile>> consumer) {
        if (self instanceof CbLegacyUserCacheLookup own) {
            own.method_37156(name, consumer);
            return;
        }
        super_method_37156(self, name, consumer);
    }

    /** super.findByNameAsync(name, consumer): vanilla's lookup; the consumer runs on the cache's executor, as in 1.20.1 */
    @SuppressWarnings("unchecked")
    public static void super_method_37156(class_3312 self, String name, Consumer<Optional<GameProfile>> consumer) {
        CompletableFuture<Optional<GameProfile>> lookup;
        try {
            lookup = (CompletableFuture<Optional<GameProfile>>) CbSuper.special(class_3312.class, "method_37156",
                MethodType.methodType(CompletableFuture.class, String.class)).invoke(self, name);
        } catch (Throwable t) {
            throw CbSuper.rethrow(t);
        }
        lookup.whenCompleteAsync((profile, failure) -> consumer.accept(profile),
            ((top.dext.centurybridge.shims.v1_20_2.UserCacheExecutorAccessor) self).cb$executor());
    }

    /** findByNameAsync(name), the shape 1.20.2 calls, synthesized into a cache overriding the 1.20.1 one */
    public static CompletableFuture<Optional<GameProfile>> findByNameAsync(CbLegacyUserCacheLookup self, String name) {
        CompletableFuture<Optional<GameProfile>> result = new CompletableFuture<>();
        self.method_37156(name, result::complete);
        return result;
    }

    private CbServer() {
    }
}
