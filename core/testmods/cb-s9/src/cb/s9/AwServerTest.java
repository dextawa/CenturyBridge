package cb.s9;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.class_1299;
import net.minecraft.class_1452;
import net.minecraft.class_1542;
import net.minecraft.class_1659;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1826;
import net.minecraft.class_2596;
import net.minecraft.class_2602;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3730;
import net.minecraft.class_3898;
import net.minecraft.class_5629;
import net.minecraft.class_9145;
import net.minecraft.server.MinecraftServer;

/**
 * Server members a 1.21.8 mod reaches through its own access widener (cb_s9.accesswidener) that 1.21.9 dropped or
 * reshaped (damage-aw rows): ItemEntity's private copy constructor, SpawnEggItem.defaultType,
 * ServerGamePacketListenerImpl.tryHandleChat(message, action), ChunkMap.TrackedEntity.broadcastIgnorePlayers.
 */
final class AwServerTest {
    private AwServerTest() {
    }

    /** a packet the recording connections only count (never encoded) */
    static final class MarkPacket implements class_2596<class_2602> {
        @Override
        public class_9145<? extends class_2596<class_2602>> method_65080() {
            return null;
        }

        @Override
        public void method_65081(class_2602 listener) {
        }
    }

    /** a connection of a player that records what it is sent */
    static final class Recorder implements class_5629 {
        final class_3222 player;
        final List<Object> sent = new ArrayList<>();

        Recorder(class_3222 player) {
            this.player = player;
        }

        @Override
        public class_3222 method_32311() {
            return player;
        }

        @Override
        public void method_14364(class_2596<?> packet) {
            sent.add(packet);
        }
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("aw: ItemEntity copy constructor", () -> {
            class_3218 level = server.method_30002();
            class_1542 other = new class_1542(level, 3.5, 71.0, -8.25, new class_1799(class_1802.field_8477, 3));
            other.field_7204 = 42;
            class_1542 copy = new class_1542(other);
            S9TestMod.check(copy != other && copy.method_6983() != other.method_6983()
                    && copy.method_6983().method_7909() == class_1802.field_8477 && copy.method_6983().method_7947() == 3,
                "new ItemEntity(other) holds a copy of the other's stack (3 diamonds), got " + copy.method_6983());
            S9TestMod.check(copy.method_23317() == 3.5 && copy.method_23318() == 71.0 && copy.method_23321() == -8.25,
                "the copy stands where the other stands, got " + copy.method_23317() + " " + copy.method_23318() + " " + copy.method_23321());
            S9TestMod.check(copy.field_7204 == 42 && copy.field_7203 == other.field_7203,
                "the copy keeps the other's age (42) and bob offset, got " + copy.field_7204 + " " + copy.field_7203 + "/" + other.field_7203);
        });

        S9TestMod.run("aw: SpawnEggItem.defaultType", () -> {
            class_1826 egg = class_1826.method_8019(class_1299.field_6093);
            S9TestMod.check(egg != null && egg.field_8917 == class_1299.field_6093,
                "the pig spawn egg's defaultType is EntityType.PIG, got " + (egg == null ? "no egg" : egg.field_8917));
        });

        S9TestMod.run("aw: tryHandleChat(message, action)", () -> {
            class_3218 level = server.method_30002();
            FakePlayer fp = FakePlayer.get(level, new GameProfile(UUID.fromString("0b5ed7c1-0000-4000-8000-00000000cb5a"), "cbs9chat"));
            long before = fp.method_14219();
            try {
                Thread.sleep(5L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            boolean[] ran = {false};
            fp.field_13987.method_44337("hello", () -> ran[0] = true);
            long deadline = System.nanoTime() + 5_000_000_000L;
            server.method_18857(() -> ran[0] || System.nanoTime() > deadline);
            S9TestMod.check(ran[0], "tryHandleChat(\"hello\", action) runs the action on the server (chat visible)");
            S9TestMod.check(fp.method_14219() > before, "and resets the player's last action time, " + before + " -> " + fp.method_14219());
            // 1.21.8 refused a player with chat hidden (1.21.9 skips the refusal only for its new isCommand flag)
            fp.field_13993 = class_1659.field_7536;
            boolean[] hidden = {false};
            fp.field_13987.method_44337("hidden", () -> hidden[0] = true);
            long until = System.nanoTime() + 500_000_000L;
            server.method_18857(() -> hidden[0] || System.nanoTime() > until);
            S9TestMod.check(!hidden[0], "with chat visibility HIDDEN, tryHandleChat(message, action) refuses as 1.21.8 did (action not run)");
            fp.field_13993 = class_1659.field_7538;
        });

        S9TestMod.run("aw: TrackedEntity.broadcastIgnorePlayers(packet, uuids)", () -> {
            class_3218 level = server.method_30002();
            class_3898 chunkMap = level.method_14178().field_17254;
            class_1452 pig = (class_1452) class_1299.field_6093.method_5883(level, class_3730.field_16462);
            class_3898.class_3208 tracked = chunkMap.new class_3208(pig, 1, 1, true);
            FakePlayer a = FakePlayer.get(level, new GameProfile(UUID.fromString("0b5ed7c1-0000-4000-8000-00000000cb5b"), "cbs9a"));
            FakePlayer b = FakePlayer.get(level, new GameProfile(UUID.fromString("0b5ed7c1-0000-4000-8000-00000000cb5c"), "cbs9b"));
            Recorder ra = new Recorder(a), rb = new Recorder(b);
            Set<class_5629> seen = tracked.field_18250;
            seen.add(ra);
            seen.add(rb);
            MarkPacket p = new MarkPacket();
            tracked.method_66006(p, List.of(b.method_5667()));
            S9TestMod.check(ra.sent.size() == 1 && ra.sent.get(0) == p, "a player not in the list gets the packet, got " + ra.sent);
            S9TestMod.check(rb.sent.isEmpty(), "the player in the list does not, got " + rb.sent);
        });
    }
}
