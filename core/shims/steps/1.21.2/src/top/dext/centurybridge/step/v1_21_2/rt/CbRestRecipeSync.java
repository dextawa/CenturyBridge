package top.dext.centurybridge.step.v1_21_2.rt;

import top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeManagerAccessor;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_10289;
import net.minecraft.class_1863;
import net.minecraft.class_2788;
import net.minecraft.class_2960;
import net.minecraft.class_3244;
import net.minecraft.class_5455;
import net.minecraft.class_7699;
import net.minecraft.class_7701;
import net.minecraft.class_8710;
import net.minecraft.class_8786;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

/**
 * 1.21.1's recipe synchronisation, given back.
 *
 * Up to 1.21.1 the server sent every recipe to the client (ClientboundUpdateRecipesPacket carried the
 * RecipeHolders, each through its serializer's stream codec) and the client kept them in its own
 * RecipeManager (ClientPacketListener.getRecipeManager()); the client recipe book, its collections, ghost
 * recipes and the place-recipe request all worked on those RecipeHolders. 1.21.2 stopped sending recipes:
 * ClientboundUpdateRecipesPacket now carries only item property sets and the stonecutter list, and the
 * recipe book receives RecipeDisplayEntries addressed by RecipeDisplayId.
 *
 * Wherever 1.21.2 sends ClientboundUpdateRecipesPacket (join, datapack reload, a mod's own send), the layer
 * first sends that player, when its client has the layer, every recipe the packet stood for in 1.21.1 --
 * the server's recipes, or the ones a 1.21.1 caller built the packet from -- with the RecipeDisplayIds the
 * server's RecipeManager gave each recipe's displays. The client side ({@link CbRestRecipesClient}) keeps
 * them as 1.21.1's client RecipeManager and as the table between RecipeHolder and RecipeDisplayId that the
 * 1.21.1 recipe-book API needs. Recipes are encoded one by one with RecipeHolder.STREAM_CODEC (1.21.1's
 * packet codec) and sent in payloads below the custom-payload size limit.
 *
 * A client without the layer receives nothing extra; a client with the layer on a server without it keeps an
 * empty recipe set (1.21.2's server never sends recipes).
 */
public final class CbRestRecipeSync {

    private CbRestRecipeSync() {
    }

    /** the custom payload's size limit is 1 MiB; batches stay well below it */
    private static final int BATCH_BYTES = 900_000;
    static final int FIRST = 1;
    static final int LAST = 2;

    /** one batch: flags (FIRST / LAST) and encoded entries (a RecipeHolder, then its display ids) */
    public record Payload(int flags, List<byte[]> entries) implements class_8710 {
        public static final class_8710.class_9154<Payload> TYPE =
                new class_8710.class_9154<>(class_2960.method_60655("centurybridge", "recipes_1_21_1"));
        public static final class_9139<class_9129, Payload> CODEC = class_8710.method_56484(Payload::write, Payload::read);

        private void write(class_9129 buf) {
            buf.method_10804(flags);
            buf.method_10804(entries.size());
            for (byte[] e : entries) {
                buf.method_10813(e);
            }
        }

        private static Payload read(class_9129 buf) {
            int flags = buf.method_10816();
            int n = buf.method_10816();
            List<byte[]> entries = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                entries.add(buf.method_10795());
            }
            return new Payload(flags, entries);
        }

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }

    /** main entrypoint */
    public static void init() {
        PayloadTypeRegistry.playS2C().register(Payload.TYPE, Payload.CODEC);
    }

    /** the recipes a 1.21.1 caller built a ClientboundUpdateRecipesPacket from */
    private static final Map<class_2788, List<class_8786<?>>> BUILT = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * new ClientboundUpdateRecipesPacket(recipes) of 1.21.1: 1.21.2's packet for those recipes -- the item
     * property sets and stonecutter list 1.21.2's RecipeManager derives from them (finalizeRecipeLoading, with
     * the running server's enabled features, else the default ones) -- that also carries the recipes to a
     * client with the layer when sent.
     */
    public static class_2788 newUpdateRecipesPacket(Collection<class_8786<?>> recipes) {
        class_1863 manager = new class_1863(null);
        ((CbRestRecipeManagerAccessor) (Object) manager).cb$setRecipes(class_10289.method_64700(new ArrayList<>(recipes)));
        manager.method_64681(enabledFeatures());
        class_2788 packet = new class_2788(manager.method_64692(), manager.method_64694());
        BUILT.put(packet, List.copyOf(recipes));
        return packet;
    }

    /** ClientboundUpdateRecipesPacket.getRecipes() of 1.21.1: the recipes the packet stands for */
    public static List<class_8786<?>> getRecipes(class_2788 packet) {
        List<class_8786<?>> built = BUILT.get(packet);
        if (built != null) {
            return built;
        }
        return CbRestRecipesHolder.receivedRecipes();
    }

    private static class_7699 enabledFeatures() {
        net.minecraft.server.MinecraftServer server = CbRestRecipesHolder.server;
        return server != null ? server.method_27728().method_45560() : class_7701.field_40183;
    }

    /** ServerCommonPacketListenerImpl.send, before a ClientboundUpdateRecipesPacket goes to a player */
    public static void beforeSend(class_3244 connection, class_2788 packet) {
        if (!ServerPlayNetworking.canSend(connection, Payload.TYPE)) {
            return;
        }
        net.minecraft.server.MinecraftServer server = connection.field_14140.field_13995;
        class_1863 manager = server.method_3772();
        List<class_8786<?>> built = BUILT.get(packet);
        Collection<class_8786<?>> recipes = built != null ? built : manager.method_8126();
        class_5455 access = server.method_30611();
        List<byte[]> batch = new ArrayList<>();
        int size = 0;
        int flags = FIRST;
        for (class_8786<?> holder : recipes) {
            byte[] e = encode(access, manager, holder);
            if (!batch.isEmpty() && size + e.length > BATCH_BYTES) {
                ServerPlayNetworking.send(connection.field_14140, new Payload(flags, batch));
                flags = 0;
                batch = new ArrayList<>();
                size = 0;
            }
            batch.add(e);
            size += e.length + 5;
        }
        ServerPlayNetworking.send(connection.field_14140, new Payload(flags | LAST, batch));
    }

    private static byte[] encode(class_5455 access, class_1863 manager, class_8786<?> holder) {
        class_9129 buf = new class_9129(Unpooled.buffer(), access);
        try {
            class_8786.field_48357.encode(buf, holder);
            List<Integer> ids = new ArrayList<>();
            manager.method_64679(holder.comp_1932(), entry -> ids.add(entry.comp_3262().comp_3267()));
            buf.method_10804(ids.size());
            for (int id : ids) {
                buf.method_10804(id);
            }
            byte[] out = new byte[buf.readableBytes()];
            buf.readBytes(out);
            return out;
        } finally {
            buf.release();
        }
    }

    /** a received entry: the holder and its display ids */
    record Entry(class_8786<?> holder, int[] displayIds) {
    }

    static Entry decode(class_5455 access, byte[] bytes) {
        class_9129 buf = new class_9129(Unpooled.wrappedBuffer(bytes), access);
        class_8786<?> holder = class_8786.field_48357.decode(buf);
        int n = buf.method_10816();
        int[] ids = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i] = buf.method_10816();
        }
        return new Entry(holder, ids);
    }
}
