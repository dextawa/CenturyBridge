package top.dext.centurybridge.step.v1_20_5.rt;

import java.time.Instant;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import top.dext.centurybridge.step.v1_20_5.facade.ChatTypeBoundNetwork;
import top.dext.centurybridge.step.v1_20_5.facade.FactorData;

/**
 * 1.20.4's game, login and configuration packet members on 1.20.5's (area s4fin network), the ones the
 * packet generator (gen_packet_bridges.py) leaves to hand-written wiring because a value changed type:
 * <ul>
 * <li>ClientboundUpdateMobEffectPacket: 1.20.4 carried the instance's factor data (darkness' fade); 1.20.5
 *     carries only whether the client should blend the effect in. A packet built with 1.20.4's constructor
 *     blends when the instance's fade has not settled (still fading in or out), and keeps the factor data
 *     it was built from for getFactorData(); a decoded packet answers the factor data the 1.20.5 client
 *     makes from it (a new instance, blending skipped unless asked).</li>
 * <li>Values 1.20.5 wraps: sound event, mob effect, game event, damage type and dimension type holders;
 *     nullable components and number formats as Optional; ChatType.Bound with a holder and an optional
 *     target name, ChatType.BoundNetwork gone (facade ChatTypeBoundNetwork).</li>
 * <li>Arguments 1.20.5 added that 1.20.4 decided elsewhere: the login packet's enforcesSecureChat (1.20.4
 *     sent it in the server data packet; 1.20.5's PlayerList passes the server's enforceSecureProfile()),
 *     the hello packet's shouldAuthenticate (1.20.4 sent a hello only to clients it authenticated).</li>
 * <li>ServerboundChatCommandPacket: 1.20.5 split 1.20.4's packet into the unsigned command and
 *     ServerboundChatCommandSignedPacket.</li>
 * </ul>
 * Registry values that live in a world's registries (chat types, damage types, dimension types) resolve
 * against the running world's registries (CbRegistries), as 1.20.4 resolved them against the registry
 * access the packet's handler had.
 */
public final class CbNetGame {

    // ---------------------------------------------------------------- ClientboundUpdateMobEffectPacket

    /** MobEffectInstance (CbNetEffectBlendMixin): whether its 1.20.5 blend state has reached its target */
    public interface BlendSlot {
        boolean cbS4net$blendSettled();

        void cbS4net$blendSettled(boolean settled);
    }

    /** ClientboundUpdateMobEffectPacket (CbNetMobEffectPacketMixin): the factor data 1.20.4's constructor took */
    public interface MobEffectPacketSlot {
        FactorData cbS4net$factorData();

        void cbS4net$factorData(FactorData data);

        boolean cbS4net$built();

        void cbS4net$built(boolean built);
    }

    /** CbNetEffectBlendMixin: the blend state ticked or set; settled when its factor is at its target */
    public static void blendSettled(net.minecraft.class_1293 instance, boolean settled) {
        ((BlendSlot) (Object) instance).cbS4net$blendSettled(settled);
    }

    /**
     * new ClientboundUpdateMobEffectPacket(id, instance)'s blend flag: 1.20.4 sent the instance's factor data
     * so the client went on fading from where the server was; 1.20.5 can only say "blend in" or "already in
     * full". Blend while the fade has not settled -- the instance's own factor data (an effect a 1.20.4 mod
     * gave a factory) or, for an effect 1.20.5 blends itself, its blend state. An effect with no fade at all
     * does not blend.
     */
    public static boolean mobEffectBlend(net.minecraft.class_1293 instance) {
        FactorData own = ((CbEffects.InstanceSlot) (Object) instance).cbS4$factorData();
        if (own != null) {
            return !(own.field_42107 >= own.field_38086 && own.field_38088 == own.field_38087);
        }
        if (instance.method_5579().comp_349().method_55652() <= 0) {
            return false;
        }
        return !((BlendSlot) (Object) instance).cbS4net$blendSettled();
    }

