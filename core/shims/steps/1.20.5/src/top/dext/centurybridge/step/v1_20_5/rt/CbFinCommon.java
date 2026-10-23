package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * Small 1.20.4 members across the server, commands, sounds and value providers (area s4fin, non-client
 * remainder) whose 1.20.5 form takes an argument 1.20.4 did not have, or a value of another type:
 * <ul>
 * <li>Command registration and the component / style arguments take a CommandBuildContext in 1.20.5: the
 *     running server's registries and enabled features, else the running world's registries with vanilla's
 *     features (what 1.20.4's argument types resolved against).</li>
 * <li>A connection's "transferred" flag (1.20.5's server transfers): 1.20.4 had no transfers, so false.</li>
 * <li>Codec fields 1.20.5 turned into MapCodecs: the same codec as a Codec (CbCodecs, one per MapCodec).</li>
 * </ul>
 */
public final class CbFinCommon {

    /** the CommandBuildContext 1.20.4's command code is registered / parsed with */
    public static net.minecraft.class_7157 commandContext() {
        net.minecraft.server.MinecraftServer server = CbRegistries.server();
        if (server != null) {
            return net.minecraft.class_7157.method_46722(server.method_30611(), server.method_27728().method_45560());
        }
        return net.minecraft.class_7157.method_46722(CbRegistries.current(), net.minecraft.class_7701.field_40182);
    }

    /** a 1.20.4 connection, login listener or cookie was never a transfer (1.20.4 had none) */
    public static boolean notTransferred() {
        return false;
    }

    /** a callback 1.20.4's call had no place for (ServerStatusPinger.pingServer's ping-complete): nothing to run */
    public static Runnable noCallback() {
        return () -> {
        };
    }

    /**
     * StructureCheck.checkStart(pos, structure, skipKnown): 1.20.5 also applies the placement's frequency reduction
     * and exclusion zone there, which 1.20.4's callers applied themselves before asking; a placement with neither
     * (frequency 1, no exclusion zone) leaves 1.20.4's check as it was
     */
    public static net.minecraft.class_6874 unrestrictedPlacement() {
        return new net.minecraft.class_6872(1, 0, net.minecraft.class_6873.field_36421, 0);
    }

    /** ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE: 1.20.4's 6 blocks, squared */
    public static double field_37280() {
        return 36.0;
    }

    // ---------------------------------------------------------------- SoundEvent

    /** SoundEvent.writeToNetwork(buf): location and optional fixed range (1.20.5's DIRECT_STREAM_CODEC writes the same) */
    public static void method_47958(net.minecraft.class_3414 sound, net.minecraft.class_2540 buf) {
        net.minecraft.class_3414.field_48278.encode(buf, sound);
    }

    /** SoundEvent.readFromNetwork(buf) */
    public static net.minecraft.class_3414 method_47961(net.minecraft.class_2540 buf) {
        return net.minecraft.class_3414.field_48278.decode(buf);
    }

    // ---------------------------------------------------------------- StoringChunkProgressListener(radius)

    /** 1.20.4's StoringChunkProgressListener(radius) fields: the logging listener for the radius */
    public static net.minecraft.class_3951 progressLogger(int radius) {
        return net.minecraft.class_3951.method_56044(radius);
    }

    /** ... the diameter it covers */
    public static int progressDiameter(int radius) {
        return radius * 2 + 1;
    }

    /** ... the radius of every status it tracks */
    public static int progressMaxRadius(int radius) {
        return radius + net.minecraft.class_2806.method_12155();
    }

    /** ... and its diameter */
    public static int progressMaxDiameter(int radius) {
        return progressMaxRadius(radius) * 2 + 1;
    }

    // ---------------------------------------------------------------- MapCodec fields, as 1.20.4's Codecs

    public static com.mojang.serialization.Codec field_33443() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6333.field_33443);
    }

    public static com.mojang.serialization.Codec field_33446() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6334.field_33446);
    }

    public static com.mojang.serialization.Codec field_28998() {
        return CbCodecs.fromMapCodec(net.minecraft.class_5861.field_28998);
    }

    public static com.mojang.serialization.Codec field_35352() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6728.field_35352);
    }

    public static com.mojang.serialization.Codec field_29004() {
        return CbCodecs.fromMapCodec(net.minecraft.class_5862.field_29004);
    }

    public static com.mojang.serialization.Codec field_29943() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6016.field_29943);
    }

    public static com.mojang.serialization.Codec field_29012() {
        return CbCodecs.fromMapCodec(net.minecraft.class_5865.field_29012);
    }

    public static com.mojang.serialization.Codec field_29016() {
        return CbCodecs.fromMapCodec(net.minecraft.class_5866.field_29016);
    }

    public static com.mojang.serialization.Codec field_29949() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6019.field_29949);
    }

    public static com.mojang.serialization.Codec field_31537() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6121.field_31537);
    }

    public static com.mojang.serialization.Codec field_33772() {
        return CbCodecs.fromMapCodec(net.minecraft.class_6378.field_33772);
    }

    /** BiomeFilter.CODEC = codec (1.20.4 left the field writable; 1.20.5's is a MapCodec) */
    @SuppressWarnings("unchecked")
    public static void field_35714(com.mojang.serialization.Codec codec) {
        net.minecraft.class_6792.field_35714 = CbCodecs.toMapCodec(codec);
    }

    private CbFinCommon() {
    }
}
