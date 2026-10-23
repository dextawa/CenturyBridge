package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import top.dext.centurybridge.step.v1_20_5.facade.ParticleOptionsDeserializer;

/**
 * 1.20.4's particle API on 1.20.5's. In 1.20.4 a particle type carried a Deserializer (options from a
 * command's text after the type id, and from the network) and a codec, and every options object wrote
 * itself to the network (writeToNetwork) and to text (writeToString, the command form 1.20.4 also
 * persisted particles in). 1.20.5 types carry a map codec and a stream codec instead, and options no
 * longer write themselves.
 * <ul>
 * <li>A type made the 1.20.4 way -- a converted ParticleType subclass (its super constructor's
 *     deserializer is kept, {@link #pend}/{@link #claim}), or {@link #legacyType} (Fabric API 1.20.4's
 *     FabricParticleTypes.complex) -- gets 1.20.5's stream codec from its deserializer and its options'
 *     own writeToNetwork, and its map codec from its 1.20.4 codec, or (none: FabricParticleTypes had
 *     none) from the command form, as 1.20.4 stored particles ({@link #commandForm}).</li>
 * <li>A 1.20.4 call of writeToNetwork / writeToString / getDeserializer answers 1.20.4's: a converted
 *     options class's own method (adapters {@link WriteNetwork} / {@link WriteString}), else the body
 *     1.20.4's vanilla class had -- its wire format and its text -- else, for an options class 1.20.4
 *     never had, 1.20.5's own codecs (the stream codec; the type id and the options as SNBT).</li>
 * <li>Vanilla's 1.20.4 deserializers ({@link #BLOCK} ...) read what those bodies write, so a 1.20.4
 *     mod's packet or text round-trips as it did.</li>
 * </ul>
 * Item and position-source payloads go through 1.20.5's stream codecs (1.20.4 wrote an item as NBT,
 * a position source by its type's id): both sides of a converted mod use the same pair.
 */
public final class CbParticles {

    /** a 1.20.4 ParticleOptions class declaring writeToNetwork (adapter) */
    public interface WriteNetwork {
        void method_10294(net.minecraft.class_2540 buf);
    }

    /** a 1.20.4 ParticleOptions class declaring writeToString (adapter) */
    public interface WriteString {
        String method_10293();
    }

    /** a 1.20.4 ParticleType class declaring getDeserializer (adapter) */
    public interface GetDeserializer {
        @SuppressWarnings("rawtypes")
        ParticleOptionsDeserializer method_10298();
    }

    /** a 1.20.4 ParticleType class declaring codec() as 1.20.4's Codec (override hook adapter) */
    public interface LegacyCodec {
        @SuppressWarnings("rawtypes")
        Codec method_29138();
    }

    /** ParticleType (CbParticleTypeMixin): the 1.20.4 deserializer the type was made with, if any */
    public interface DeserializerSlot {
        @SuppressWarnings("rawtypes")
        ParticleOptionsDeserializer cbS4$deserializer();

        @SuppressWarnings("rawtypes")
        void cbS4$deserializer(ParticleOptionsDeserializer deserializer);
    }

    // ---------------------------------------------------------------- types made the 1.20.4 way

    /** a converted super(overrideLimiter, deserializer)'s deserializer, until ParticleType's constructor returns */
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<ParticleOptionsDeserializer> PENDING = new ThreadLocal<>();

    /** new ParticleType(overrideLimiter, deserializer) -> (overrideLimiter): the deserializer kept for the type */
    @SuppressWarnings("rawtypes")
    public static boolean pend(boolean overrideLimiter, ParticleOptionsDeserializer deserializer) {
        PENDING.set(deserializer);
        return overrideLimiter;
    }

    /** CbParticleTypeMixin, ParticleType's constructor returning: the type takes the deserializer its converted constructor gave */
    @SuppressWarnings("rawtypes")
    public static void claim(net.minecraft.class_2396 type) {
        ParticleOptionsDeserializer d = PENDING.get();
        if (d != null) {
            PENDING.remove();
            ((DeserializerSlot) type).cbS4$deserializer(d);
        }
    }