    /** after 1.20.4's constructor (post): the packet keeps the factor data it was built from, as 1.20.4's did */
    @SuppressWarnings("rawtypes")
    public static void mobEffectBuilt(net.minecraft.class_2783 packet, net.minecraft.class_1293 instance) {
        MobEffectPacketSlot slot = (MobEffectPacketSlot) (Object) packet;
        Optional d = CbEffects.getFactorData(instance);
        slot.cbS4net$factorData(d == null ? null : (FactorData) d.orElse(null));
        slot.cbS4net$built(true);
    }

    /**
     * getFactorData() (CbNetMobEffectPacketMixin): a packet built by 1.20.4's constructor answers the factor
     * data it took; a decoded (or 1.20.5-built) one answers the factor data of the instance the 1.20.5
     * client builds from it -- new instance, blending skipped unless the packet asks to blend.
     */
    @SuppressWarnings("rawtypes")
    public static FactorData mobEffectFactorData(net.minecraft.class_2783 packet) {
        MobEffectPacketSlot slot = (MobEffectPacketSlot) (Object) packet;
        if (slot.cbS4net$built()) {
            return slot.cbS4net$factorData();
        }
        net.minecraft.class_1293 instance = new net.minecraft.class_1293(packet.method_11946(), packet.method_11944(),
            packet.method_11945(), packet.method_11950(), packet.method_11949(), packet.method_11942(), null);
        if (!packet.method_55629()) {
            instance.method_55657();
        }
        Optional d = CbEffects.getFactorData(instance);
        return d == null ? null : (FactorData) d.orElse(null);
    }

    /** getEffectAmplifier()'s 1.20.4 byte, as 1.20.4 code read it (sign-extended where it widened it) */
    public static int amplifierInt(byte amplifier) {
        return amplifier;
    }

    // ---------------------------------------------------------------- Optional <-> nullable

    public static Optional<net.minecraft.class_2561> optionalComponent(net.minecraft.class_2561 c) {
        return Optional.ofNullable(c);
    }

    public static net.minecraft.class_2561 componentOrNull(Optional<net.minecraft.class_2561> c) {
        return c.orElse(null);
    }

    public static Optional<net.minecraft.class_9022> optionalNumberFormat(net.minecraft.class_9022 f) {
        return Optional.ofNullable(f);
    }

    public static net.minecraft.class_9022 numberFormatOrNull(Optional<net.minecraft.class_9022> f) {
        return f.orElse(null);
    }

    // ---------------------------------------------------------------- world registry values

    /** ClientboundDamageEventPacket's 1.20.4 damage type id (the world's DAMAGE_TYPE registry) -> holder */
    public static net.minecraft.class_6880<net.minecraft.class_8110> damageType(int id) {
        net.minecraft.class_2378<net.minecraft.class_8110> r =
            CbRegistries.currentAccess().method_30530(net.minecraft.class_7924.field_42534);
        return r.method_40265(id).orElseThrow(() -> new IllegalArgumentException("no damage type with id " + id));
    }

    /** holder -> the 1.20.4 damage type id */
    public static int damageTypeId(net.minecraft.class_6880<net.minecraft.class_8110> holder) {
        return CbRegistries.currentAccess().method_30530(net.minecraft.class_7924.field_42534).method_10206(holder.comp_349());
    }

    /** CommonPlayerSpawnInfo's 1.20.4 dimension type key -> holder */
    public static net.minecraft.class_6880<net.minecraft.class_2874> dimensionType(net.minecraft.class_5321<net.minecraft.class_2874> key) {
        return CbRegistries.currentAccess().method_30530(net.minecraft.class_7924.field_41241).method_40290(key);
    }

    /** holder -> the 1.20.4 dimension type key */
    public static net.minecraft.class_5321<net.minecraft.class_2874> dimensionTypeKey(net.minecraft.class_6880<net.minecraft.class_2874> holder) {
        return holder.method_40230().orElseThrow(() -> new IllegalStateException("dimension type not registered: " + holder));
    }

    /** a chat type value -> its holder in the world's CHAT_TYPE registry */
    public static net.minecraft.class_6880<net.minecraft.class_2556> chatType(net.minecraft.class_2556 type) {
        return chatTypes(CbRegistries.currentAccess()).method_47983(type);
    }

