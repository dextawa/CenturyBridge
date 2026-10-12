package cb.datatest;

import io.netty.buffer.Unpooled;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2539;
import net.minecraft.class_2540;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_2653;
import net.minecraft.class_2656;
import net.minecraft.class_2761;
import net.minecraft.class_5891;
import net.minecraft.class_8733;

/**
 * The 1.20.4 packet surface: write(buf) / new P(buf) round trips (a plain class, a record-ified
 * packet, one carrying an item stack), Packet.write through the interface, a subclass of a packet
 * 1.20.5 made a record (its getter override answers, super(buf) and super getters work), a unit
 * packet, nextProtocol(), and a mod's own Packet implementation.
 */
final class NetTest {

    /** a 1.20.4 mod's own packet class */
    static final class ModPacket implements class_2596<class_2602> {
        final int value;

        ModPacket(int value) {
            this.value = value;
        }

        ModPacket(class_2540 buf) {
            this.value = buf.method_10816();
        }

        @Override
        public void method_11052(class_2540 buf) {
            buf.method_10804(value);
        }

        @Override
        public void method_11054(class_2602 listener) {
        }
    }

    /** a 1.20.4 subclass of ClientboundCooldownPacket (a plain class in 1.20.4, a record in 1.20.5) */
    static final class LongCooldown extends class_2656 {
        LongCooldown(class_1792 item, int ticks) {
            super(item, ticks);
        }

        LongCooldown(class_2540 buf) {
            super(buf);
        }

        @Override
        public int method_11454() {
            return super.method_11454() * 10;
        }
    }

    static class_2540 buf() {
        return new class_2540(Unpooled.buffer());
    }

    static void run() {
        class_2761 time = new class_2761(123L, 456L, true);
        class_2540 b = buf();
        time.method_11052(b);
        class_2761 time2 = new class_2761(b);
        DataTestMod.check("SetTime round trip game time", time2.method_11871(), 123L);
        DataTestMod.check("SetTime round trip day time", time2.method_11873(), 456L);
        DataTestMod.check("SetTime round trip consumed", b.readableBytes(), 0);

        class_2596<?> asPacket = time;
        class_2540 b2 = buf();
        asPacket.method_11052(b2);
        DataTestMod.check("Packet.write through the interface", b2.readableBytes() > 0 && new class_2761(b2).method_11871() == 123L, true);

        class_2656 cooldown = new class_2656(class_1802.field_8477, 40);
        class_2540 b3 = buf();
        cooldown.method_11052(b3);
        class_2656 cooldown2 = new class_2656(b3);
        DataTestMod.check("Cooldown (record) round trip item", cooldown2.method_11453(), class_1802.field_8477);
        DataTestMod.check("Cooldown (record) round trip ticks", cooldown2.method_11454(), 40);

        LongCooldown sub = new LongCooldown(class_1802.field_8279, 7);
        DataTestMod.check("record subclass getter override (call)", sub.method_11454(), 70);
        class_2656 asBase = sub;
        DataTestMod.check("record subclass getter override (through the base)", asBase.method_11454(), 70);
        class_2540 b4 = buf();
        new class_2656(class_1802.field_8279, 3).method_11052(b4);
        LongCooldown sub2 = new LongCooldown(b4);
        DataTestMod.check("record subclass super(buf)", sub2.method_11453(), class_1802.field_8279);
        DataTestMod.check("record subclass super(buf) ticks via override", sub2.method_11454(), 30);

        class_2653 slot = new class_2653(0, 5, 3, new class_1799(class_1802.field_8477, 12));
        class_2540 b5 = buf();
        slot.method_11052(b5);
        class_2653 slot2 = new class_2653(b5);
        DataTestMod.check("ContainerSetSlot (registry codec) item", slot2.method_11449().method_7909(), class_1802.field_8477);
        DataTestMod.check("ContainerSetSlot (registry codec) count", slot2.method_11449().method_7947(), 12);
        DataTestMod.check("ContainerSetSlot (registry codec) slot", slot2.method_11450(), 3);

        class_5891 enter = new class_5891();
        class_2540 b6 = buf();
        enter.method_11052(b6);
        DataTestMod.check("unit packet writes nothing", b6.readableBytes(), 0);

        DataTestMod.check("FinishConfiguration.nextProtocol", new class_8733().method_52273(), class_2539.field_20591);
        DataTestMod.check("SetTime.nextProtocol (Packet default)", time.method_52273(), null);

        ModPacket mine = new ModPacket(77);
        class_2596<?> minePacket = mine;
        class_2540 b7 = buf();
        minePacket.method_11052(b7);
        DataTestMod.check("mod packet written through the interface", new ModPacket(b7).value, 77);
        DataTestMod.check("mod packet has a type", String.valueOf(cbType(minePacket)).contains("legacy/"), true);
    }

    /** Packet.type() is 1.20.5 API: called reflectively, as a 1.20.4 mod cannot name it */
    static Object cbType(class_2596<?> packet) {
        try {
            for (java.lang.reflect.Method m : packet.getClass().getMethods()) {
                if (m.getParameterCount() == 0 && m.getReturnType().getName().equals("net.minecraft.class_9145")) {
                    return m.invoke(packet);
                }
            }
            return "no type()";
        } catch (ReflectiveOperationException e) {
            return e.toString();
        }
    }

    private NetTest() {
    }
}
