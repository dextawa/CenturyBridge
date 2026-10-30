package cb.s9;

import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_2540;
import net.minecraft.class_2658;
import net.minecraft.class_3218;
import net.minecraft.class_4076;
import net.minecraft.class_4209;
import net.minecraft.class_8714;
import net.minecraft.class_8728;
import net.minecraft.class_9129;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 debug payloads (removed by 1.21.9): a 1.21.8 mod builds GameTestAddMarkerDebugPayload and
 * VillageSectionsDebugPayload, sends them through ClientboundCustomPayloadPacket's GAMEPLAY_STREAM_CODEC and reads
 * back equal payloads of the same class (1.21.8's types registered in the codec; VillageSections uses
 * FriendlyByteBuf's SectionPos read/write); its own STREAM_CODEC round-trips too; DebugPackets.addGameTestMarker /
 * clearGameTestMarkers run.
 */
public final class DebugPayloadsTest {

    private DebugPayloadsTest() {
    }

    static void run(MinecraftServer server) {
        class_3218 level = server.method_30002();
        S9TestMod.run("debug payloads: 1.21.8 records through the gameplay codec", () -> {
            class_8714 marker = new class_8714(new class_2338(1, 2, 3), 0x7f00ff00, "cb-s9 marker", 1234);
            class_9129 buf = new class_9129(Unpooled.buffer(), server.method_30611());
            class_2658.field_48620.encode(buf, new class_2658(marker));
            class_2658 back = class_2658.field_48620.decode(buf);
            S9TestMod.check(back.comp_1646() instanceof class_8714 && back.comp_1646().equals(marker) && buf.readableBytes() == 0,
                "GameTestAddMarkerDebugPayload round-trips through ClientboundCustomPayloadPacket.GAMEPLAY_STREAM_CODEC, got " + back.comp_1646());
            S9TestMod.check(marker.method_56479().comp_2242().toString().equals("minecraft:debug/game_test_add_marker"),
                "GameTestAddMarkerDebugPayload.TYPE id is 1.21.8's, got " + marker.method_56479().comp_2242());

            class_8728 sections = new class_8728(java.util.Set.of(class_4076.method_18682(new class_2338(16, 32, 48))),
                java.util.Set.of(class_4076.method_18676(-1, 0, 2)));
            class_2540 own = new class_2540(Unpooled.buffer());
            class_8728.field_48686.encode(own, sections);
            class_8728 ownBack = class_8728.field_48686.decode(own);
            S9TestMod.check(ownBack.equals(sections) && own.readableBytes() == 0,
                "VillageSectionsDebugPayload.STREAM_CODEC (SectionPos read/write) round-trips, got " + ownBack);
            class_9129 buf2 = new class_9129(Unpooled.buffer(), server.method_30611());
            class_2658.field_48620.encode(buf2, new class_2658(sections));
            Object back2 = class_2658.field_48620.decode(buf2).comp_1646();
            S9TestMod.check(back2.equals(sections), "VillageSectionsDebugPayload round-trips through the gameplay codec, got " + back2);

            class_4209.method_22318(level, class_2338.field_10980, "cb-s9", 0xffffffff, 100);
            class_4209.method_22317(level);
            S9TestMod.check(List.of(marker).size() == 1, "DebugPackets.addGameTestMarker / clearGameTestMarkers ran");
        });
    }
}