    /**
     * A type as 1.20.4's new ParticleType(overrideLimiter, deserializer) with no codec of its own made it --
     * Fabric API 1.20.4's FabricParticleTypes.complex(alwaysSpawn, factory).
     */
    public static <T extends net.minecraft.class_2394> net.minecraft.class_2396<T> legacyType(boolean overrideLimiter,
                                                                                            ParticleOptionsDeserializer<T> deserializer) {
        return new LegacyType<>(overrideLimiter, deserializer);
    }

    /** {@link #legacyType}'s type */
    public static final class LegacyType<T extends net.minecraft.class_2394> extends net.minecraft.class_2396<T> {
        private final MapCodec<T> codec;
        private final net.minecraft.class_9139<net.minecraft.class_9129, T> streamCodec;

        LegacyType(boolean overrideLimiter, ParticleOptionsDeserializer<T> deserializer) {
            super(overrideLimiter);
            ((DeserializerSlot) (Object) this).cbS4$deserializer(deserializer);
            this.codec = commandForm(this);
            this.streamCodec = streamCodecOf(this);
        }

        @Override
        public MapCodec<T> method_29138() {
            return codec;
        }

        @Override
        public net.minecraft.class_9139<net.minecraft.class_9129, T> method_56179() {
            return streamCodec;
        }
    }

    /**
     * codec() of a converted type that declares 1.20.4's codec() (the override hook's bridge): its codec as
     * 1.20.4's dispatch read it (CbCodecs.toMapCodec), else -- it had none -- its command form.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static MapCodec codec(LegacyCodec type) {
        Codec c = type.method_29138();
        return c != null ? CbCodecs.toMapCodec(c) : commandForm((net.minecraft.class_2396) (Object) type);
    }

    /** streamCodec() of a converted ParticleType subclass (abstract fill): its deserializer and its options' writeToNetwork */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9139 streamCodec(net.minecraft.class_2396 type) {
        return streamCodecOf(type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends net.minecraft.class_2394> net.minecraft.class_9139<net.minecraft.class_9129, T> streamCodecOf(
            net.minecraft.class_2396<T> type) {
        return net.minecraft.class_9139.method_56437(
            (buf, options) -> writeToNetwork((net.minecraft.class_2394) options, (net.minecraft.class_2540) buf),
            buf -> (T) deserializer(type).method_10297(type, (net.minecraft.class_2540) buf));
    }

    /**
     * 1.20.4's persistent form of a particle that has no codec: the command form -- what writeToString wrote
     * after the type's id, read back by the type's deserializer, as 1.20.4's AreaEffectCloud kept its particle.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends net.minecraft.class_2394> MapCodec<T> commandForm(net.minecraft.class_2396<T> type) {
        return Codec.STRING.optionalFieldOf("legacy_arguments", "").flatXmap(
            args -> {
                try {
                    return DataResult.success((T) deserializer(type).method_10296(type, new StringReader(args)));
                } catch (CommandSyntaxException e) {
                    return DataResult.error(() -> "1.20.4 particle " + typeId(type) + " arguments '" + args + "': " + e.getMessage());
                }
            },
            options -> {
                String text = writeToString(options);
                String id = typeId(type);
                return text.startsWith(id) ? DataResult.success(text.substring(id.length()))
                    : DataResult.error(() -> "1.20.4 particle text '" + text + "' does not start with its type " + id);
            });
    }

    private static String typeId(net.minecraft.class_2396<?> type) {
        return String.valueOf(net.minecraft.class_7923.field_41180.method_10221(type));
    }

    // ---------------------------------------------------------------- getDeserializer

    /** type.getDeserializer(): the class's own override, else 1.20.4's (see {@link #superDeserializer}) */
    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer deserializer(net.minecraft.class_2396 type) {
        return type instanceof GetDeserializer g ? g.method_10298() : superDeserializer(type);
    }

    /**
     * ParticleType.getDeserializer()'s answer: the deserializer the type was made with (a 1.20.4-made type),
     * else vanilla's 1.20.4 one for a vanilla type, else one reading 1.20.5's own forms (the SNBT of the map
     * codec after the type id, the stream codec).
     */
    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer superDeserializer(net.minecraft.class_2396 type) {
        ParticleOptionsDeserializer own = ((DeserializerSlot) type).cbS4$deserializer();
        return own != null ? own : vanillaDeserializer(type);
    }

    @SuppressWarnings("rawtypes")
    private static ParticleOptionsDeserializer vanillaDeserializer(net.minecraft.class_2396 type) {
        if (type instanceof net.minecraft.class_2400) {
            return SIMPLE;
        }
        if (type == net.minecraft.class_2398.field_11217 || type == net.minecraft.class_2398.field_35434
                || type == net.minecraft.class_2398.field_11206 || type == net.minecraft.class_2398.field_50248) {
            return BLOCK;
        }
        if (type == net.minecraft.class_2398.field_11212) {
            return DUST;
        }
        if (type == net.minecraft.class_2398.field_28276) {
            return DUST_COLOR_TRANSITION;
        }
        if (type == net.minecraft.class_2398.field_11218) {
            return ITEM;
        }
        if (type == net.minecraft.class_2398.field_38003) {
            return SCULK_CHARGE;
        }
        if (type == net.minecraft.class_2398.field_38357) {
            return SHRIEK;
        }
        if (type == net.minecraft.class_2398.field_28275) {
            return VIBRATION;
        }
        return CODECS;
    }

    /** super.getDeserializer() in a mod subclass of ParticleType */
    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer superGetDeserializer(net.minecraft.class_2396 type) {
        return superDeserializer(type);
    }

    // ---------------------------------------------------------------- vanilla's 1.20.4 deserializers

    /** SimpleParticleType.DESERIALIZER: the type is its own options */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer SIMPLE = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) {
            return (net.minecraft.class_2400) type;
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return (net.minecraft.class_2400) type;
        }
    };

