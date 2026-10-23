package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_10295;
import net.minecraft.class_10298;
import net.minecraft.class_1703;
import net.minecraft.class_1729;
import net.minecraft.class_1863;
import net.minecraft.class_2695;
import net.minecraft.class_2840;
import net.minecraft.class_2853;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import net.minecraft.class_8710;
import net.minecraft.class_8786;
import net.minecraft.class_9129;
import net.minecraft.class_9139;

/**
 * 1.21.1's recipe-addressed packets on 1.21.2's display-addressed ones.
 *
 * 1.21.1 named a recipe in ServerboundPlaceRecipePacket, ServerboundRecipeBookSeenRecipePacket and
 * ClientboundPlaceGhostRecipePacket by its id; 1.21.2 names a RecipeDisplayId (the server's index of one of
 * the recipe's displays) or sends the RecipeDisplay itself. A recipe and its displays are related by the
 * server's RecipeManager, and on the client by the table the layer's recipe synchronisation sent
 * ({@link CbRestRecipeSync}). A recipe is sent as its first display, as 1.21.2's recipe book sends a
 * recipe's display; a display read back answers the recipe it belongs to.
 *
 * A recipe with no display in 1.21.2 (a 1.21.1 recipe class 1.21.2 gives no display) cannot be named in
 * 1.21.2's packets. Placing one is still expressed: when the server has the layer the client sends
 * {@link PlaceById}, which the server handles exactly as 1.21.2's handlePlaceRecipe handles a display, and a
 * ghost recipe comes back as {@link GhostById}.
 */
public final class CbRestRecipeNet {

    private CbRestRecipeNet() {
    }

    /** a 1.21.1 packet 1.21.2 has no type for, sent as the 1.21.2 packet(s) saying the same (CbRestOutgoingPacketMixin) */
    public interface Outgoing {
        net.minecraft.class_2596<?> cb$toW();
    }

    /** Connection.send: a 1.21.1 packet without a 1.21.2 type goes out as its 1.21.2 packet(s), anything else as it is */
    public static net.minecraft.class_2596<?> outgoing(net.minecraft.class_2596<?> packet) {
        return packet instanceof Outgoing legacy ? legacy.cb$toW() : packet;
    }

    /** the client's table (set by {@link CbRestRecipesClient}): a recipe's display ids, a display id's recipe */
    static volatile Function<class_8786<?>, int[]> clientDisplays;
    static volatile Function<class_10298, class_8786<?>> clientRecipe;
    static volatile Function<class_10295, class_8786<?>> clientRecipeOfDisplay;

    /** place-recipe by recipe id, for a recipe without a display (client to a server with the layer) */
    public record PlaceById(int containerId, class_2960 recipe, boolean useMaxItems) implements class_8710 {
        public static final class_8710.class_9154<PlaceById> TYPE =
                new class_8710.class_9154<>(class_2960.method_60655("centurybridge", "place_recipe_1_21_1"));
        public static final class_9139<class_9129, PlaceById> CODEC = class_8710.method_56484(PlaceById::write, PlaceById::read);

        private void write(class_9129 buf) {
            buf.method_10804(containerId);
            buf.method_10812(recipe);
            buf.method_52964(useMaxItems);
        }

        private static PlaceById read(class_9129 buf) {
            return new PlaceById(buf.method_10816(), buf.method_10810(), buf.readBoolean());
        }

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }

    /** the ghost recipe answering a {@link PlaceById} (server to client) */
    public record GhostById(int containerId, class_2960 recipe) implements class_8710 {
        public static final class_8710.class_9154<GhostById> TYPE =
                new class_8710.class_9154<>(class_2960.method_60655("centurybridge", "ghost_recipe_1_21_1"));
        public static final class_9139<class_9129, GhostById> CODEC = class_8710.method_56484(GhostById::write, GhostById::read);

        private void write(class_9129 buf) {
            buf.method_10804(containerId);
            buf.method_10812(recipe);
        }

        private static GhostById read(class_9129 buf) {
            return new GhostById(buf.method_10816(), buf.method_10810());
        }

        @Override
        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }

    static void init() {
        PayloadTypeRegistry.playC2S().register(PlaceById.TYPE, PlaceById.CODEC);
        PayloadTypeRegistry.playS2C().register(GhostById.TYPE, GhostById.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PlaceById.TYPE, (payload, context) -> placeById(context.player(), payload));
    }

    /** 1.21.2's ServerGamePacketListenerImpl.handlePlaceRecipe, with the recipe named by id */
    private static void placeById(class_3222 player, PlaceById payload) {
        player.method_14234();
        class_1703 menu = player.field_7512;
        if (player.method_7325() || menu.field_7763 != payload.containerId() || !menu.method_7597(player)) {
            return;
        }
        class_5321<net.minecraft.class_1860<?>> key = class_5321.method_29179(class_7924.field_52178, payload.recipe());
        class_8786<?> holder = player.field_13995.method_3772().method_8130(key).orElse(null);
        if (holder == null || !player.method_14253().method_64593(key) || !(menu instanceof class_1729 book)) {
            return;
        }
        if (holder.comp_1933().method_61671().method_61687()) {
            return;
        }
        class_1729.class_9885 result = book.method_17697(payload.useMaxItems(), player.method_7337(), holder, player.method_51469(), player.method_31548());
        if (result == class_1729.class_9885.field_52573 && ServerPlayNetworking.canSend(player, GhostById.TYPE)) {
            ServerPlayNetworking.send(player, new GhostById(menu.field_7763, payload.recipe()));
        }
    }