    static net.minecraft.class_2378<net.minecraft.class_2556> chatTypes(net.minecraft.class_5455 access) {
        return access.method_30530(net.minecraft.class_7924.field_41237);
    }

    public static net.minecraft.class_2556 chatTypeValue(net.minecraft.class_6880<net.minecraft.class_2556> holder) {
        return holder.comp_349();
    }

    // ---------------------------------------------------------------- ChatType.Bound / BoundNetwork

    /** ChatType.bind(name) (1.20.4): bound to this chat type, no target name */
    public static net.minecraft.class_2556.class_7602 bind(net.minecraft.class_2556 type, net.minecraft.class_2561 name) {
        return new net.minecraft.class_2556.class_7602(chatType(type), name, Optional.empty());
    }

    /** Bound.toNetwork(registryAccess): the chat type's id in the given registries */
    public static ChatTypeBoundNetwork toNetwork(net.minecraft.class_2556.class_7602 bound, net.minecraft.class_5455 access) {
        return new ChatTypeBoundNetwork(chatTypes(access).method_10206(bound.comp_919().comp_349()), bound.comp_920(),
            bound.comp_921().orElse(null));
    }

    /** a packet's 1.20.4 BoundNetwork -> the Bound 1.20.5 carries (resolved in the running world's registries) */
    public static net.minecraft.class_2556.class_7602 bound(ChatTypeBoundNetwork network) {
        return network.method_44840(CbRegistries.currentAccess())
            .orElseThrow(() -> new IllegalArgumentException("no chat type with id " + network.comp_922()));
    }

    /** a packet's Bound -> 1.20.4's BoundNetwork (ids in the running world's registries) */
    public static ChatTypeBoundNetwork boundNetwork(net.minecraft.class_2556.class_7602 bound) {
        return toNetwork(bound, CbRegistries.currentAccess());
    }

    // ---------------------------------------------------------------- arguments 1.20.5 added

    /** ClientboundLoginPacket's enforcesSecureChat: the running server's enforceSecureProfile(), as 1.20.5's PlayerList passes */
    public static boolean enforcesSecureChat() {
        net.minecraft.server.MinecraftServer s = CbRegistries.server();
        return s != null && s.method_43500();
    }

    /** ClientboundHelloPacket's shouldAuthenticate: 1.20.4 sent a hello only to a client it went on to authenticate */
    public static boolean helloAuthenticates() {
        return true;
    }

    // ---------------------------------------------------------------- the rest

    /** ClientboundBlockEntityDataPacket.create(be, getter): 1.20.4's getter took no registries */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BiFunction blockEntityTag(Function getter) {
        return (be, registries) -> getter.apply(be);
    }

    /**
     * ClientboundUpdateRecipesPacket.toNetwork(buf, recipe)'s 1.20.4 body: the serializer's key, the recipe's
     * id, then the serializer's own network form (its stream codec, which 1.20.4 serializers' toNetwork feed).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void recipeToNetwork(net.minecraft.class_2540 buf, net.minecraft.class_8786 holder) {
        net.minecraft.class_1865 serializer = holder.comp_1933().method_8119();
        buf.method_10812(net.minecraft.class_7923.field_41189.method_10221(serializer));
        buf.method_10812(holder.comp_1932());
        serializer.method_56104().encode(CbNetBufs.cbRegistryBuf(buf), holder.comp_1933());
    }

    /** ClientboundExplodePacket.writeParticle(buf, particle)'s 1.20.4 body: type id and options (ParticleTypes.STREAM_CODEC) */
    public static void writeParticle(net.minecraft.class_2540 buf, net.minecraft.class_2394 particle) {
        net.minecraft.class_2398.field_48456.encode(CbNetBufs.cbRegistryBuf(buf), particle);
    }

    // ---------------------------------------------------------------- ServerboundChatCommandPacket