    /** BlockParticleOption.DESERIALIZER */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer BLOCK = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new net.minecraft.class_2388(type,
                net.minecraft.class_2259.method_41955(net.minecraft.class_7923.field_41175.method_46771(), reader, false).comp_622());
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return new net.minecraft.class_2388(type, (net.minecraft.class_2680) buf.method_52962(net.minecraft.class_2248.field_10651::method_10200));
        }
    };

    /** DustParticleOptions.DESERIALIZER */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer DUST = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            org.joml.Vector3f color = readVector3f(reader);
            reader.expect(' ');
            float scale = reader.readFloat();
            return new net.minecraft.class_2390(color, scale);
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return new net.minecraft.class_2390(readVector3f(buf), buf.readFloat());
        }
    };

    /** DustColorTransitionOptions.DESERIALIZER */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer DUST_COLOR_TRANSITION = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            org.joml.Vector3f from = readVector3f(reader);
            reader.expect(' ');
            float scale = reader.readFloat();
            org.joml.Vector3f to = readVector3f(reader);
            return new net.minecraft.class_5743(from, to, scale);
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            org.joml.Vector3f from = readVector3f(buf);
            float scale = buf.readFloat();
            org.joml.Vector3f to = readVector3f(buf);
            return new net.minecraft.class_5743(from, to, scale);
        }
    };

    /** 1.20.4 ItemParser's unknown item error */
    private static final com.mojang.brigadier.exceptions.DynamicCommandExceptionType UNKNOWN_ITEM =
        new com.mojang.brigadier.exceptions.DynamicCommandExceptionType(id -> net.minecraft.class_2561.method_43469("argument.item.id.invalid", id));

    /** ItemParticleOption.DESERIALIZER: 1.20.4's item syntax, an id and the item's 1.20.4 NBT */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer ITEM = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int start = reader.getCursor();
            net.minecraft.class_2960 id = net.minecraft.class_2960.method_12835(reader);
            if (net.minecraft.class_7923.field_41178.method_17966(id).isEmpty()) {
                reader.setCursor(start);
                throw UNKNOWN_ITEM.createWithContext(reader, id);
            }
            net.minecraft.class_2487 nbt = new net.minecraft.class_2487();
            nbt.method_10582("id", id.toString());
            nbt.method_10567("Count", (byte) 1);
            if (reader.canRead() && reader.peek() == '{') {
                nbt.method_10566("tag", new net.minecraft.class_2522(reader).method_10727());
            }
            return new net.minecraft.class_2392(type, CbItemTags.of(nbt));
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return new net.minecraft.class_2392(type, (net.minecraft.class_1799) net.minecraft.class_1799.field_49268.decode(CbItemHolders.registryBuf(buf)));
        }
    };

    /** SculkChargeParticleOptions.DESERIALIZER */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer SCULK_CHARGE = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new net.minecraft.class_7227(reader.readFloat());
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return new net.minecraft.class_7227(buf.readFloat());
        }
    };

    /** ShriekParticleOption.DESERIALIZER */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer SHRIEK = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new net.minecraft.class_7290(reader.readInt());
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return new net.minecraft.class_7290(buf.method_10816());
        }
    };

    /** VibrationParticleOption.DESERIALIZER: a block position and the ticks until arrival */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer VIBRATION = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            int ticks = reader.readInt();
            return new net.minecraft.class_5745(new net.minecraft.class_5707(net.minecraft.class_2338.method_49637(x, y, z)), ticks);
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            net.minecraft.class_5716 destination = (net.minecraft.class_5716) net.minecraft.class_5716.field_48379.decode(CbItemHolders.registryBuf(buf));
            return new net.minecraft.class_5745(destination, buf.method_10816());
        }
    };

    /** an options class 1.20.4 never had: 1.20.5's own forms (SNBT of the map codec after the id; the stream codec) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ParticleOptionsDeserializer CODECS = new ParticleOptionsDeserializer() {
        @Override
        public net.minecraft.class_2394 method_10296(net.minecraft.class_2396 type, StringReader reader) throws CommandSyntaxException {
            net.minecraft.class_2487 tag = reader.canRead() && reader.peek() == '{'
                ? new net.minecraft.class_2522(reader).method_10727() : new net.minecraft.class_2487();
            DataResult<net.minecraft.class_2394> r = type.method_29138().codec().parse(nbtOps(), tag);
            return r.getOrThrow(message -> new IllegalArgumentException("particle " + typeId(type) + " " + tag + ": " + message));
        }

        @Override
        public net.minecraft.class_2394 method_10297(net.minecraft.class_2396 type, net.minecraft.class_2540 buf) {
            return (net.minecraft.class_2394) type.method_56179().decode(CbItemHolders.registryBuf(buf));
        }
    };

    private static com.mojang.serialization.DynamicOps<net.minecraft.class_2520> nbtOps() {
        return CbRegistries.current().method_57093(net.minecraft.class_2509.field_11560);
    }

    /** BlockParticleOption.DESERIALIZER (fieldGetters) and the other vanilla constants */
    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer blockDeserializer() {
        return BLOCK;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer dustDeserializer() {
        return DUST;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer dustColorTransitionDeserializer() {
        return DUST_COLOR_TRANSITION;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer itemDeserializer() {
        return ITEM;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer sculkChargeDeserializer() {
        return SCULK_CHARGE;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer shriekDeserializer() {
        return SHRIEK;
    }

    @SuppressWarnings("rawtypes")
    public static ParticleOptionsDeserializer vibrationDeserializer() {
        return VIBRATION;
    }

    // ---------------------------------------------------------------- writeToNetwork / writeToString

    /** options.writeToNetwork(buf): the class's own, else its vanilla class's 1.20.4 body, else the type's stream codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void writeToNetwork(net.minecraft.class_2394 options, net.minecraft.class_2540 buf) {
        if (options instanceof WriteNetwork w) {
            w.method_10294(buf);
        } else if (options instanceof net.minecraft.class_2388 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_2390 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_5743 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_2392 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_7227 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_7290 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_5745 o) {
            superWriteToNetwork(o, buf);
        } else if (options instanceof net.minecraft.class_2400 o) {
            superWriteToNetwork(o, buf);
        } else {
            net.minecraft.class_2396 type = options.method_10295();
            if (type instanceof LegacyType || ((DeserializerSlot) type).cbS4$deserializer() != null) {
                throw new IllegalStateException("1.20.4 particle options " + options.getClass().getName()
                    + " of type " + typeId(type) + " declare no writeToNetwork");
            }
            type.method_56179().encode(CbItemHolders.registryBuf(buf), options);
        }
    }

    /** options.writeToString(): the class's own, else its vanilla class's 1.20.4 text, else the type id and the options' SNBT */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String writeToString(net.minecraft.class_2394 options) {
        if (options instanceof WriteString w) {
            return w.method_10293();
        } else if (options instanceof net.minecraft.class_2388 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_2390 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_5743 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_2392 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_7227 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_7290 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_5745 o) {
            return superWriteToString(o);
        } else if (options instanceof net.minecraft.class_2400 o) {
            return superWriteToString(o);
        }
        net.minecraft.class_2396 type = options.method_10295();
        String id = typeId(type);
        DataResult<net.minecraft.class_2520> encoded = type.method_29138().codec().encodeStart(nbtOps(), options);
        net.minecraft.class_2520 tag = encoded.getOrThrow(message -> new IllegalArgumentException("particle " + id + ": " + message));
        return tag instanceof net.minecraft.class_2487 c && c.method_33133() ? id : id + tag;
    }

    // the per-class shapes 1.20.4 code calls (instanceRedirects name the class that declared the method)

    public static void writeToNetwork(net.minecraft.class_2388 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_5743 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_2392 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_7227 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_7290 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_5745 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static void writeToNetwork(net.minecraft.class_2400 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    /** ... of DustParticleOptionsBase, renamed to 1.20.5's ScalableParticleOptionsBase */
    public static void writeToNetwork(net.minecraft.class_9679 o, net.minecraft.class_2540 buf) {
        writeToNetwork((net.minecraft.class_2394) o, buf);
    }

    public static String writeToString(net.minecraft.class_2388 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_5743 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_2392 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_7227 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_7290 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_5745 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_2400 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    public static String writeToString(net.minecraft.class_9679 o) {
        return writeToString((net.minecraft.class_2394) o);
    }

    // 1.20.4's bodies, per vanilla class (a mod subclass's super calls; the vanilla classes' own answers).
    // 1.20.4 wrote each class's own fields, not what a subclass's getter answers: read through accessors.

    private static org.joml.Vector3f dustColorField(net.minecraft.class_2390 o) {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleDustAccessor) o).cbS4$color();
    }

    private static float scaleField(net.minecraft.class_9679 o) {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleScaleAccessor) o).cbS4$scale();
    }

    private static top.dext.centurybridge.step.v1_20_5.mixin.CbParticleTransitionAccessor transition(net.minecraft.class_5743 o) {
        return (top.dext.centurybridge.step.v1_20_5.mixin.CbParticleTransitionAccessor) o;
    }

    private static void writeVector3f(net.minecraft.class_2540 buf, org.joml.Vector3f v) {
        buf.writeFloat(v.x());
        buf.writeFloat(v.y());
        buf.writeFloat(v.z());
    }

    /** BlockParticleOption: the state's id in the block state registry */
    public static void superWriteToNetwork(net.minecraft.class_2388 o, net.minecraft.class_2540 buf) {
        buf.method_52963(net.minecraft.class_2248.field_10651::method_10206, ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleBlockAccessor) o).cbS4$state());
    }

    /** DustParticleOptions (DustParticleOptionsBase's body): the colour and the scale */
    public static void superWriteToNetwork(net.minecraft.class_2390 o, net.minecraft.class_2540 buf) {
        writeVector3f(buf, dustColorField(o));
        buf.writeFloat(scaleField(o));
    }

    /** DustColorTransitionOptions: the colour and scale (DustParticleOptionsBase's), then the colour it turns to */
    public static void superWriteToNetwork(net.minecraft.class_5743 o, net.minecraft.class_2540 buf) {
        writeVector3f(buf, transition(o).cbS4$fromColor());
        buf.writeFloat(scaleField(o));
        writeVector3f(buf, transition(o).cbS4$toColor());
    }

    /**
     * DustParticleOptionsBase's body, for a super call that names the old base (renamed to 1.20.5's
     * ScalableParticleOptionsBase): a converted subclass's (facade) fields, else the vanilla class's fields.
     */
    public static void superWriteToNetwork(net.minecraft.class_9679 o, net.minecraft.class_2540 buf) {
        if (o instanceof top.dext.centurybridge.step.v1_20_5.facade.DustParticleOptionsBase f) {
            writeVector3f(buf, f.field_28273);
            buf.writeFloat(f.field_28274);
        } else if (o instanceof net.minecraft.class_2390 d) {
            superWriteToNetwork(d, buf);
        } else if (o instanceof net.minecraft.class_5743 t) {
            writeVector3f(buf, transition(t).cbS4$fromColor());
            buf.writeFloat(scaleField(t));
        } else {
            throw new UnsupportedOperationException(o.getClass().getName() + " is scalable particle options without a colour"
            + " (1.20.4's DustParticleOptionsBase always had one)");
        }
    }

    /** ItemParticleOption: the item (1.20.5's stream form of a stack) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void superWriteToNetwork(net.minecraft.class_2392 o, net.minecraft.class_2540 buf) {
        ((net.minecraft.class_9139) net.minecraft.class_1799.field_49268).encode(CbItemHolders.registryBuf(buf),
            ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleItemAccessor) o).cbS4$itemStack());
    }

    /** SculkChargeParticleOptions (a record): the roll */
    public static void superWriteToNetwork(net.minecraft.class_7227 o, net.minecraft.class_2540 buf) {
        buf.writeFloat(o.comp_632());
    }

    /** ShriekParticleOption: the delay */
    public static void superWriteToNetwork(net.minecraft.class_7290 o, net.minecraft.class_2540 buf) {
        buf.method_10804(((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleShriekAccessor) o).cbS4$delay());
    }

    /** VibrationParticleOption: the destination (1.20.5's stream form of a position source) and the arrival ticks */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void superWriteToNetwork(net.minecraft.class_5745 o, net.minecraft.class_2540 buf) {
        top.dext.centurybridge.step.v1_20_5.mixin.CbParticleVibrationAccessor v = (top.dext.centurybridge.step.v1_20_5.mixin.CbParticleVibrationAccessor) o;
        ((net.minecraft.class_9139) net.minecraft.class_5716.field_48379).encode(CbItemHolders.registryBuf(buf), v.cbS4$destination());
        buf.method_10804(v.cbS4$arrivalInTicks());
    }

    /** SimpleParticleType: nothing */
    public static void superWriteToNetwork(net.minecraft.class_2400 o, net.minecraft.class_2540 buf) {
    }

    public static String superWriteToString(net.minecraft.class_2388 o) {
        return typeId(o.method_10295()) + " " + net.minecraft.class_2259.method_9685(((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleBlockAccessor) o).cbS4$state());
    }

    private static String dustText(net.minecraft.class_2394 o, org.joml.Vector3f c, float scale) {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", typeId(o.method_10295()), c.x(), c.y(), c.z(), scale);
    }

    public static String superWriteToString(net.minecraft.class_2390 o) {
        return dustText(o, dustColorField(o), scaleField(o));
    }

    public static String superWriteToString(net.minecraft.class_5743 o) {
        org.joml.Vector3f c = transition(o).cbS4$fromColor();
        org.joml.Vector3f to = transition(o).cbS4$toColor();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", typeId(o.method_10295()), c.x(), c.y(), c.z(),
            scaleField(o), to.x(), to.y(), to.z());
    }

    /** DustParticleOptionsBase's text, for a super call naming the old base (see the network form) */
    public static String superWriteToString(net.minecraft.class_9679 o) {
        if (o instanceof top.dext.centurybridge.step.v1_20_5.facade.DustParticleOptionsBase f) {
            return dustText(f, f.field_28273, f.field_28274);
        } else if (o instanceof net.minecraft.class_2390 d) {
            return superWriteToString(d);
        } else if (o instanceof net.minecraft.class_5743 t) {
            return dustText(t, transition(t).cbS4$fromColor(), scaleField(t));
        }
        throw new UnsupportedOperationException(o.getClass().getName() + " is scalable particle options without a colour"
            + " (1.20.4's DustParticleOptionsBase always had one)");
    }

    /** ItemParticleOption: the type id, the item's id and its 1.20.4 NBT (1.20.4's ItemInput.serialize) */
    public static String superWriteToString(net.minecraft.class_2392 o) {
        net.minecraft.class_1799 stack = ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleItemAccessor) o).cbS4$itemStack();
        String item = stack.method_41409().method_40230().map(k -> ((net.minecraft.class_5321<?>) k).method_29177().toString())
            .orElse("unknown[" + stack.method_41409() + "]");
        net.minecraft.class_2487 tag = CbItemTags.getTag(stack);
        return typeId(o.method_10295()) + " " + item + (tag == null ? "" : tag.toString());
    }

    public static String superWriteToString(net.minecraft.class_7227 o) {
        return String.format(Locale.ROOT, "%s %.2f", typeId(o.method_10295()), o.comp_632());
    }

    public static String superWriteToString(net.minecraft.class_7290 o) {
        return String.format(Locale.ROOT, "%s %d", typeId(o.method_10295()), ((top.dext.centurybridge.step.v1_20_5.mixin.CbParticleShriekAccessor) o).cbS4$delay());
    }

    /** VibrationParticleOption: the destination's position (asked with no level, as 1.20.4 did) and the ticks */
    public static String superWriteToString(net.minecraft.class_5745 o) {
        top.dext.centurybridge.step.v1_20_5.mixin.CbParticleVibrationAccessor v = (top.dext.centurybridge.step.v1_20_5.mixin.CbParticleVibrationAccessor) o;
        net.minecraft.class_243 pos = (net.minecraft.class_243) v.cbS4$destination().method_32956(null).get();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", typeId(o.method_10295()), pos.field_1352, pos.field_1351,
            pos.field_1350, v.cbS4$arrivalInTicks());
    }

    public static String superWriteToString(net.minecraft.class_2400 o) {
        return typeId(o);
    }

    // ---------------------------------------------------------------- DustParticleOptionsBase

    /**
     * DustParticleOptionsBase.getColor() (the class is 1.20.5's ScalableParticleOptionsBase now): a converted
     * subclass's own (facade/DustParticleOptionsBase), dust's colour, a colour transition's starting colour --
     * each asked by its 1.20.5 name, which an override hook connects to a 1.20.4 subclass's getColor().
     */
    public static org.joml.Vector3f dustColor(net.minecraft.class_9679 o) {
        if (o instanceof top.dext.centurybridge.step.v1_20_5.facade.DustParticleOptionsBase f) {
            return f.method_33119();
        }
        if (o instanceof net.minecraft.class_2390 d) {
            return d.method_59843();
        }
        if (o instanceof net.minecraft.class_5743 t) {
            return t.method_33110();
        }
        throw new UnsupportedOperationException(o.getClass().getName() + " is scalable particle options without a colour"
            + " (1.20.4's DustParticleOptionsBase always had one)");
    }

    /** super.getColor() in a mod subclass of a 1.20.4 dust class: the base's colour field, past the class's own override */
    public static org.joml.Vector3f superDustColor(net.minecraft.class_9679 o) {
        if (o instanceof top.dext.centurybridge.step.v1_20_5.facade.DustParticleOptionsBase f) {
            return f.field_28273;
        }
        if (o instanceof net.minecraft.class_2390 d) {
            return dustColorField(d);
        }
        if (o instanceof net.minecraft.class_5743 t) {
            return transition(t).cbS4$fromColor();
        }
        throw new UnsupportedOperationException(o.getClass().getName() + " is scalable particle options without a colour"
            + " (1.20.4's DustParticleOptionsBase always had one)");
    }

    /** DustParticleOptionsBase.getScale(): 1.20.5's getScale, which an override hook connects to a 1.20.4 subclass's */
    public static float dustScale(net.minecraft.class_9679 o) {
        return o.method_59846();
    }

    /** super.getScale() in a mod subclass of a 1.20.4 dust class: the base's scale field */
    public static float superDustScale(net.minecraft.class_9679 o) {
        if (o instanceof top.dext.centurybridge.step.v1_20_5.facade.DustParticleOptionsBase f) {
            return f.field_28274;
        }
        return scaleField(o);
    }

    /** DustParticleOptionsBase.readVector3f(reader): 1.20.4's three space-led floats */
    public static org.joml.Vector3f readVector3f(StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        float x = reader.readFloat();
        reader.expect(' ');
        float y = reader.readFloat();
        reader.expect(' ');
        float z = reader.readFloat();
        return new org.joml.Vector3f(x, y, z);
    }

    /** DustParticleOptionsBase.readVector3f(buf) */
    public static org.joml.Vector3f readVector3f(net.minecraft.class_2540 buf) {
        return new org.joml.Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    /** DustParticleOptionsBase.MIN_SCALE */
    public static float minScale() {
        return 0.01F;
    }

    /** DustParticleOptionsBase.MAX_SCALE */
    public static float maxScale() {
        return 4.0F;
    }

    private CbParticles() {
    }
}
