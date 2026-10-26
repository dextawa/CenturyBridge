package top.dext.centurybridge.step.v1_20_5.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.class_1799;
import net.minecraft.class_2359;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_6880;
import net.minecraft.class_8824;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.facade.FriendlyByteBufReader;
import top.dext.centurybridge.step.v1_20_5.facade.FriendlyByteBufWriter;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetBufs;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * FriendlyByteBuf: the 1.20.4 methods 1.20.5 took out or moved to stream codecs, back on the buffer
 * (area s4net), so a 1.20.4 call stays a call on the buffer and a 1.20.4 subclass's override still
 * overrides. The collection / map / optional / nullable helpers took 1.20.4's Reader / Writer
 * (FriendlyByteBufReader / FriendlyByteBufWriter now); 1.20.5 kept them for StreamDecoder /
 * StreamEncoder with the same wire form, which these hand on to. Items and components 1.20.5 writes
 * only through stream codecs on a RegistryFriendlyByteBuf: through the buffer's view
 * (CbNetBufs.cbRegistryBuf). The rest are 1.20.4's bodies over 1.20.5's primitives.
 */
@Mixin(class_2540.class)
public abstract class CbNetFriendlyByteBufBridge implements CbRoutes.LegacyBuf {

    private class_2540 cb$self() {
        return (class_2540) (Object) this;
    }

    // ---------------------------------------------------------------- Reader / Writer helpers

    /** was readCollection(IntFunction, Reader) */
    public <T, C extends Collection<T>> C method_34068(IntFunction<C> factory, FriendlyByteBufReader<T> reader) {
        return cb$self().method_34068(factory, reader::apply);
    }

    /** was writeCollection(Collection, Writer) */
    public <T> void method_34062(Collection<T> values, FriendlyByteBufWriter<T> writer) {
        cb$self().method_34062(values, writer::accept);
    }

    /** was readList(Reader) */
    public <T> List<T> method_34066(FriendlyByteBufReader<T> reader) {
        return cb$self().method_34066(reader::apply);
    }

    /** was readMap(IntFunction, Reader, Reader) */
    public <K, V, M extends Map<K, V>> M method_34069(IntFunction<M> factory, FriendlyByteBufReader<K> keys, FriendlyByteBufReader<V> values) {
        return cb$self().method_34069(factory, keys::apply, values::apply);
    }

    /** was readMap(Reader, Reader) */
    public <K, V> Map<K, V> method_34067(FriendlyByteBufReader<K> keys, FriendlyByteBufReader<V> values) {
        return cb$self().method_34067(keys::apply, values::apply);
    }

    /** was writeMap(Map, Writer, Writer) */
    public <K, V> void method_34063(Map<K, V> map, FriendlyByteBufWriter<K> keys, FriendlyByteBufWriter<V> values) {
        cb$self().method_34063(map, keys::accept, values::accept);
    }

    /** was writeOptional(Optional, Writer) */
    public <T> void method_37435(Optional<T> value, FriendlyByteBufWriter<T> writer) {
        cb$self().method_37435(value, writer::accept);
    }

    /** was readOptional(Reader) */
    public <T> Optional<T> method_37436(FriendlyByteBufReader<T> reader) {
        return cb$self().method_37436(reader::apply);
    }

    /** was writeNullable(Object, Writer) */
    public <T> void method_43826(T value, FriendlyByteBufWriter<T> writer) {
        cb$self().method_43826(value, writer::accept);
    }

    /** was readNullable(Reader) */
    public <T> T method_43827(FriendlyByteBufReader<T> reader) {
        return cb$self().method_43827(reader::apply);
    }

    /** was writeEither(Either, Writer, Writer): true and the left value, or false and the right one */
    public <L, R> void method_43619(Either<L, R> value, FriendlyByteBufWriter<L> left, FriendlyByteBufWriter<R> right) {
        class_2540 self = cb$self();
        value.ifLeft(l -> {
            self.method_52964(true);
            left.accept(self, l);
        }).ifRight(r -> {
            self.method_52964(false);
            right.accept(self, r);
        });
    }

    /** was readEither(Reader, Reader) */
    public <L, R> Either<L, R> method_43622(FriendlyByteBufReader<L> left, FriendlyByteBufReader<R> right) {
        class_2540 self = cb$self();
        return self.readBoolean() ? Either.left(left.apply(self)) : Either.right(right.apply(self));
    }

    // ---------------------------------------------------------------- ids and holders

    /** was readById(IdMap): the element of the VarInt id (null when the map has none) */
    public <T> T method_42064(class_2359<T> map) {
        return map.method_10200(cb$self().method_10816());
    }

