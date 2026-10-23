package cb.datatest;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2350;
import net.minecraft.class_2359;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2940;
import net.minecraft.class_2941;
import net.minecraft.class_2943;
import net.minecraft.class_2945;
import net.minecraft.class_6880;
import net.minecraft.class_7375;
import net.minecraft.class_7923;

/**
 * The 1.20.4 FriendlyByteBuf and EntityDataSerializer surface: the Reader / Writer helpers
 * (collections, maps, optionals, nullables, either, asOptional), ids and holders by id, game
 * profiles, items and components on a plain buffer, a vanilla serializer's write / read through the
 * interface, the simple / optional / simpleEnum / simpleId factories, and a mod's own serializer
 * class carried through DataValue's wire format.
 */
final class BufTest {

    /** a 1.20.4 mod's own serializer class: a string, upper-cased on the wire */
    static final class ShoutSerializer implements class_2941<String> {
        @Override
        public void method_12715(class_2540 buf, String value) {
            buf.method_10814(value.toUpperCase());
        }

        @Override
        public String method_12716(class_2540 buf) {
            return buf.method_19772();
        }

        @Override
        public String method_12714(String value) {
            return value;
        }
    }

    static void run() {
        class_2540 b = NetTest.buf();
        List<Integer> ints = List.of(1, 2, 300);
        b.method_34062(ints, class_2540::method_10804);
        List<Integer> back = b.method_34068(ArrayList::new, class_2540::method_10816);
        DataTestMod.check("writeCollection / readCollection", back, ints);
        b.method_34062(ints, class_2540::method_10804);
        DataTestMod.check("readList", b.method_34066(class_2540::method_10816), ints);

        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        b.method_34063(map, class_2540::method_10814, class_2540::method_10804);
        DataTestMod.check("writeMap / readMap", b.method_34067(class_2540::method_19772, class_2540::method_10816), map);
        b.method_34063(map, class_2540::method_10814, class_2540::method_10804);
        Map<String, Integer> sized = b.method_34069(HashMap::new, class_2540::method_19772, class_2540::method_10816);
        DataTestMod.check("readMap (factory)", sized, map);

        b.method_37435(Optional.of("x"), class_2540::method_10814);
        b.method_37435(Optional.<String>empty(), class_2540::method_10814);
        DataTestMod.check("writeOptional / readOptional (present)", b.method_37436(class_2540::method_19772), Optional.of("x"));
        DataTestMod.check("writeOptional / readOptional (empty)", b.method_37436(class_2540::method_19772), Optional.empty());
        b.method_43826("n", class_2540::method_10814);
        b.method_43826(null, class_2540::method_10814);
        DataTestMod.check("writeNullable / readNullable (value)", b.method_43827(class_2540::method_19772), "n");
        DataTestMod.check("writeNullable / readNullable (null)", b.method_43827(class_2540::method_19772), null);

        b.method_43619(Either.<String, Integer>left("l"), class_2540::method_10814, class_2540::method_10804);
        b.method_43619(Either.<String, Integer>right(9), class_2540::method_10814, class_2540::method_10804);
        DataTestMod.check("writeEither / readEither (left)", b.method_43622(class_2540::method_19772, class_2540::method_10816), Either.left("l"));
        DataTestMod.check("writeEither / readEither (right)", b.method_43622(class_2540::method_19772, class_2540::method_10816), Either.right(9));

        class_2540.class_7462<String> writer = class_2540::method_10814;
        class_2540.class_7461<String> reader = class_2540::method_19772;
        writer.asOptional().accept(b, Optional.of("o"));
        DataTestMod.check("Writer.asOptional / Reader.asOptional", reader.asOptional().apply(b), Optional.of("o"));
        DataTestMod.check("Reader / Writer consumed", b.readableBytes(), 0);

        b.method_42065(class_7923.field_41178, class_1802.field_8477);
        DataTestMod.check("writeId / readById", b.method_42064(class_7923.field_41178), class_1802.field_8477);
        class_2359<class_6880<class_1792>> holders = class_7923.field_41178.method_40295();
        b.method_47989(holders, class_7923.field_41178.method_47983(class_1802.field_8477), (buf, item) -> buf.method_42065(class_7923.field_41178, item));
        b.method_47989(holders, class_6880.method_40223(class_1802.field_8279), (buf, item) -> buf.method_42065(class_7923.field_41178, item));
        DataTestMod.check("holder by id (registered)", b.method_47990(holders, buf -> buf.method_42064(class_7923.field_41178)).comp_349(), class_1802.field_8477);
        class_6880<class_1792> direct = b.method_47990(holders, buf -> buf.method_42064(class_7923.field_41178));
        DataTestMod.check("holder by id (direct)", direct.comp_349(), class_1802.field_8279);

        UUID id = UUID.fromString("00000000-0000-0000-0000-00000000cb04");
        GameProfile profile = new GameProfile(id, "cbtest");
        profile.getProperties().put("textures", new Property("textures", "skin", "sig"));
        b.method_43616(profile);
        GameProfile read = b.method_43623();
        DataTestMod.check("game profile id", read.getId(), id);
        DataTestMod.check("game profile name", read.getName(), "cbtest");
        Property prop = read.getProperties().get("textures").iterator().next();
        DataTestMod.check("game profile property", prop.value() + "/" + prop.signature(), "skin/sig");

        b.method_10793(new class_1799(class_1802.field_8477, 5));
        b.method_10793(class_1799.field_8037);
        class_1799 stack = b.method_10819();
        DataTestMod.check("writeItem / readItem", stack.method_7909() + " x" + stack.method_7947(), class_1802.field_8477 + " x5");
        DataTestMod.check("writeItem / readItem (empty)", b.method_10819().method_7960(), true);
        b.method_10805(class_2561.method_43470("hello"));
        b.method_10805(class_2561.method_43470("trusted"));
        DataTestMod.check("writeComponent / readComponent", b.method_10808().getString(), "hello");
        DataTestMod.check("readComponentTrusted", b.method_54148().getString(), "trusted");
        DataTestMod.check("items and components consumed", b.readableBytes(), 0);

        class_2941<Integer> ints1 = class_2943.field_13327;
        ints1.method_12715(b, 300);
        DataTestMod.check("INT serializer write: a VarInt", b.readableBytes(), 2);
        DataTestMod.check("INT serializer read", ints1.method_12716(b), 300);
        class_7375 black = class_7923.field_41163.method_31140(class_7375.field_38710);
        class_2943.field_38826.method_12715(b, black);
        DataTestMod.check("CAT_VARIANT serializer round trip", class_2943.field_38826.method_12716(b), black);

        class_2941<String> simple = class_2941.method_43242(class_2540::method_10814, class_2540::method_19772);
        simple.method_12715(b, "s");
        DataTestMod.check("EntityDataSerializer.simple", simple.method_12716(b), "s");
        class_2941<Optional<String>> optional = class_2941.method_43243(class_2540::method_10814, class_2540::method_19772);
        optional.method_12715(b, Optional.of("p"));
        DataTestMod.check("EntityDataSerializer.optional", optional.method_12716(b), Optional.of("p"));
        class_2941<class_2350> dirs = class_2941.method_43240(class_2350.class);
        dirs.method_12715(b, class_2350.field_11036);
        DataTestMod.check("EntityDataSerializer.simpleEnum", dirs.method_12716(b), class_2350.field_11036);
        class_2941<class_1792> items = class_2941.method_43237(class_7923.field_41178);
        items.method_12715(b, class_1802.field_8477);
        DataTestMod.check("EntityDataSerializer.simpleId", items.method_12716(b), class_1802.field_8477);
        DataTestMod.check("serializers consumed", b.readableBytes(), 0);

        ShoutSerializer shout = new ShoutSerializer();
        class_2943.method_12720(shout);
        DataTestMod.check("mod serializer registered", class_2943.method_12719(shout) >= 0, true);
        class_2941<String> asInterface = shout;
        asInterface.method_12715(b, "hey");
        DataTestMod.check("mod serializer through the interface", asInterface.method_12716(b), "HEY");
        class_2940<String> accessor = shout.method_12717(200);
        List<class_2945.class_7834<?>> one = new ArrayList<>();
        one.add(class_2945.class_7834.method_46360(accessor, "wire"));
        List<class_2945.class_7834<?>> wired = DataTestMod.roundTrip(one);
        DataTestMod.check("mod serializer in DataValue's wire format", wired.size() == 1 ? wired.get(0).comp_1117() : wired, "WIRE");
    }

    private BufTest() {
    }
}
