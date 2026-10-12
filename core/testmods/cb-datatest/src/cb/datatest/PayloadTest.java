package cb.datatest;

import io.netty.buffer.Unpooled;
import net.minecraft.class_2540;
import net.minecraft.class_2658;
import net.minecraft.class_2960;
import net.minecraft.class_8709;
import net.minecraft.class_8710;

/**
 * 1.20.4 custom payloads: a mod's own payload class (write + id) sent in a vanilla custom payload
 * packet keeps 1.20.4's wire form (its id, then what its write wrote); vanilla's brand payload keeps
 * write / id / ID; write and id through the CustomPacketPayload interface.
 */
final class PayloadTest {

    static final class Hello implements class_8710 {
        static final class_2960 ID = new class_2960("cb_datatest", "hello");
        final String text;

        Hello(String text) {
            this.text = text;
        }

        @Override
        public void method_53028(class_2540 buf) {
            buf.method_10814(text);
        }

        @Override
        public class_2960 comp_1678() {
            return ID;
        }
    }

    static class_2540 buf() {
        return new class_2540(Unpooled.buffer());
    }

    static void run() {
        Hello hello = new Hello("hi there");
        class_2658 packet = new class_2658(hello);
        class_2540 b = buf();
        packet.method_11052(b);
        DataTestMod.check("mod payload packet: id first", b.method_10810(), Hello.ID);
        DataTestMod.check("mod payload packet: then its write", b.method_19772(), "hi there");
        DataTestMod.check("mod payload packet: nothing else", b.readableBytes(), 0);

        class_8710 asPayload = hello;
        class_2540 b2 = buf();
        asPayload.method_53028(b2);
        DataTestMod.check("mod payload write through the interface", b2.method_19772(), "hi there");
        DataTestMod.check("mod payload id through the interface", asPayload.comp_1678(), Hello.ID);

        class_8709 brand = new class_8709("cbtest");
        DataTestMod.check("brand payload id()", brand.comp_1678(), class_8709.field_45697);
        DataTestMod.check("brand payload ID", class_8709.field_45697, new class_2960("minecraft", "brand"));
        class_2540 b3 = buf();
        brand.method_53028(b3);
        DataTestMod.check("brand payload write", b3.method_19772(), "cbtest");
        class_8710 brandPayload = brand;
        class_2540 b4 = buf();
        brandPayload.method_53028(b4);
        DataTestMod.check("brand payload write through the interface", b4.method_19772(), "cbtest");

        class_2540 b5 = buf();
        new class_2658(brand).method_11052(b5);
        class_2658 back = new class_2658(b5);
        DataTestMod.check("brand payload packet round trip", back.comp_1646() instanceof class_8709 got ? got.comp_1677() : back.comp_1646(), "cbtest");
    }

    private PayloadTest() {
    }
}
