package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10264;
import net.minecraft.class_10265;
import net.minecraft.class_10266;
import net.minecraft.class_10268;
import net.minecraft.class_10269;
import net.minecraft.class_10298;
import net.minecraft.class_2602;
import net.minecraft.class_2653;
import net.minecraft.class_2777;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_8786;
import net.minecraft.class_9833;
import net.minecraft.class_9834;
import net.minecraft.class_9835;
import top.dext.centurybridge.step.v1_21_2.facade.ClientboundRecipePacket;

/**
 * 1.21.1's ClientGamePacketListener on 1.21.2's.
 *
 * A 1.21.1 class implementing the listener (a fake client connection, a replay tool) lacks the handlers 1.21.2
 * added; each is given to it (abstractFills) as the 1.21.1 message saying the same, handed to the handler 1.21.1
 * had for it: the recipe-book add / remove / settings packets as 1.21.1's ClientboundRecipePacket (INIT for a
 * replacing add, ADD otherwise -- recipes named by id through the client's table, the settings the book has),
 * an entity position sync as the teleport packet of the same position, rotations and ground flag, the cursor
 * item and a player-inventory slot as 1.21.1's container-slot packets for container -1 and -2 (how 1.21.1's
 * server sent them). The minecart-along-track and player-rotation packets had no 1.21.1 counterpart (1.21.1's
 * server never sent anything of the kind), so a 1.21.1 listener receives nothing for them, as it did.
 *
 * handleAddOrRemoveRecipes(packet) of 1.21.1 called on any listener: a 1.21.1 class's own handler; 1.21.2's
 * listener handles the 1.21.2 packets the 1.21.1 packet stands for.
 */
@Environment(EnvType.CLIENT)
public final class CbRestClientListeners {

    private CbRestClientListeners() {
    }

    /** handleAddOrRemoveRecipes(packet) of a 1.21.1 listener class */
    public interface LegacyRecipeListener {
        void method_11115(ClientboundRecipePacket packet);
    }

    /** handleAddOrRemoveRecipes(packet) of 1.21.1, called on any listener */
    public static void method_11115(class_2602 listener, ClientboundRecipePacket packet) {
        if (listener instanceof LegacyRecipeListener legacy) {
            legacy.method_11115(packet);
        } else {
            packet.method_11054(listener);
        }
    }

    /** handleAddOrRemoveRecipes(packet) of 1.21.1 on 1.21.2's ClientPacketListener */
    public static void method_11115(class_634 listener, ClientboundRecipePacket packet) {
        method_11115((class_2602) listener, packet);
    }

    private static net.minecraft.class_5411 settings() {
        class_310 mc = class_310.method_1551();
        return mc.field_1724 != null ? mc.field_1724.method_3130().method_30173().method_30178() : new net.minecraft.class_5411();
    }

    private static List<class_2960> ids(Iterable<class_10298> displays) {
        List<class_2960> out = new ArrayList<>();
        for (class_10298 display : displays) {
            class_8786<?> recipe = CbRestRecipesClient.byDisplay(display);
            if (recipe != null && !out.contains(recipe.comp_1932().method_29177())) {
                out.add(recipe.comp_1932().method_29177());
            }
        }
        return out;
    }

    /** handleRecipeBookAdd of a 1.21.1 listener class */
    public static void recipeBookAdd(class_2602 listener, class_10266 packet) {
        List<class_10298> all = new ArrayList<>();
        List<class_10298> highlighted = new ArrayList<>();
        for (class_10266.class_10267 entry : packet.comp_3232()) {
            all.add(entry.comp_3233().comp_3262());
            if (entry.method_64562()) {
                highlighted.add(entry.comp_3233().comp_3262());
            }
        }
        ClientboundRecipePacket.State state = packet.comp_3294() ? ClientboundRecipePacket.State.INIT : ClientboundRecipePacket.State.ADD;
        ((LegacyRecipeListener) listener).method_11115(new ClientboundRecipePacket(state, ids(all),
            state == ClientboundRecipePacket.State.INIT ? ids(highlighted) : List.of(), settings()));
    }

    /** handleRecipeBookRemove of a 1.21.1 listener class */
    public static void recipeBookRemove(class_2602 listener, class_10268 packet) {
        ((LegacyRecipeListener) listener).method_11115(new ClientboundRecipePacket(ClientboundRecipePacket.State.REMOVE, ids(packet.comp_3235()), List.of(), settings()));
    }

    /** handleRecipeBookSettings of a 1.21.1 listener class: the settings, as 1.21.1's recipe packets carried them */
    public static void recipeBookSettings(class_2602 listener, class_10269 packet) {
        ((LegacyRecipeListener) listener).method_11115(new ClientboundRecipePacket(ClientboundRecipePacket.State.ADD, List.of(), List.of(), packet.comp_3236()));
    }

    /** handleEntityPositionSync of a 1.21.1 listener class: the teleport packet of the same values */
    public static void entityPositionSync(class_2602 listener, class_10264 packet) {
        listener.method_11086(new class_2777(packet.comp_3223(), packet.comp_3224(), Set.of(), packet.comp_3225()));
    }

    /** handleSetCursorItem of a 1.21.1 listener class: 1.21.1's container-slot packet of the carried item (container -1) */
    public static void setCursorItem(class_2602 listener, class_9834 packet) {
        listener.method_11109(new class_2653(-1, 0, -1, packet.comp_2890()));
    }

    /** handleSetPlayerInventory of a 1.21.1 listener class: 1.21.1's container-slot packet of the inventory (container -2) */
    public static void setPlayerInventory(class_2602 listener, class_9835 packet) {
        listener.method_11109(new class_2653(-2, 0, packet.comp_2891(), packet.comp_2892()));
    }

    /** handleMinecartAlongTrack of a 1.21.1 listener class: no 1.21.1 message said this, so its listener receives nothing */
    public static void minecartAlongTrack(class_2602 listener, class_9833 packet) {
    }

    /** handleRotatePlayer of a 1.21.1 listener class: no 1.21.1 message said this, so its listener receives nothing */
    public static void rotatePlayer(class_2602 listener, class_10265 packet) {
    }
}
