package top.dext.centurybridge.step.v1_21_2.rt;

import top.dext.centurybridge.step.v1_21_2.mixin.CbRestRecipeManagerAccessor;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRestClientRecipeBookAccessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_10289;
import net.minecraft.class_10295;
import net.minecraft.class_10297;
import net.minecraft.class_10298;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_8786;

/**
 * The client side of 1.21.1's recipe synchronisation ({@link CbRestRecipeSync}): 1.21.1's client
 * RecipeManager, and the table between the recipes (RecipeHolder, by id) and the RecipeDisplayIds 1.21.2's
 * recipe book addresses them by.
 */
@Environment(EnvType.CLIENT)
public final class CbRestRecipesClient {

    private CbRestRecipesClient() {
    }

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    private static final List<CbRestRecipeSync.Entry> pending = new ArrayList<>();
    private static Map<class_2960, class_8786<?>> byId = Map.of();
    private static Int2ObjectOpenHashMap<class_8786<?>> byDisplay = new Int2ObjectOpenHashMap<>();
    private static Map<class_5321<class_1860<?>>, int[]> displays = Map.of();
    private static class_1863 manager;
    private static class_634 managerConnection;
    private static final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /** client entrypoint */
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CbRestRecipeSync.Payload.TYPE, (payload, context) -> accept(payload));
        ClientPlayNetworking.registerGlobalReceiver(CbRestRecipeNet.GhostById.TYPE, (payload, context) -> {
            class_8786<?> holder = byId(payload.recipe());
            if (holder != null && ghostListener != null) {
                ghostListener.accept(payload.containerId(), holder);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> install(List.of()));
        CbRestRecipeNet.clientDisplays = CbRestRecipesClient::displayIds;
        CbRestRecipeNet.clientRecipe = CbRestRecipesClient::byDisplay;
        CbRestRecipeNet.clientRecipeOfDisplay = CbRestRecipesClient::byRecipeDisplay;
    }

    /** receives a ghost recipe the server named by recipe (set by the 1.21.1 recipe-book client) */
    static volatile BiConsumer<Integer, class_8786<?>> ghostListener;

    /** runs after every change of the received recipes (1.21.1's ClientPacketListener.handleUpdateRecipes) */
    public static void onRecipesChanged(Runnable listener) {
        listeners.add(listener);
    }

    private static void accept(CbRestRecipeSync.Payload payload) {
        if ((payload.flags() & CbRestRecipeSync.FIRST) != 0) {
            pending.clear();
        }
        class_634 connection = class_310.method_1551().method_1562();
        if (connection == null) {
            return;
        }
        class_5455 access = connection.method_29091();
        for (byte[] e : payload.entries()) {
            pending.add(CbRestRecipeSync.decode(access, e));
        }
        if ((payload.flags() & CbRestRecipeSync.LAST) != 0) {
            install(new ArrayList<>(pending));
            pending.clear();
        }
    }

    private static void install(List<CbRestRecipeSync.Entry> entries) {
        Map<class_2960, class_8786<?>> ids = new LinkedHashMap<>();
        Int2ObjectOpenHashMap<class_8786<?>> disp = new Int2ObjectOpenHashMap<>();
        Map<class_5321<class_1860<?>>, int[]> keys = new HashMap<>();
        for (CbRestRecipeSync.Entry e : entries) {
            ids.put(e.holder().comp_1932().method_29177(), e.holder());
            keys.put(e.holder().comp_1932(), e.displayIds());
            for (int id : e.displayIds()) {
                disp.put(id, e.holder());
            }
        }
        byId = ids;
        byDisplay = disp;
        displays = keys;
        if (manager != null) {
            // 1.21.1 kept one RecipeManager per connection and replaced its recipes in place
            ((CbRestRecipeManagerAccessor) (Object) manager).cb$setRecipes(class_10289.method_64700(new ArrayList<>(ids.values())));
        }
        CbRestRecipesHolder.setReceived(new ArrayList<>(ids.values()));
        for (Runnable r : listeners) {
            r.run();
        }
    }

    /** every recipe received, in the server's order */
    public static Collection<class_8786<?>> recipes() {
        return byId.values();
    }

    /** the received recipe of this id, or null */
    public static class_8786<?> byId(class_2960 id) {
        return byId.get(id);
    }

    /** the recipe a RecipeDisplayId of 1.21.2's recipe book shows, or null (not received) */
    public static class_8786<?> byDisplay(class_10298 display) {
        return display == null ? null : byDisplay.get(display.comp_3267());
    }

    /** the recipe one of the displays the client's recipe book knows belongs to, or null */
    public static class_8786<?> byRecipeDisplay(class_10295 display) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || display == null) {
            return null;
        }
        for (class_10297 entry : ((CbRestClientRecipeBookAccessor) player.method_3130()).cb$known().values()) {
            if (display.equals(entry.comp_3263())) {
                class_8786<?> holder = byDisplay(entry.comp_3262());
                if (holder != null) {
                    return holder;
                }
            }
        }
        return null;
    }

    /** LocalPlayer.removeRecipeHighlight(recipe) of 1.21.1: each of the recipe's displays */
    public static void removeRecipeHighlight(class_746 player, class_8786<?> recipe) {
        for (int id : displayIds(recipe)) {
            player.method_3141(new class_10298(id));
        }
    }

    /**
     * MultiPlayerGameMode.handlePlaceRecipe(containerId, recipe, useMaxItems) of 1.21.1: the recipe's first
     * display, as 1.21.2's recipe book places it; a recipe without a display is placed by id when the server
     * has the layer ({@link CbRestRecipeNet.PlaceById}), and otherwise cannot be named to the server
     */
    public static void handlePlaceRecipe(class_636 gameMode, int containerId, class_8786<?> recipe, boolean useMaxItems) {
        class_10298 display = firstDisplay(recipe);
        if (display != null) {
            gameMode.method_2912(containerId, display, useMaxItems);
        } else if (ClientPlayNetworking.canSend(CbRestRecipeNet.PlaceById.TYPE)) {
            ClientPlayNetworking.send(new CbRestRecipeNet.PlaceById(containerId, recipe.comp_1932().method_29177(), useMaxItems));
        } else {
            LOGGER.warn("cannot place recipe {}: it has no recipe display in 1.21.2 and the server lacks CenturyBridge's 1.21.2 layer",
                    recipe.comp_1932().method_29177());
        }
    }

    /** the RecipeDisplayIds of a recipe's displays (empty: not received, or a recipe without display) */
    public static int[] displayIds(class_8786<?> holder) {
        int[] ids = displays.get(holder.comp_1932());
        return ids != null ? ids : new int[0];
    }

    /** the first RecipeDisplayId of a recipe, or null */
    public static class_10298 firstDisplay(class_8786<?> holder) {
        int[] ids = displayIds(holder);
        return ids.length > 0 ? new class_10298(ids[0]) : null;
    }

    /**
     * ClientPacketListener.getRecipeManager() of 1.21.1: a RecipeManager holding the recipes the server
     * sent (empty until they arrive, and on a server without the layer)
     */
    public static class_1863 getRecipeManager(class_634 connection) {
        if (manager == null || managerConnection != connection) {
            class_1863 m = new class_1863(connection.method_29091());
            ((CbRestRecipeManagerAccessor) (Object) m).cb$setRecipes(class_10289.method_64700(new ArrayList<>(byId.values())));
            manager = m;
            managerConnection = connection;
        }
        return manager;
    }
}