    /**
     * ServerboundChatCommandPacket (CbNetChatCommandMixin): 1.20.4's signing fields. When it was made (1.20.4's
     * timestamp); for one standing in for 1.20.5's signed command packet (a 1.20.4 listener's handleChatCommand),
     * that packet's timestamp, salt, signatures and acknowledgements.
     */
    public interface ChatCommandSlot {
        Instant cbS4net$timeStamp();

        /** the signed packet this one stands in for, or null */
        net.minecraft.class_9449 cbS4net$signed();

        void cbS4net$signed(net.minecraft.class_9449 signed);
    }

    private static ChatCommandSlot slot(net.minecraft.class_7472 packet) {
        return (ChatCommandSlot) (Object) packet;
    }

    /**
     * new ServerboundChatCommandPacket(command, timeStamp, salt, argumentSignatures, lastSeenMessages): with no
     * argument signatures it is 1.20.5's (unsigned) command packet, which 1.20.5's client sends for exactly that
     * case. Signed arguments go in ServerboundChatCommandSignedPacket in 1.20.5, a record of another type that
     * this constructor cannot answer.
     */
    public static net.minecraft.class_7472 chatCommand(String command, Instant timeStamp, long salt,
                                                       net.minecraft.class_7450 signatures,
                                                       net.minecraft.class_7635.class_7636 lastSeen) {
        if (!signatures.comp_912().isEmpty()) {
            throw new IllegalArgumentException("1.20.5 sends a command with signed arguments as ServerboundChatCommandSignedPacket,"
                + " not as ServerboundChatCommandPacket");
        }
        return new net.minecraft.class_7472(command);
    }

    /** 1.20.5's signed command packet as 1.20.4's ServerboundChatCommandPacket (its fields carried along) */
    public static net.minecraft.class_7472 chatCommandOf(net.minecraft.class_9449 signed) {
        net.minecraft.class_7472 packet = new net.minecraft.class_7472(signed.comp_2532());
        slot(packet).cbS4net$signed(signed);
        return packet;
    }

    /** timeStamp(): a signed packet's; else when the packet was made (1.20.4's client stamped Instant.now() as it made it) */
    public static Instant chatCommandTimeStamp(net.minecraft.class_7472 packet) {
        net.minecraft.class_9449 signed = slot(packet).cbS4net$signed();
        return signed != null ? signed.comp_2533() : slot(packet).cbS4net$timeStamp();
    }

    /** salt(): a signed packet's; an unsigned command's salt signs nothing */
    public static long chatCommandSalt(net.minecraft.class_7472 packet) {
        net.minecraft.class_9449 signed = slot(packet).cbS4net$signed();
        return signed != null ? signed.comp_2534() : 0L;
    }

    /** argumentSignatures(): a signed packet's; an unsigned command has none */
    public static net.minecraft.class_7450 chatCommandSignatures(net.minecraft.class_7472 packet) {
        net.minecraft.class_9449 signed = slot(packet).cbS4net$signed();
        return signed != null ? signed.comp_2535() : net.minecraft.class_7450.field_39807;
    }

    /** lastSeenMessages(): a signed packet's; an unsigned command acknowledges nothing (1.20.5's unsigned path updates no chat state) */
    public static net.minecraft.class_7635.class_7636 chatCommandLastSeen(net.minecraft.class_7472 packet) {
        net.minecraft.class_9449 signed = slot(packet).cbS4net$signed();
        return signed != null ? signed.comp_2536() : new net.minecraft.class_7635.class_7636(0, new BitSet());
    }

    // ---------------------------------------------------------------- ClientboundPlayerInfoUpdatePacket.Action.Writer

    /** 1.20.4's Action.Writer shape: write(FriendlyByteBuf, entry) */
    @FunctionalInterface
    public interface LegacyInfoWriter {
        void write(net.minecraft.class_2540 buf, net.minecraft.class_2703.class_2705 entry);
    }

    /** a 1.20.4 Writer lambda as 1.20.5's (the RegistryFriendlyByteBuf 1.20.5 passes is a FriendlyByteBuf) */
    public static net.minecraft.class_2703.class_5893.class_7830 infoWriter(LegacyInfoWriter legacy) {
        return legacy::write;
    }

    private CbNetGame() {
    }
}
