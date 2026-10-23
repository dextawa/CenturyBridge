package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.function.Function;

/**
 * 1.20.4's chat serialization members on 1.20.5's (area s4fin network):
 * <ul>
 * <li>Component.Serializer / SerializerAdapter / HoverEvent: 1.20.5 threads the registries through component
 *     JSON (a component can name registry entries); 1.20.4's calls take the running world's (CbRegistries).</li>
 * <li>SignedMessageChain.DecodeException: 1.20.4's said whether the failure disconnects the player; 1.20.5's
 *     never does (the player is told in red). The flag is kept on the exception (CbNetDecodeExceptionMixin) and
 *     honoured where the server handles the failure (CbNetDecodeFailureMixin); 1.20.5's own exceptions keep
 *     1.20.5's choice.</li>
 * <li>NumberFormatType: 1.20.4's readFromStream / writeToStream are 1.20.5's streamCodec(), both ways.</li>
 * </ul>
 */
public final class CbNetChat {

    // ---------------------------------------------------------------- Component.Serializer / HoverEvent

    /** Component.Serializer.toJsonTree(component) (1.20.5: package-private, with the registries): the same tree */
    public static com.google.gson.JsonElement toJsonTree(net.minecraft.class_2561 component) {
        return com.google.gson.JsonParser.parseString(net.minecraft.class_2561.class_2562.method_10867(component, CbRegistries.current()));
    }

    /** HoverEvent.Action's legacy converter: 1.20.4's took the component only */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2568.class_9341 legacyConverter(Function converter) {
        return (component, ops) -> (com.mojang.serialization.DataResult) converter.apply(component);
    }

    /** the registry ops 1.20.4's EntityTooltipInfo.legacyCreate(component) parses with: JSON over the running world's registries */
    public static net.minecraft.class_6903<com.google.gson.JsonElement> jsonOps() {
        return CbRegistries.current().method_57093(com.mojang.serialization.JsonOps.INSTANCE);
    }

    // ---------------------------------------------------------------- SignedMessageChain.DecodeException

    /** DecodeException (CbNetDecodeExceptionMixin): 1.20.4's shouldDisconnect(), overridable as 1.20.4's was */
    public interface Disconnects {
        boolean method_46314();

        void cbS4net$disconnects(boolean disconnects);
    }

    /** after 1.20.4's constructor (post): the exception keeps whether it disconnects */
    public static void decodeBuilt(net.minecraft.class_7610.class_7825 e, boolean disconnects) {
        ((Disconnects) (Object) e).cbS4net$disconnects(disconnects);
    }

    // ---------------------------------------------------------------- NumberFormatType

    /** a mod NumberFormatType with 1.20.4's readFromStream / writeToStream */
    public interface LegacyFormatType {
        net.minecraft.class_9022 method_55462(net.minecraft.class_2540 buf);

        void method_55461(net.minecraft.class_2540 buf, net.minecraft.class_9022 format);
    }

    /** a mod NumberFormatType's 1.20.4 network form as its streamCodec() */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9139 streamCodec(LegacyFormatType type) {
        return net.minecraft.class_9139.method_56437(
            (buf, format) -> type.method_55461((net.minecraft.class_2540) buf, (net.minecraft.class_9022) format),
            buf -> type.method_55462((net.minecraft.class_2540) buf));
    }

    /** type.readFromStream(buf) on any type: its streamCodec()'s decode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9022 method_55462(net.minecraft.class_9023 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_9022) type.method_56360().decode(CbNetBufs.cbRegistryBuf(buf));
    }

    /** type.writeToStream(buf, format) on any type: its streamCodec()'s encode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_55461(net.minecraft.class_9023 type, net.minecraft.class_2540 buf, net.minecraft.class_9022 format) {
        type.method_56360().encode(CbNetBufs.cbRegistryBuf(buf), format);
    }

    /** NumberFormatTypes.writeToStream(buf, format)'s 1.20.4 body: the type's registry id, then the type's own form (STREAM_CODEC) */
    public static void writeFormat(net.minecraft.class_2540 buf, net.minecraft.class_9022 format) {
        net.minecraft.class_9024.field_48544.encode(CbNetBufs.cbRegistryBuf(buf), format);
    }

    /** NumberFormatTypes.readFromStream(buf) */
    public static net.minecraft.class_9022 readFormat(net.minecraft.class_2540 buf) {
        return net.minecraft.class_9024.field_48544.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    private CbNetChat() {
    }
}