    private static int[] displayIds(class_8786<?> holder) {
        Function<class_8786<?>, int[]> client = clientDisplays;
        int[] ids = client != null ? client.apply(holder) : null;
        if ((ids == null || ids.length == 0) && CbRestRecipesHolder.server != null) {
            List<Integer> found = new ArrayList<>();
            CbRestRecipesHolder.server.method_3772().method_64679(holder.comp_1932(), e -> found.add(e.comp_3262().comp_3267()));
            ids = found.stream().mapToInt(Integer::intValue).toArray();
        }
        return ids != null ? ids : new int[0];
    }

    /** the first display of a recipe, or null (none in 1.21.2) */
    public static class_10298 firstDisplay(class_8786<?> holder) {
        int[] ids = displayIds(holder);
        return ids.length > 0 ? new class_10298(ids[0]) : null;
    }

    private static class_10298 requireDisplay(class_8786<?> holder, String what) {
        class_10298 id = firstDisplay(holder);
        if (id == null) {
            throw new IllegalArgumentException(what + " of recipe " + holder.comp_1932().method_29177()
                    + ": 1.21.2 names a recipe in this packet by one of its recipe displays, and this recipe has none");
        }
        return id;
    }

    /** the recipe a display id stands for (the server's RecipeManager, else the client's table), or null */
    private static class_8786<?> recipeOf(class_10298 display) {
        net.minecraft.server.MinecraftServer server = CbRestRecipesHolder.server;
        if (server != null) {
            class_1863 manager = server.method_3772();
            try {
                class_1863.class_10288 info = manager.method_64686(display);
                if (info != null) {
                    return info.comp_3250();
                }
            } catch (IndexOutOfBoundsException e) {
                // not one of this server's displays
            }
        }
        Function<class_10298, class_8786<?>> client = clientRecipe;
        return client != null ? client.apply(display) : null;
    }

    private static class_2960 idOf(class_8786<?> holder) {
        return holder == null ? null : holder.comp_1932().method_29177();
    }

    /** new ServerboundPlaceRecipePacket(containerId, recipe, useMaxItems) of 1.21.1 */
    public static class_2840 newPlaceRecipe(int containerId, class_8786<?> recipe, boolean useMaxItems) {
        return new class_2840(containerId, requireDisplay(recipe, "ServerboundPlaceRecipePacket"), useMaxItems);
    }

    /** ServerboundPlaceRecipePacket.getRecipe() of 1.21.1: the id of the recipe its display belongs to */
    public static class_2960 placeRecipeId(class_2840 packet) {
        return idOf(recipeOf(packet.comp_3244()));
    }

    /** new ServerboundRecipeBookSeenRecipePacket(recipe) of 1.21.1 */
    public static class_2853 newSeenRecipe(class_8786<?> recipe) {
        return new class_2853(requireDisplay(recipe, "ServerboundRecipeBookSeenRecipePacket"));
    }

    /** ServerboundRecipeBookSeenRecipePacket.getRecipe() of 1.21.1 */
    public static class_2960 seenRecipeId(class_2853 packet) {
        return idOf(recipeOf(packet.comp_3246()));
    }

    /** new ClientboundPlaceGhostRecipePacket(containerId, recipe) of 1.21.1: the recipe's first display */
    public static class_2695 newGhostRecipe(int containerId, class_8786<?> recipe) {
        net.minecraft.server.MinecraftServer server = CbRestRecipesHolder.server;
        if (server != null) {
            List<class_10295> found = new ArrayList<>();
            server.method_3772().method_64679(recipe.comp_1932(), e -> found.add(e.comp_3263()));
            if (!found.isEmpty()) {
                return new class_2695(containerId, found.get(0));
            }
        }
        throw new IllegalArgumentException("ClientboundPlaceGhostRecipePacket of recipe " + recipe.comp_1932().method_29177()
                + ": 1.21.2 sends a ghost recipe as one of the recipe's displays, and this recipe has none");
    }

    /** ClientboundPlaceGhostRecipePacket.getRecipe() of 1.21.1: the id of the recipe the display belongs to */
    public static class_2960 ghostRecipeId(class_2695 packet) {
        Function<class_10295, class_8786<?>> client = clientRecipeOfDisplay;
        class_8786<?> holder = client != null ? client.apply(packet.comp_3227()) : null;
        if (holder == null && CbRestRecipesHolder.server != null) {
            for (class_8786<?> h : CbRestRecipesHolder.server.method_3772().method_8126()) {
                List<class_10295> found = new ArrayList<>();
                CbRestRecipesHolder.server.method_3772().method_64679(h.comp_1932(), e -> found.add(e.comp_3263()));
                if (found.contains(packet.comp_3227())) {
                    holder = h;
                    break;
                }
            }
        }
        return idOf(holder);
    }
}
