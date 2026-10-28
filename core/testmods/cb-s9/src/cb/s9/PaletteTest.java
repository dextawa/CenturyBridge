package cb.s9;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2540;
import net.minecraft.class_2680;
import net.minecraft.class_2509;
import net.minecraft.class_2814;
import net.minecraft.class_2841;
import net.minecraft.class_7522;
import net.minecraft.class_2834;
import net.minecraft.class_2837;
import net.minecraft.class_6564;
import net.minecraft.CbS9TestResize;

/** 1.21.8 palette calls that rely on the palette's own registry and resize handler (s9d) */
final class PaletteTest {

    private PaletteTest() {
    }

    static void run() {
        S9TestMod.run("palettes: 1.21.8 constructors / creates keep the registry and resize handler", () -> {
            List<String> calls = new ArrayList<>();
            class_2680 stone = class_2246.field_10340.method_9564(), dirt = class_2246.field_10566.method_9564(),
                sand = class_2246.field_10102.method_9564();
            class_2814<class_2680> hash = new class_2814<>(class_2248.field_10651, 1, new CbS9TestResize<>(calls));
            S9TestMod.check(hash.method_12291(stone) == 0 && hash.method_12291(dirt) == 1 && hash.method_12291(stone) == 0,
                "HashMapPalette(registry, 1 bit, resize).idFor: stone 0, dirt 1, stone again 0");
            int grown = hash.method_12291(sand);
            S9TestMod.check(grown == 99 && calls.size() == 1 && calls.get(0).startsWith("2:"),
                "a third value at 1 bit goes to the palette's own resize handler (bits 2), got " + grown + " " + calls);
            class_2540 buf = new class_2540(Unpooled.buffer());
            hash.method_12287(buf);
            int written = buf.readableBytes();
            S9TestMod.check(written == hash.method_12290(), "write(buf) wrote getSerializedSize() bytes, got " + written + " / " + hash.method_12290());
            class_2814<class_2680> back = new class_2814<>(class_2248.field_10651, 1, new CbS9TestResize<>(calls));
            back.method_12289(buf);
            // 1.21.8's idFor adds the value before it reports the growth (javap -c HashMapPalette.idFor), so sand is in it
            S9TestMod.check(back.method_12197() == 3 && back.method_12288(0) == stone && back.method_12288(1) == dirt && back.method_12288(2) == sand,
                "read(buf) into a new palette gives stone, dirt, sand, got size " + back.method_12197());
            class_2837<class_2680> copy = hash.method_39956(new CbS9TestResize<>(calls));
            S9TestMod.check(copy.method_12290() == written, "copy(resize) keeps the registry: its serialized size is the original's");
            class_2837<class_2680> linear = class_2834.method_38295(4, class_2248.field_10651, new CbS9TestResize<>(calls), List.of(stone, dirt));
            S9TestMod.check(linear.method_12291(dirt) == 1 && linear.method_12290() > 0, "LinearPalette.create(bits, registry, resize, values): idFor / size");
            class_6564<class_2680> single = new class_6564<>(class_2248.field_10651, new CbS9TestResize<>(calls), List.of(stone));
            class_2540 one = new class_2540(Unpooled.buffer());
            single.method_12287(one);
            S9TestMod.check(one.readableBytes() == single.method_12290() && single.method_12291(stone) == 0,
                "SingleValuePalette(registry, resize, values): write / size / idFor");
        });

        S9TestMod.run("PalettedContainer with 1.21.8's registry-free Strategy", () -> {
            class_2680 air = class_2246.field_10124.method_9564(), stone = class_2246.field_10340.method_9564();
            class_2841.class_6563 states = class_2841.class_6563.field_34569;
            S9TestMod.check(states.method_38312() == 4096 && states.method_38313(1, 2, 3) == ((2 << 4 | 3) << 4 | 1)
                && class_2841.class_6563.field_34570.method_38312() == 64, "Strategy.size() / getIndex as 1.21.8's");
            class_2841<class_2680> c = new class_2841<>(class_2248.field_10651, air, states);
            c.method_35321(1, 2, 3, stone);
            int distinct = 0;
            for (class_2680 s2 : class_2248.field_10651) {
                if (distinct++ > 40) {
                    break;
                }
                c.method_35321(distinct % 16, 5, distinct / 16, s2);
            }
            S9TestMod.check(c.method_12321(1, 2, 3) == stone, "new PalettedContainer(registry, value, SECTION_STATES): set / get across palette growth");
            class_7522.class_6562<class_2680> packed = c.method_44345(class_2248.field_10651, states);
            S9TestMod.check(packed.comp_75().contains(stone) && packed.comp_76().isPresent(), "pack(registry, strategy) packs the palette and data");
            var codec = class_2841.method_44343(class_2248.field_10651, class_2680.field_24734, states, air);
            var tag = codec.encodeStart(class_2509.field_11560, c).getOrThrow();
            class_2841<class_2680> back = codec.parse(class_2509.field_11560, tag).getOrThrow();
            S9TestMod.check(back.method_12321(1, 2, 3) == stone, "codecRW(registry, codec, strategy, default) round-trips the container");
        });
    }
}
