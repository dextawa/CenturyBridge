package cb.s6realms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_11305;
import net.minecraft.class_11306;
import net.minecraft.class_4877;
import net.minecraft.class_4883;
import top.dext.centurybridge.step.v1_21_6.rt.CbS6RealmsSlots;

/** Native 1.21.6 behavior harness for the converted 1.21.5 fixture and both live map directions. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class S6RealmsSlotsHarness {
    private static int checks;

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }

    private static class_4883 ping(long marker) {
        class_4883 ping = class_4883.method_25076();
        ping.field_22636 = (int) marker;
        return ping;
    }

    private static class_11306 wrapper(int slot, class_4883 ping, ArrayList<class_11305> players) {
        return new class_11306(slot, ping, players);
    }

    private static void roundTripAndViews() {
        check(CbS6RealmsSlots.cbLegacySlots(null) == null, "null native -> old");
        check(CbS6RealmsSlots.cbNativeSlots(null) == null, "null old -> native");

        Map<Integer, class_11306> nativeBacking = new HashMap<>();
        ArrayList<class_11305> players = new ArrayList<>();
        players.add(new class_11305("id-one", "player-one"));
        class_4883 original = ping(10);
        class_11306 originalWrapper = wrapper(1, original, players);
        nativeBacking.put(1, originalWrapper);

        Map<Integer, class_4883> legacy = CbS6RealmsSlots.cbLegacySlots(nativeBacking);
        check(CbS6RealmsSlots.cbLegacySlots(legacy) == legacy, "same-direction old conversion is idempotent");
        check(CbS6RealmsSlots.cbNativeSlots(legacy) == nativeBacking, "native -> old -> native unwraps identity");
        check(legacy.get(1) == original, "old read exposes the wrapper's ping");

        class_4883 replacement = ping(20);
        check(legacy.put(1, replacement) == original, "old put answers the previous ping");
        check(nativeBacking.get(1) == originalWrapper, "old put preserves native wrapper identity");
        check(originalWrapper.field_60206 == replacement, "old put replaces the wrapper's ping");
        check(originalWrapper.field_60207 == players, "old put preserves the player list");

        Map.Entry<Integer, class_4883> oldEntry = legacy.entrySet().iterator().next();
        class_4883 entered = ping(30);
        check(oldEntry.setValue(entered) == replacement, "old entry.setValue answers the previous ping");
        check(nativeBacking.get(1) == originalWrapper && originalWrapper.field_60206 == entered,
            "old entry.setValue writes through in place");

        class_4883 added = ping(40);
        legacy.put(2, added);
        class_11306 addedWrapper = nativeBacking.get(2);
        check(addedWrapper != null && addedWrapper.field_60205 == 2, "old put creates the keyed native slot");
        check(addedWrapper.field_60206 == added && addedWrapper.field_60207.isEmpty(),
            "a new native wrapper keeps the old ping and has no invented players");
        check(legacy.remove(2) == added && !nativeBacking.containsKey(2), "old removal writes through");

        Map<Integer, class_4883> oldBacking = new HashMap<>();
        class_4883 oldPing = ping(50);
        oldBacking.put(1, oldPing);
        Map<Integer, class_11306> nativeView = CbS6RealmsSlots.cbNativeSlots(oldBacking);
        check(CbS6RealmsSlots.cbNativeSlots(nativeView) == nativeView, "same-direction native conversion is idempotent");

        class_11306 cached = nativeView.get(1);
        check(cached != null && cached.field_60206 == oldPing && cached.field_60205 == 1,
            "native read wraps the old ping in its slot");
        check(nativeView.get(1) == cached, "native reads preserve wrapper identity");

        ArrayList<class_11305> roundTripPlayers = new ArrayList<>();
        roundTripPlayers.add(new class_11305("round-trip-id", "round-trip-player"));
        cached.field_60207 = roundTripPlayers;
        Map<Integer, class_4883> oldRoundTrip = CbS6RealmsSlots.cbLegacySlots(nativeView);
        check(CbS6RealmsSlots.cbLegacySlots(nativeView) == oldRoundTrip,
            "a native view owns one inverse old view");
        check(CbS6RealmsSlots.cbNativeSlots(oldRoundTrip) == nativeView,
            "NativeSlots -> old -> native restores the original native view");
        check(nativeView.get(1) == cached && cached.field_60207 == roundTripPlayers,
            "the inverse round trip retains wrapper identity and its player list");

        class_4883 enteredThroughOldView = ping(55);
        Map.Entry<Integer, class_4883> oldRoundTripEntry = oldRoundTrip.entrySet().iterator().next();
        check(oldRoundTripEntry.setValue(enteredThroughOldView) == oldPing,
            "the paired old entry answers the previous ping");
        check(nativeView.get(1) == cached && cached.field_60206 == enteredThroughOldView
                && cached.field_60207 == roundTripPlayers && oldBacking.get(1) == enteredThroughOldView,
            "the paired old entry updates ping and backing map without losing wrapper or players");

        class_4883 directlyReplaced = ping(60);
        oldBacking.put(1, directlyReplaced);
        check(nativeView.get(1) == cached && cached.field_60206 == directlyReplaced,
            "the cached native wrapper follows direct old-map replacement");

        ArrayList<class_11305> suppliedPlayers = new ArrayList<>();
        suppliedPlayers.add(new class_11305("id-two", "player-two"));
        class_11306 supplied = wrapper(1, ping(70), suppliedPlayers);
        check(nativeView.put(1, supplied) == cached, "native put answers the previously viewed wrapper");
        check(nativeView.get(1) == supplied, "native put preserves the supplied wrapper identity");
        check(oldBacking.get(1) == supplied.field_60206, "native put writes its ping through");
        check(supplied.field_60207 == suppliedPlayers, "native put retains player data");

        Map.Entry<Integer, class_11306> nativeEntry = nativeView.entrySet().iterator().next();
        class_11306 enteredNative = wrapper(1, ping(80), new ArrayList<>());
        check(nativeEntry.setValue(enteredNative) == supplied, "native entry.setValue answers the old wrapper");
        check(nativeView.get(1) == enteredNative && oldBacking.get(1) == enteredNative.field_60206,
            "native entry.setValue preserves the wrapper and writes the old ping");

        nativeView.put(2, wrapper(2, ping(90), new ArrayList<>()));
        Iterator<Map.Entry<Integer, class_11306>> iterator = nativeView.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getKey() == 2) {
                iterator.remove();
            }
        }
        check(!oldBacking.containsKey(2), "native iterator removal writes through");
    }

    private static void nativeCopyHelper() {
        class_4877 server = new class_4877();
        Map<Integer, class_11306> input = new HashMap<>();
        ArrayList<class_11305> players = new ArrayList<>();
        players.add(new class_11305("copy-id", "copy-player"));
        class_11306 source = wrapper(1, ping(100), players);
        input.put(1, source);

        Map<Integer, class_11306> copy = server.method_25061(input);
        check(copy != input, "native control: the real copy helper returns a new map");
        check(copy.get(1) != source, "native control: the real copy helper copies the wrapper");
        check(copy.get(1).field_60206 != source.field_60206, "native control: the helper clones the ping");
        check(copy.get(1).field_60206.field_22636 == 100L, "native control: the cloned ping keeps its data");
        check(copy.get(1).field_60207 == players, "native control: the copy carries the player list");
        check(CbS6RealmsSlots.cbLegacySlots(copy).get(1) == copy.get(1).field_60206,
            "the native copy result has a CCE-free old view");
    }

    /** The original one-player failure: NativeSlots -> old -> native -> RealmsServer.method_25061. */
    private static void onePlayerNativeCopyCounterexample() {
        class_4877 server = new class_4877();
        Map<Integer, class_4883> oldBacking = new HashMap<>();
        oldBacking.put(1, ping(110));
        Map<Integer, class_11306> nativeView = CbS6RealmsSlots.cbNativeSlots(oldBacking);
        class_11306 source = nativeView.get(1);
        ArrayList<class_11305> players = new ArrayList<>();
        players.add(new class_11305("counterexample-id", "counterexample-player"));
        source.field_60207 = players;

        Map<Integer, class_4883> oldView = CbS6RealmsSlots.cbLegacySlots(nativeView);
        Map<Integer, class_11306> nativeArgument = CbS6RealmsSlots.cbNativeSlots(oldView);
        Map<Integer, class_11306> copy = server.method_25061(nativeArgument);
        check(copy.get(1).field_60207 == players && copy.get(1).field_60207.size() == 1,
            "counterexample: native copy receives the original one-player list");
        check(nativeArgument == nativeView, "counterexample: the inverse conversion restores NativeSlots");
        check(nativeArgument.get(1) == source, "counterexample: the original wrapper survives the round trip");
        check(copy.get(1) != source && copy.get(1).field_60206 != source.field_60206,
            "counterexample: native copy still clones the wrapper and ping");
    }

    private static void convertedOverrideAndFields() {
        S6RealmsSlotsOld oldServer = new S6RealmsSlotsOld();
        Map<Integer, class_11306> input = new HashMap<>();
        ArrayList<class_11305> players = new ArrayList<>();
        players.add(new class_11305("override-id", "override-player"));
        class_4883 originalPing = ping(200);
        class_11306 originalWrapper = wrapper(1, originalPing, players);
        input.put(1, originalWrapper);

        Map<Integer, class_11306> output = ((class_4877) oldServer).method_25061((Map) input);
        check(S6RealmsSlotsOld.seen == originalPing, "the converted override received old ping values");
        check(S6RealmsSlotsOld.seenMap.get(1) == input.get(1).field_60206,
            "the converted override's map remains live after its body returns");
        check(input.get(1) == originalWrapper, "the override's old put preserved the native wrapper");
        check(input.get(1).field_60206.field_22642 == 4242L, "the override's replacement reached native input");
        check(input.get(1).field_60207 == players, "the override's replacement preserved players");
        check(input.get(2).field_60206.field_22642 == 5252L, "the override's added old entry became native");
        check(output != input && output.get(1) != originalWrapper,
            "the override's converted super call ran the real native copy");
        check(output.get(1).field_60206.field_22642 == -1L
                && output.get(2).field_60206.field_22642 == -1L,
            "the converted super call kept native ping-copy field semantics");
        check(output.get(1).field_60206 != input.get(1).field_60206,
            "the converted super call retained native clone semantics");
        check(output.get(1).field_60207 == players, "the converted super call retained player data");

        class_4877 fieldServer = new class_4877();
        fieldServer.field_22607 = input;
        Map<Integer, class_4883> read = S6RealmsSlotsOld.readSlots(fieldServer);
        check(read.get(1) == input.get(1).field_60206, "converted GETFIELD returns old ping values");

        Map<Integer, class_4883> oldSlots = new HashMap<>();
        class_4883 fieldPing = ping(300);
        oldSlots.put(9, fieldPing);
        S6RealmsSlotsOld.writeSlots(fieldServer, oldSlots);
        Map<Integer, class_11306> nativeField = fieldServer.field_22607;
        class_11306 fieldWrapper = nativeField.get(9);
        check(fieldWrapper != null && fieldWrapper.field_60205 == 9 && fieldWrapper.field_60206 == fieldPing,
            "converted PUTFIELD installs a native live view");
        ArrayList<class_11305> fieldPlayers = new ArrayList<>();
        fieldPlayers.add(new class_11305("field-id", "field-player"));
        fieldWrapper.field_60207 = fieldPlayers;

        Map<Integer, class_4883> fieldRead = S6RealmsSlotsOld.readSlots(fieldServer);
        check(CbS6RealmsSlots.cbNativeSlots(fieldRead) == nativeField,
            "PUTFIELD -> GETFIELD -> native restores the installed NativeSlots");
        check(nativeField.get(9) == fieldWrapper && fieldWrapper.field_60207 == fieldPlayers,
            "PUTFIELD -> GETFIELD retains the wrapper and player list");

        Map<Integer, class_11306> fieldCopy = CbS6RealmsSlots.cbNativeSlots(
            S6RealmsSlotsOld.copySlots(fieldServer));
        check(fieldCopy != nativeField && fieldCopy.get(9) != fieldWrapper,
            "converted GETFIELD/call runs the real native copy");
        check(nativeField.get(9) == fieldWrapper && nativeField.get(9).field_60207 == fieldPlayers,
            "converted GETFIELD/call leaves the source wrapper and players intact");
        check(fieldCopy.get(9).field_60207 == fieldPlayers && fieldCopy.get(9).field_60207.size() == 1,
            "converted GETFIELD/call copies the original one-player list");
    }

    private static void convertedOverrideThroughNativeView() {
        S6RealmsSlotsOld oldServer = new S6RealmsSlotsOld();
        Map<Integer, class_4883> oldBacking = new HashMap<>();
        class_4883 originalPing = ping(210);
        oldBacking.put(1, originalPing);
        S6RealmsSlotsOld.writeSlots(oldServer, oldBacking);
        Map<Integer, class_11306> nativeField = oldServer.field_22607;
        class_11306 originalWrapper = nativeField.get(1);
        ArrayList<class_11305> players = new ArrayList<>();
        players.add(new class_11305("view-override-id", "view-override-player"));
        originalWrapper.field_60207 = players;

        Map<Integer, class_11306> output = ((class_4877) oldServer).method_25061((Map) nativeField);
        check(S6RealmsSlotsOld.seen == originalPing, "native-view override receives the old ping");
        check(CbS6RealmsSlots.cbNativeSlots(S6RealmsSlotsOld.seenMap) == nativeField,
            "the override argument inverts to the original NativeSlots");
        check(nativeField.get(1) == originalWrapper && originalWrapper.field_60207 == players,
            "the override's old put preserves the original wrapper and players");
        check(output.get(1) != originalWrapper && output.get(1).field_60207 == players
                && output.get(1).field_60207.size() == 1,
            "the override's converted super call copies the original one-player list");
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("counterexample")) {
            onePlayerNativeCopyCounterexample();
        } else {
            roundTripAndViews();
            nativeCopyHelper();
            onePlayerNativeCopyCounterexample();
            convertedOverrideAndFields();
            convertedOverrideThroughNativeView();
        }
        System.out.println("S6REALMS_OK checks=" + checks);
    }
}
