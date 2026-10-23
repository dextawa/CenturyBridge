package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's game event API on 1.20.5: position source types read and wrote themselves on a byte buffer
 * (read / write, PositionSourceType.fromNetwork / toNetwork), 1.20.5's have stream codecs of the same
 * bytes; GameEvent was a class with an intrusive registry holder, 1.20.5's is a record the registry holds;
 * the vibration resonance events were an array of events, 1.20.5's a list of keys.
 */
public final class CbRestGameEvents {

    /** 1.20.4 PositionSourceType.read / write, the shapes a mod's type implements */
    public interface LegacyType {
        net.minecraft.class_5716 method_32962(net.minecraft.class_2540 buf);

        void method_32960(net.minecraft.class_2540 buf, net.minecraft.class_5716 source);
    }

    /** PositionSourceType.read(buf) called on any type: its stream codec's decode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5716 method_32962(net.minecraft.class_5717 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_5716) ((net.minecraft.class_9139) type.method_56123()).decode(buf);
    }

    /** PositionSourceType.write(buf, source) called on any type: its stream codec's encode */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_32960(net.minecraft.class_5717 type, net.minecraft.class_2540 buf, net.minecraft.class_5716 source) {
        ((net.minecraft.class_9139) type.method_56123()).encode(buf, source);
    }