    /** was writeId(IdMap, value) */
    public <T> void method_42065(class_2359<T> map, T value) {
        int id = map.method_10206(value);
        if (id == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + map);
        }
        cb$self().method_10804(id);
    }

    /**
     * was writeById(IdMap&lt;Holder&gt;, Holder, Writer): a registered holder as its id + 1, a direct one
     * as 0 and then its value.
     */
    public <T> void method_47989(class_2359<class_6880<T>> map, class_6880<T> holder, FriendlyByteBufWriter<T> direct) {
        class_2540 self = cb$self();
        class_6880.class_6882 kind = holder.method_40231();
        if (kind == class_6880.class_6882.field_36446) { // REFERENCE
            int id = map.method_10206(holder);
            if (id == -1) {
                throw new IllegalArgumentException("Can't find id for '" + holder.comp_349() + "' in map " + map);
            }
            self.method_10804(id + 1);
        } else if (kind == class_6880.class_6882.field_36447) { // DIRECT
            self.method_10804(0);
            direct.accept(self, holder.comp_349());
        }
    }

    /** was readById(IdMap&lt;Holder&gt;, Reader): the holder of id - 1, or a direct one for 0 */
    public <T> class_6880<T> method_47990(class_2359<class_6880<T>> map, FriendlyByteBufReader<T> direct) {
        class_2540 self = cb$self();
        int id = self.method_10816();
        if (id == 0) {
            return class_6880.method_40223(direct.apply(self));
        }
        class_6880<T> holder = map.method_10200(id - 1);
        if (holder == null) {
            throw new IllegalArgumentException("Can't find element with id " + id);
        }
        return holder;
    }

    // ---------------------------------------------------------------- game profiles

    /** was readGameProfile(): id, name (16 at most), properties */
    public GameProfile method_43623() {
        class_2540 self = cb$self();
        GameProfile profile = new GameProfile(self.method_10790(), self.method_10800(16));
        profile.getProperties().putAll(method_46255());
        return profile;
    }

    /** was writeGameProfile(GameProfile) */
    public void method_43616(GameProfile profile) {
        class_2540 self = cb$self();
        self.method_10797(profile.getId());
        self.method_10814(profile.getName());
        method_46249(profile.getProperties());
    }

    /** was readGameProfileProperties(): a counted list of properties, keyed by name */
    public PropertyMap method_46255() {
        PropertyMap map = new PropertyMap();
        cb$self().method_34065(buf -> {
            Property p = method_43624();
            map.put(p.name(), p);
        });
        return map;
    }

    /** was writeGameProfileProperties(PropertyMap) */
    public void method_46249(PropertyMap properties) {
        Collection<Property> values = properties.values();
        class_2540 self = cb$self();
        self.method_10804(values.size());
        for (Property p : values) {
            method_43617(p);
        }
    }

    /** was readProperty(): name, value, optional signature */
    public Property method_43624() {
        class_2540 self = cb$self();
        String name = self.method_19772();
        String value = self.method_19772();
        String signature = self.method_43827(class_2540::method_19772);
        return new Property(name, value, signature);
    }

    /** was writeProperty(Property) */
    public void method_43617(Property property) {
        class_2540 self = cb$self();
        self.method_10814(property.name());
        self.method_10814(property.value());
        self.method_43826(property.signature(), class_2540::method_10814);
    }

    // ---------------------------------------------------------------- items and components

    /** was writeItem(ItemStack): 1.20.5's item stack codec (empty allowed), through the buffer's registry view */
    @Override
    @CbVanillaReads("CbRouteBufItemMixin: ItemStack.OPTIONAL_STREAM_CODEC writes each stack of 1.20.5's packets through it, as 1.20.4's packets wrote theirs, when a converted mod mixes it")
    public class_2540 method_10793(class_1799 stack) {
        CbRoutes.enterIo();
        try {
            class_1799.field_49268.encode(CbNetBufs.cbRegistryBuf(cb$self()), stack);
        } finally {
            CbRoutes.exitIo();
        }
        return cb$self();
    }

    /** was readItem() */
    @Override
    @CbVanillaReads("CbRouteBufItemMixin: ItemStack.OPTIONAL_STREAM_CODEC reads each stack of 1.20.5's packets through it, as 1.20.4's packets read theirs, when a converted mod mixes it")
    public class_1799 method_10819() {
        CbRoutes.enterIo();
        try {
            return class_1799.field_49268.decode(CbNetBufs.cbRegistryBuf(cb$self()));
        } finally {
            CbRoutes.exitIo();
        }
    }

    /** was writeComponent(Component): its NBT form, as 1.20.4 wrote it (ComponentSerialization) */
    public class_2540 method_10805(class_2561 component) {
        class_8824.field_49666.encode(CbNetBufs.cbRegistryBuf(cb$self()), component);
        return cb$self();
    }

    /** was readComponent(): read with 1.20.4's size limit (2 MiB of NBT, ComponentSerialization.STREAM_CODEC) */
    public class_2561 method_10808() {
        return class_8824.field_48540.decode(CbNetBufs.cbRegistryBuf(cb$self()));
    }

    /** was readComponentTrusted(): read without a size limit (ComponentSerialization.TRUSTED_STREAM_CODEC) */
    public class_2561 method_54148() {
        return class_8824.field_49666.decode(CbNetBufs.cbRegistryBuf(cb$self()));
    }
}
