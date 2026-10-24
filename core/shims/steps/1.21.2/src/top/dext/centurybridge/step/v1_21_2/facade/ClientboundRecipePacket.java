package top.dext.centurybridge.step.v1_21_2.facade;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeNet;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipesHolder;

/**
 * ClientboundRecipePacket of 1.21.1 (net/minecraft/class_2713, gone in 1.21.2; classRenamed): the player's
 * recipe book changing -- INIT (every known recipe and the highlighted ones), ADD (newly unlocked, highlighted
 * and announced), REMOVE -- with the book settings. 1.21.2 sends ClientboundRecipeBookAddPacket (display entries,
 * replacing the book or adding to it, each flagged highlighted / announced), ClientboundRecipeBookRemovePacket
 * (display ids) and ClientboundRecipeBookSettingsPacket.
 *
 * Its constructor, getters, state and 1.21.1 stream codec are 1.21.1's. Sent, it goes out as the 1.21.2 packets
 * that say the same (CbRestOutgoingPacketMixin: a bundle of the add / remove packet of the recipes' displays, as
 * the server's RecipeManager lists them, and the settings packet); handled, those packets are handled.
 */
public class ClientboundRecipePacket implements net.minecraft.class_2596<net.minecraft.class_2602>, CbRestRecipeNet.Outgoing {

    /** STREAM_CODEC of 1.21.1 (its wire format) */
    public static final net.minecraft.class_9139<net.minecraft.class_2540, ClientboundRecipePacket> field_47958 =
        net.minecraft.class_2596.method_56443(ClientboundRecipePacket::write, ClientboundRecipePacket::new);
    /** GamePacketTypes.CLIENTBOUND_RECIPE of 1.21.1 (no 1.21.2 protocol carries it: the packet is sent as 1.21.2's packets) */
    public static final net.minecraft.class_9145<ClientboundRecipePacket> CB_TYPE =
        new net.minecraft.class_9145<>(net.minecraft.class_2598.field_11942, net.minecraft.class_2960.method_60656("recipe"));

    /** GamePacketTypes.CLIENTBOUND_RECIPE of 1.21.1 (fieldGetters) */
    public static net.minecraft.class_9145<ClientboundRecipePacket> field_48073() {
        return CB_TYPE;
    }

    private final State state;
    private final List<net.minecraft.class_2960> recipes;
    private final List<net.minecraft.class_2960> toHighlight;
    private final net.minecraft.class_5411 bookSettings;

    public ClientboundRecipePacket(State state, Collection<net.minecraft.class_2960> recipes, Collection<net.minecraft.class_2960> toHighlight,
                                   net.minecraft.class_5411 bookSettings) {
        this.state = state;
        this.recipes = ImmutableList.copyOf(recipes);
        this.toHighlight = ImmutableList.copyOf(toHighlight);
        this.bookSettings = bookSettings;
    }

    public ClientboundRecipePacket(net.minecraft.class_2540 buf) {
        this.state = buf.method_10818(State.class);
        this.bookSettings = net.minecraft.class_5411.field_54548.decode(buf);
        this.recipes = buf.method_34066(net.minecraft.class_2540::method_10810);
        this.toHighlight = this.state == State.INIT ? buf.method_34066(net.minecraft.class_2540::method_10810) : ImmutableList.of();
    }

    private void write(net.minecraft.class_2540 buf) {
        buf.method_10817(this.state);
        net.minecraft.class_5411.field_54548.encode(buf, this.bookSettings);
        buf.method_34062(this.recipes, net.minecraft.class_2540::method_10812);
        if (this.state == State.INIT) {
            buf.method_34062(this.toHighlight, net.minecraft.class_2540::method_10812);
        }
    }

    @Override
    public net.minecraft.class_9145<ClientboundRecipePacket> method_55846() {
        return CB_TYPE;
    }

    @Override
    public void method_11054(net.minecraft.class_2602 listener) {
        this.method_11753(listener);
    }

    /** handle(listener): the 1.21.2 packets it stands for */
    public void method_11753(net.minecraft.class_2602 listener) {
        ((net.minecraft.class_8042) this.cb$toW()).method_11054(listener);
    }

    /** getRecipes() */
    public List<net.minecraft.class_2960> method_11750() {
        return this.recipes;
    }

    /** getHighlights() */
    public List<net.minecraft.class_2960> method_11757() {
        return this.toHighlight;
    }

    /** getBookSettings() */
    public net.minecraft.class_5411 method_11756() {
        return this.bookSettings;
    }

    /** getState() */
    public State method_11751() {
        return this.state;
    }

    /**
     * The 1.21.2 packets saying the same, from the server's RecipeManager (the displays of each recipe): INIT a
     * replacing add (highlighted as listed, no announcement -- 1.21.1's INIT announced nothing), ADD an add
     * (highlighted, announced when the recipe shows a notification, as 1.21.1's client announced it), REMOVE a remove
     * of the displays; then the settings, which 1.21.1's client applied with every recipe packet.
     */
    @Override
    public net.minecraft.class_2596<?> cb$toW() {
        net.minecraft.server.MinecraftServer server = CbRestRecipesHolder.server();
        if (server == null) {
            throw new IllegalStateException("CenturyBridge: a 1.21.1 ClientboundRecipePacket outside a running server: 1.21.2 names a recipe"
                + " in the recipe book by its displays, which only the server's RecipeManager lists");
        }
        net.minecraft.class_1863 manager = server.method_3772();
        List<net.minecraft.class_2596<? super net.minecraft.class_2602>> out = new ArrayList<>();
        if (this.state == State.REMOVE) {
            List<net.minecraft.class_10298> ids = new ArrayList<>();
            for (net.minecraft.class_2960 id : this.recipes) {
                manager.method_64679(key(id), entry -> ids.add(entry.comp_3262()));
            }
            out.add(new net.minecraft.class_10268(ids));
        } else {
            List<net.minecraft.class_10266.class_10267> entries = new ArrayList<>();
            for (net.minecraft.class_2960 id : this.recipes) {
                boolean highlight = this.state == State.ADD || this.toHighlight.contains(id);
                boolean announce = this.state == State.ADD && manager.method_8130(key(id)).map(h -> h.comp_1933().method_49188()).orElse(false);
                manager.method_64679(key(id), entry -> entries.add(new net.minecraft.class_10266.class_10267(entry, announce, highlight)));
            }
            out.add(new net.minecraft.class_10266(entries, this.state == State.INIT));
        }
        out.add(new net.minecraft.class_10269(this.bookSettings));
        return new net.minecraft.class_8042(out);
    }

    private static net.minecraft.class_5321<net.minecraft.class_1860<?>> key(net.minecraft.class_2960 id) {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52178, id);
    }

    /** ClientboundRecipePacket.State of 1.21.1 (class_2713$class_2714) */
    public enum State {
        INIT, ADD, REMOVE;

        public static final State field_12416 = INIT;
        public static final State field_12415 = ADD;
        public static final State field_12417 = REMOVE;
    }
}