    /** BlockPositionSource.Type.read(buf) */
    public static net.minecraft.class_5707 method_32929(net.minecraft.class_5707.class_5708 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_5707) method_32962(type, buf);
    }

    /** BlockPositionSource.Type.write(buf, source) */
    public static void method_32930(net.minecraft.class_5707.class_5708 type, net.minecraft.class_2540 buf, net.minecraft.class_5707 source) {
        method_32960(type, buf, source);
    }

    /** EntityPositionSource.Type.read(buf) */
    public static net.minecraft.class_5709 method_32934(net.minecraft.class_5709.class_5710 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_5709) method_32962(type, buf);
    }

    /** EntityPositionSource.Type.write(buf, source) */
    public static void method_32935(net.minecraft.class_5709.class_5710 type, net.minecraft.class_2540 buf, net.minecraft.class_5709 source) {
        method_32960(type, buf, source);
    }

    /** 1.20.4 PositionSourceType.fromNetwork(buf): the type by registry id, then its read */
    public static net.minecraft.class_5716 method_32963(net.minecraft.class_2540 buf) {
        net.minecraft.class_5717<?> type = net.minecraft.class_7923.field_41191.method_10200(buf.method_10816());
        if (type == null) {
            throw new IllegalArgumentException("Unknown position source type");
        }
        return method_32962(type, buf);
    }

    /** 1.20.4 PositionSourceType.toNetwork(source, buf): the type's registry id, then its write */
    public static void method_32958(net.minecraft.class_5716 source, net.minecraft.class_2540 buf) {
        net.minecraft.class_5717<?> type = source.method_32955();
        int id = net.minecraft.class_7923.field_41191.method_10206(type);
        if (id == -1) {
            throw new IllegalArgumentException("Can't find id for '" + type + "' in map " + net.minecraft.class_7923.field_41191);
        }
        buf.method_10804(id);
        method_32960(type, buf, source);
    }

    /** streamCodec() of a mod's 1.20.4 position source type (adapter hook): its own read and write */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9139 streamCodec(LegacyType type) {
        return net.minecraft.class_9139.method_56437(
            (buf, source) -> type.method_32960(asFriendly(buf), (net.minecraft.class_5716) source),
            buf -> type.method_32962(asFriendly(buf)));
    }

    private static net.minecraft.class_2540 asFriendly(Object buf) {
        return buf instanceof net.minecraft.class_2540 f ? f : new net.minecraft.class_2540((io.netty.buffer.ByteBuf) buf);
    }

    /** 1.20.4 BlockPositionSource.Type's read / write, the shapes a mod's subclass overrides */
    public interface LegacyBlockRead {
        net.minecraft.class_5707 method_32929(net.minecraft.class_2540 buf);
    }

    public interface LegacyBlockWrite {
        void method_32930(net.minecraft.class_2540 buf, net.minecraft.class_5707 source);
    }

    /** 1.20.4 EntityPositionSource.Type's read / write, the shapes a mod's subclass overrides */
    public interface LegacyEntityRead {
        net.minecraft.class_5709 method_32934(net.minecraft.class_2540 buf);
    }

    public interface LegacyEntityWrite {
        void method_32935(net.minecraft.class_2540 buf, net.minecraft.class_5709 source);
    }

    /** both shapes (a subclass overriding read and write) */
    public interface LegacyBlockType extends LegacyBlockRead, LegacyBlockWrite {
        @Override
        net.minecraft.class_5707 method_32929(net.minecraft.class_2540 buf);

        @Override
        void method_32930(net.minecraft.class_2540 buf, net.minecraft.class_5707 source);
    }

    public interface LegacyEntityType extends LegacyEntityRead, LegacyEntityWrite {
        @Override
        net.minecraft.class_5709 method_32934(net.minecraft.class_2540 buf);

        @Override
        void method_32935(net.minecraft.class_2540 buf, net.minecraft.class_5709 source);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static net.minecraft.class_9139 codec(net.minecraft.class_5717 vanillaType, java.util.function.BiConsumer write,
                                                  java.util.function.Function read) {
        net.minecraft.class_9139 vanilla = vanillaType.method_56123();
        return net.minecraft.class_9139.method_56437(
            (buf, source) -> {
                if (write != null) {
                    write.accept(asFriendly(buf), source);
                } else {
                    vanilla.encode(buf, source);
                }
            },
            buf -> read != null ? read.apply(asFriendly(buf)) : vanilla.decode(buf));
    }

    /**
     * streamCodec() of a mod's BlockPositionSource.Type subclass overriding 1.20.4's read and write
     * (adapter hooks): its own; overriding one of them: its own for that one, 1.20.5's block source codec
     * (1.20.4's body) for the other
     */
    public static net.minecraft.class_9139 blockCodec(LegacyBlockType type) {
        return codec(net.minecraft.class_5717.field_28185,
            (buf, source) -> type.method_32930((net.minecraft.class_2540) buf, (net.minecraft.class_5707) source),
            buf -> type.method_32929((net.minecraft.class_2540) buf));
    }

    public static net.minecraft.class_9139 blockCodec(LegacyBlockRead type) {
        return codec(net.minecraft.class_5717.field_28185, null, buf -> type.method_32929((net.minecraft.class_2540) buf));
    }

    public static net.minecraft.class_9139 blockCodec(LegacyBlockWrite type) {
        return codec(net.minecraft.class_5717.field_28185,
            (buf, source) -> type.method_32930((net.minecraft.class_2540) buf, (net.minecraft.class_5707) source), null);
    }

    /** the same for an EntityPositionSource.Type subclass */
    public static net.minecraft.class_9139 entityCodec(LegacyEntityType type) {
        return codec(net.minecraft.class_5717.field_28186,
            (buf, source) -> type.method_32935((net.minecraft.class_2540) buf, (net.minecraft.class_5709) source),
            buf -> type.method_32934((net.minecraft.class_2540) buf));
    }

    public static net.minecraft.class_9139 entityCodec(LegacyEntityRead type) {
        return codec(net.minecraft.class_5717.field_28186, null, buf -> type.method_32934((net.minecraft.class_2540) buf));
    }

    public static net.minecraft.class_9139 entityCodec(LegacyEntityWrite type) {
        return codec(net.minecraft.class_5717.field_28186,
            (buf, source) -> type.method_32935((net.minecraft.class_2540) buf, (net.minecraft.class_5709) source), null);
    }

    /** a mod type's super.read(buf): 1.20.4's body, the block source codec of the registered type */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5707 superRead(net.minecraft.class_5707.class_5708 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_5707) ((net.minecraft.class_9139) net.minecraft.class_5717.field_28185.method_56123()).decode(buf);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void superWrite(net.minecraft.class_5707.class_5708 type, net.minecraft.class_2540 buf, net.minecraft.class_5707 source) {
        ((net.minecraft.class_9139) net.minecraft.class_5717.field_28185.method_56123()).encode(buf, source);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5709 superRead(net.minecraft.class_5709.class_5710 type, net.minecraft.class_2540 buf) {
        return (net.minecraft.class_5709) ((net.minecraft.class_9139) net.minecraft.class_5717.field_28186.method_56123()).decode(buf);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void superWrite(net.minecraft.class_5709.class_5710 type, net.minecraft.class_2540 buf, net.minecraft.class_5709 source) {
        ((net.minecraft.class_9139) net.minecraft.class_5717.field_28186.method_56123()).encode(buf, source);
    }

    /** 1.20.4 GameEvent.is(tag): the registered event's holder in the tag */
    public static boolean method_40156(net.minecraft.class_5712 event, net.minecraft.class_6862<net.minecraft.class_5712> tag) {
        return net.minecraft.class_7923.field_41171.method_47983(event).method_40220(tag);
    }

    /** 1.20.4 GameEvent.builtInRegistryHolder(): the registered event's reference */
    public static net.minecraft.class_6880.class_6883<net.minecraft.class_5712> method_40157(net.minecraft.class_5712 event) {
        net.minecraft.class_6880<net.minecraft.class_5712> h = net.minecraft.class_7923.field_41171.method_47983(event);
        if (h instanceof net.minecraft.class_6880.class_6883<net.minecraft.class_5712> reference) {
            return reference;
        }
        throw new IllegalStateException("game event " + event + " is not registered: 1.20.5 has no holder of it before then");
    }

    /** 1.20.4 VibrationSystem.getGameEventFrequency(event): 1.20.4's frequency table (CbRestGeneric) */
    public static int method_51385(net.minecraft.class_5712 event) {
        return CbRestGeneric.field_44639().applyAsInt(event);
    }

    private static volatile net.minecraft.class_5712[] resonance;

    /** 1.20.4 VibrationSystem.RESONANCE_EVENTS: 1.20.5's resonance events as the events registered under their keys */
    public static net.minecraft.class_5712[] field_44638() {
        net.minecraft.class_5712[] r = resonance;
        if (r == null) {
            java.util.List<net.minecraft.class_5321<net.minecraft.class_5712>> keys = net.minecraft.class_8514.field_44638;
            r = new net.minecraft.class_5712[keys.size()];
            for (int i = 0; i < r.length; i++) {
                r[i] = net.minecraft.class_7923.field_41171.method_29107(keys.get(i));
            }
            resonance = r;
        }
        return r;
    }

    private CbRestGameEvents() {
    }
}
