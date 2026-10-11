package top.dext.centurybridge.rt.v1_20_2;

import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import net.minecraft.class_2505;
import net.minecraft.class_2520;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_2658;
import net.minecraft.class_2817;
import net.minecraft.class_2960;
import net.minecraft.class_8039;
import net.minecraft.class_8710;

/**
 * Server-safe bridges for the 1.20.1 -> 1.20.2 networking rework (area s1net): custom payloads,
 * the per-direction protocol codecs, the netty pipeline handlers, FriendlyByteBuf helpers.
 * Member names follow the wiring: a static/instance redirect keeps the 1.20.1 member name (the
 * receiver comes first for instance shapes), constructor factories and converters are cb*.
 * Client-only bridges live in CbNetClient.
 */
public final class CbNet {

    // ---------------------------------------------------------------- custom payloads

    /**
     * new CustomPayloadS2CPacket(Identifier, PacketByteBuf): 1.20.2 packets carry a CustomPayload;
     * CbRawPayload is the old pair (channel + the bytes the mod encoded), written verbatim. The
     * 1.20.1 constructor refused data past 1048576 bytes; so does this.
     */
    public static class_2658 cbNewClientboundPayloadPacket(class_2960 id, class_2540 data) {
        return new class_2658(cbClientboundPayload(id, data));
    }

    /** the payload for a 1.20.1 (id, data) clientbound packet: super(id, data) in a subclass, or a NEW site */
    public static class_8710 cbClientboundPayload(class_2960 id, class_2540 data) {
        if (data.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        return new CbRawPayload(id, data);
    }

    /** the payload for a 1.20.1 (id, data) serverbound packet (the 1.20.1 constructor checked no size) */
    public static class_8710 cbServerboundPayload(class_2960 id, class_2540 data) {
        return new CbRawPayload(id, data);
    }

    /** CustomPayload*Packet.getChannel(): the payload's id is the channel the packet travels on */
    public static class_2960 cbChannel(class_8710 payload) {
        return payload.comp_1678();
    }

    /**
     * CustomPayload*Packet.getData(): 1.20.1 answered a fresh buffer holding a copy of the packet's
     * bytes on every call. A 1.20.2 payload writes exactly those bytes (the wire form after the
     * channel id) -- vanilla's typed payloads, Fabric API's retained ones and CbRawPayload alike.
     */
    public static class_2540 cbData(class_8710 payload) {
        class_2540 out = new class_2540(Unpooled.buffer());
        payload.method_53028(out);
        return out;
    }

    /** whether Fabric API's networking module retains unknown payloads itself (its PayloadHelper.readCustom) */
    public static final boolean FABRIC_RETAINS_PAYLOADS =
        net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("fabric-networking-api-v1");

    /**
     * A payload on a channel 1.20.2 does not know. Vanilla 1.20.2 skips its bytes (DiscardedPayload)
     * where 1.20.1 kept them for getData(); this reads them the way the 1.20.1 packet constructor
     * did: every remaining byte, refused past the packet's size cap.
     */
    public static class_8710 cbRetainUnknown(class_2960 id, class_2540 buf, int maxSize) {
        int n = buf.readableBytes();
        if (n < 0 || n > maxSize) {
            throw new IllegalArgumentException("Payload may not be larger than " + maxSize + " bytes");
        }
        return new CbRawPayload(id, new class_2540(buf.readBytes(n)));
    }

    // ---------------------------------------------------------------- protocols

    /**
     * NetworkState.getId(): the 1.20.1 integer ids (HANDSHAKING -1, PLAY 0, STATUS 1, LOGIN 2,
     * read off 1.20.1's enum initializer). 1.20.2 answers a name instead and added CONFIGURATION,
     * a state 1.20.1 never had and so has no id for.
     */
    public static int method_10785(class_2539 state) {
        if (state == class_2539.field_20590) {
            return -1;
        }
        if (state == class_2539.field_20591) {
            return 0;
        }
        if (state == class_2539.field_20592) {
            return 1;
        }
        if (state == class_2539.field_20593) {
            return 2;
        }
        throw new IllegalStateException("CenturyBridge: protocol " + state.method_10785()
            + " did not exist in 1.20.1 and has no 1.20.1 id");
    }

    /** NetworkState.byId(int): 1.20.1 answered null outside -1..2 */
    public static class_2539 method_10782(int id) {
        return switch (id) {
            case -1 -> class_2539.field_20590;
            case 0 -> class_2539.field_20591;
            case 1 -> class_2539.field_20592;
            case 2 -> class_2539.field_20593;
            default -> null;
        };
    }

    /**
     * NetworkState.getPacketHandlerState(Packet): the state a packet class belongs to, null when
     * none. 1.20.2 lists the common packets (keep-alive, custom payload, ...) in both CONFIGURATION
     * and PLAY and the ping packets in both STATUS and PLAY; 1.20.1 had each class in exactly one
     * state -- PLAY for the common ones, STATUS for the ping ones -- which this search order gives.
     */
    public static class_2539 method_10786(class_2596<?> packet) {
        class_2539[] order = {class_2539.field_20590, class_2539.field_20592, class_2539.field_20593,
            class_2539.field_20591, class_2539.field_45671};
        for (class_2539 state : order) {
            for (class_2598 flow : class_2598.values()) {
                if (state.method_52921(flow).method_52925(packet) != -1) {
                    return state;
                }
            }
        }
        return null;
    }

    /** NetworkState.getPacketId(side, packet): the id in that direction's codec, -1 when unregistered */
    public static int method_10781(class_2539 state, class_2598 flow, class_2596<?> packet) {
        return state.method_52921(flow).method_52925(packet);
    }

    /** NetworkState.getPacketHandler(side, id, buf): decoded by that direction's codec, null for an unknown id */
    public static class_2596<?> method_10783(class_2539 state, class_2598 flow, int id, class_2540 buf) {
        return state.method_52921(flow).method_52923(id, buf);
    }

    /** NetworkState.getBundlerInfo(side): the bundler of that direction's codec */
    public static class_8039 method_48312(class_2539 state, class_2598 flow) {
        return state.method_52921(flow).method_48312();
    }

    /**
     * BundlerInfo.Provider.getBundlerInfo(side): 1.20.1's providers were the protocol states, asked
     * for one direction; 1.20.2's are each direction's codec, asked without one. A codec answers
     * for the requested direction through its protocol; any other provider answers for itself.
     */
    public static class_8039 method_48312(class_8039.class_8041 provider, class_2598 flow) {
        if (provider instanceof class_2539.class_8698<?> codec) {
            return codec.method_52922().method_52921(flow).method_48312();
        }
        return provider.method_48312();
    }

    /**
     * The channel attribute that holds one direction's protocol codec (Connection.method_52908, private):
     * the handler a 1.20.1 pipeline built for side X -- PacketDecoder(X), PacketEncoder(X),
     * PacketBundler(X), PacketUnbundler(X) -- reads the codec of direction X in 1.20.2, exactly as
     * vanilla's own configureSerialization pairs them.
     */
    public static AttributeKey<class_2539.class_8698<?>> cbProtocolKey(class_2598 flow) {
        return flow == class_2598.field_11942 ? class_2535.field_45667 : class_2535.field_45666;
    }

    /** the bandwidth monitor a 1.20.1 pipeline never had: none (every 1.20.2 user checks for null) */
    public static net.minecraft.class_8762 cbNoBandwidthMonitor() {
        return null;
    }

    // ---------------------------------------------------------------- FriendlyByteBuf

    /** PacketByteBuf.getVarIntSize(int), moved to VarInt.getByteSize (same algorithm) */
    public static int method_10815(int value) {
        return net.minecraft.class_8703.method_53015(value);
    }

    /** PacketByteBuf.getVarLongSize(long), moved to VarLong.getByteSize (same algorithm) */
    public static int method_36129(long value) {
        return net.minecraft.class_8704.method_53019(value);
    }

    /** the size accounting 1.20.1's readUnlimitedNbt / decode(ops, codec) read with: NbtTagSizeTracker.EMPTY, unlimited */
    public static class_2505 cbUnlimitedNbt() {
        return class_2505.method_53898();
    }

    /**
     * 1.20.1 readNbt(tracker) answered a compound (or null for an empty tag). 1.20.2 reads any root
     * tag; a non-compound root is refused the way 1.20.2's own compound read (readNbt()) refuses it.
     */
    public static net.minecraft.class_2487 cbNbtCompound(class_2520 tag) {
        if (tag == null || tag instanceof net.minecraft.class_2487) {
            return (net.minecraft.class_2487) tag;
        }
        throw new io.netty.handler.codec.DecoderException("Not a compound tag: " + tag);
    }

    /**
     * PacketByteBuf.decode(ops, codec): 1.20.1 read the tag unlimited (readUnlimitedNbt); 1.20.2's
     * decode takes the size accounting to read with. Called through the new shape, so a mod
     * subclass overriding decode is still the one that answers.
     */
    public static <T> T method_29171(class_2540 buf, com.mojang.serialization.DynamicOps<class_2520> ops,
            com.mojang.serialization.Codec<T> codec) {
        return buf.method_29171(ops, codec, cbUnlimitedNbt());
    }

    /** PacketByteBuf.readNbt(tracker): the tag 1.20.2 reads, held to 1.20.1's compound-or-null */
    public static net.minecraft.class_2487 method_30616(class_2540 buf, class_2505 tracker) {
        return cbNbtCompound(buf.method_30616(tracker));
    }

    /**
     * PacketByteBuf.encode(ops, codec, value) returns the buffer in 1.20.2 (void in 1.20.1): the
     * new shape vanilla calls on a mod subclass runs the subclass's 1.20.1 override, then answers
     * the buffer as 1.20.2's own encode does.
     */
    public static class_2540 cbWriteWithCodec(CbNetLegacyCodecWriter self, com.mojang.serialization.DynamicOps<class_2520> ops,
            com.mojang.serialization.Codec<Object> codec, Object value) {
        self.method_29172(ops, codec, value);
        return (class_2540) self;
    }

    /** LegacyQueryHandler.FAKE_PROTOCOL_VERSION (a 1.20.1 constant, 127; gone from 1.20.2) */
    public static final int field_29771 = 127;

    /**
     * LegacyQueryHandler(ServerNetworkIo): 1.20.1 read the MOTD, player counts and version off
     * networkIo.getServer(); 1.20.2 takes that server (the ServerInfo it implements) directly.
     */
    public static net.minecraft.class_8599 cbQueryServer(net.minecraft.class_3242 networkIo) {
        return networkIo.method_14351();
    }

    // ---------------------------------------------------------------- login queries

    /** LoginQueryRequestS2CPacket(id, channel, data): the 1.20.1 channel and buffer as a query payload */
    public static net.minecraft.class_8595 cbQueryPayload(class_2960 channel, class_2540 data) {
        return new CbRawQueryPayload(channel, data);
    }

    /** LoginQueryResponseC2SPacket(id, response): a null response (1.20.1's "not understood") stays a null payload */
    public static net.minecraft.class_8594 cbQueryAnswer(class_2540 response) {
        return response == null ? null : new CbRawQueryAnswerPayload(response);
    }

    /** 1.20.1's LoginQueryResponseC2SPacket(PacketByteBuf) read constructor: the query id, then the response */
    public static net.minecraft.class_2913 cbReadQueryAnswerPacket(class_2540 buf) {
        int id = cbReadQueryId(buf);
        return new net.minecraft.class_2913(id, cbReadQueryAnswer(buf));
    }

    /** a subclass's super(buf) of that read constructor, first argument: the query id */
    public static int cbReadQueryId(class_2540 buf) {
        return buf.method_10816();
    }

    /**
     * ... and second: 1.20.1 read the response as readNullable(every remaining byte, refused past
     * 1048576), vanilla 1.20.2 skips it (DiscardedQueryAnswerPayload).
     */
    public static net.minecraft.class_8594 cbReadQueryAnswer(class_2540 buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        int n = buf.readableBytes();
        if (n < 0 || n > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        return new CbRawQueryAnswerPayload(new class_2540(buf.readBytes(n)));
    }

    /** a login query request on a channel 1.20.2 does not know: every remaining byte, as 1.20.1 read it */
    public static net.minecraft.class_8595 cbRetainQuery(class_2960 channel, class_2540 buf) {
        int n = buf.readableBytes();
        if (n < 0 || n > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        return new CbRawQueryPayload(channel, new class_2540(buf.readBytes(n)));
    }

    /** the buffers getPayload() / getResponse() answered for one received query packet (1.20.1 handed out its own field) */
    private static final java.util.Map<Object, class_2540> QUERY_DATA = new com.google.common.collect.MapMaker().weakKeys().makeMap();

    /**
     * LoginQueryRequestS2CPacket.getPayload(): 1.20.1 handed back the buffer the packet held -- the
     * same object on every call. A payload built from a 1.20.1 buffer answers that buffer; any other
     * (Fabric API's, vanilla's) is written once into a buffer that this packet then keeps answering.
     */
    public static class_2540 cbQueryData(Object packet, net.minecraft.class_8595 payload) {
        if (payload instanceof CbRawQueryPayload raw) {
            return raw.data();
        }
        return QUERY_DATA.computeIfAbsent(packet, p -> {
            class_2540 out = new class_2540(Unpooled.buffer());
            payload.method_52296(out);
            return out;
        });
    }

    /** LoginQueryResponseC2SPacket.getResponse(): as cbQueryData, null for a null response */
    public static class_2540 cbQueryAnswerData(Object packet, net.minecraft.class_8594 payload) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof CbRawQueryAnswerPayload raw) {
            return raw.data();
        }
        return QUERY_DATA.computeIfAbsent(packet, p -> {
            class_2540 out = new class_2540(Unpooled.buffer());
            payload.method_52295(out);
            return out;
        });
    }

    // ---------------------------------------------------------------- packets turned into records

    /** UnloadChunkS2CPacket(x, z): 1.20.2 carries the ChunkPos */
    public static net.minecraft.class_1923 cbChunkPos(int x, int z) {
        return new net.minecraft.class_1923(x, z);
    }

    /**
     * PlayerRespawnS2CPacket's 1.20.1 constructor, all but the flags byte: 1.20.2 bundles exactly these
     * values, in this order, as CommonPlayerSpawnInfo (the byte stays a component of the packet).
     */
    public static net.minecraft.class_8589 cbSpawnInfo(net.minecraft.class_5321<net.minecraft.class_2874> dimensionType,
            net.minecraft.class_5321<net.minecraft.class_1937> dimension, long seed, net.minecraft.class_1934 gameMode,
            net.minecraft.class_1934 previousGameMode, boolean debug, boolean flat,
            java.util.Optional<net.minecraft.class_4208> lastDeath, int portalCooldown) {
        return new net.minecraft.class_8589(dimensionType, dimension, seed, gameMode, previousGameMode, debug, flat,
            lastDeath, portalCooldown);
    }

    /** HandshakeC2SPacket(address, port, intent): 1.20.1 stamped the running game's protocol version */
    public static int cbProtocolVersion() {
        return net.minecraft.class_155.method_16673().method_48020();
    }

    /**
     * The handshake's next state: 1.20.2 names the only two a client can ask for (ClientIntent STATUS,
     * LOGIN). 1.20.1 took any state, but its server refused all others ("Invalid intention").
     */
    public static net.minecraft.class_8592 cbIntent(class_2539 state) {
        if (state == class_2539.field_20592) {
            return net.minecraft.class_8592.field_44974;
        }
        if (state == class_2539.field_20593) {
            return net.minecraft.class_8592.field_44975;
        }
        throw new IllegalArgumentException("CenturyBridge: a 1.20.2 handshake can only ask for STATUS or LOGIN, not "
            + state.method_10785());
    }

    /** ClientSettingsC2SPacket's 1.20.1 components, which 1.20.2 bundles (same order) as SyncedClientOptions */
    public static net.minecraft.class_8791 cbClientInformation(String language, int viewDistance,
            net.minecraft.class_1659 chatVisibility, boolean chatColors, int modelParts, net.minecraft.class_1306 mainArm,
            boolean filterText, boolean allowsListing) {
        return new net.minecraft.class_8791(language, viewDistance, chatVisibility, chatColors, modelParts, mainArm,
            filterText, allowsListing);
    }

    /** ClientSettingsC2SPacket.language() */
    public static String comp_266(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1951();
    }

    /** .viewDistance() */
    public static int comp_267(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1952();
    }

    /** .chatVisibility() */
    public static net.minecraft.class_1659 comp_268(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1953();
    }

    /** .chatColors() */
    public static boolean comp_269(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1954();
    }

    /** .playerModelBitMask() */
    public static int comp_270(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1955();
    }

    /** .mainArm() */
    public static net.minecraft.class_1306 comp_271(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1956();
    }

    /** .filtersText() */
    public static boolean comp_272(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1957();
    }

    /** .allowsServerListing() */
    public static boolean comp_273(net.minecraft.class_2803 p) {
        return p.comp_1963().comp_1958();
    }

    /** ClientSettingsC2SPacket.MAX_LANGUAGE_LENGTH (16), moved to SyncedClientOptions */
    public static final int field_33360 = net.minecraft.class_8791.field_46155;

    // ---------------------------------------------------------------- scoreboard display slot

    /**
     * ScoreboardDisplayS2CPacket's int slot -> 1.20.2's ScoreboardDisplaySlot: the enum's ids are
     * 1.20.1's slot numbers (0 list, 1 sidebar, 2 below name, 3.. team sidebars).
     */
    public static net.minecraft.class_8646 cbDisplaySlot(int slot) {
        return net.minecraft.class_8646.field_45176.apply(slot);
    }

    /** ... and back, for 1.20.1's int getSlot() */
    public static int cbSlotId(net.minecraft.class_8646 slot) {
        return slot.method_52621();
    }

    // ---------------------------------------------------------------- recipe packets

    /**
     * A 1.20.1 recipe where 1.20.2 wants its RecipeEntry: the id comes from the recipe manager the
     * recipe was loaded or synced into (Trackers, as for Recipe.getId()).
     */
    public static net.minecraft.class_8786<?> cbRecipeEntry(net.minecraft.class_1860<?> recipe) {
        return new net.minecraft.class_8786<>(Statics.method_8114(recipe), recipe);
    }

    /** SynchronizeRecipesS2CPacket.writeRecipe(buf, recipe): 1.20.2 writes the RecipeEntry -- serializer, id, body -- as 1.20.1 did */
    public static void method_17816(class_2540 buf, net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_2788.method_17816(buf, cbRecipeEntry(recipe));
    }

    // ---------------------------------------------------------------- server play handler

    /**
     * ServerPlayNetworkHandler(server, connection, player): 1.20.2 also takes the connection's
     * CommonListenerCookie, of which it reads the latency. A handler made the 1.20.1 way starts as a
     * fresh connection does -- latency 0, default client options (createInitial), the player's profile.
     */
    public static net.minecraft.class_8792 cbCookieFor(net.minecraft.class_3222 player) {
        return net.minecraft.class_8792.method_53824(player.method_7334());
    }

    private CbNet() {
    }
}
