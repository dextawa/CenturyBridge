package cb.s9;

import net.minecraft.class_1923;
import net.minecraft.class_10592;
import net.minecraft.class_3194;
import net.minecraft.class_3218;
import net.minecraft.class_3228;
import net.minecraft.class_3230;
import net.minecraft.class_8563;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.8 TicketType(timeout, persist, use) (1.21.9: TicketType(timeout, flags)): a 1.21.8 mod's own ticket types load /
 * simulate chunks, persist and expire as on 1.21.8, on a fresh TicketStorage, and read back through persist() / use().
 */
final class TicketTypeTest {
    private TicketTypeTest() {
    }

    static void run(MinecraftServer server) {
        S9TestMod.run("tickets: 1.21.8 TicketType constructor", () -> {
            class_3230 las = new class_3230(0L, false, class_3230.class_10558.field_55603);
            class_3230 loading = new class_3230(1L, true, class_3230.class_10558.field_55601);
            class_3230 sim = new class_3230(0L, false, class_3230.class_10558.field_55602);
            S9TestMod.check(las.comp_3476() == class_3230.class_10558.field_55603 && !las.comp_3475()
                    && las.method_66025() && las.method_66027(),
                "TicketType(0, false, LOADING_AND_SIMULATION) loads and simulates, use() " + las.comp_3476());
            S9TestMod.check(loading.comp_3476() == class_3230.class_10558.field_55601 && loading.comp_3475()
                    && loading.method_66025() && !loading.method_66027(),
                "TicketType(1, true, LOADING) loads only and persists, use() " + loading.comp_3476());
            S9TestMod.check(sim.comp_3476() == class_3230.class_10558.field_55602 && !sim.method_66025() && sim.method_66027(),
                "TicketType(0, false, SIMULATION) simulates only, use() " + sim.comp_3476());
            S9TestMod.check(class_3230.field_14030 != null && class_3230.field_14030.method_66025() && class_3230.field_14030.method_66027()
                    && !class_3230.field_14030.comp_3475(), "TicketType.START loads and simulates, not persisted");
            S9TestMod.check(new class_3230(0L, true, class_3230.class_10558.field_55603).equals(class_3230.field_14031),
                "a 1.21.8 type equal to FORCED (0, true, LOADING_AND_SIMULATION) still equals FORCED");

            class_10592 storage = new class_10592();
            int full = class_8563.method_51828(class_3194.field_44855);
            class_1923 a = new class_1923(1000, 1000);
            class_1923 b = new class_1923(1002, 1000);
            storage.method_66358(las, a, 0);
            storage.method_66358(loading, b, 0);
            S9TestMod.check(storage.method_66354(a.method_8324(), false) == full && storage.method_66354(a.method_8324(), true) == full,
                "a LOADING_AND_SIMULATION ticket gives loading and simulation level " + full + ", got "
                    + storage.method_66354(a.method_8324(), false) + " / " + storage.method_66354(a.method_8324(), true));
            S9TestMod.check(storage.method_66354(b.method_8324(), false) == full && storage.method_66354(b.method_8324(), true) > full,
                "a LOADING ticket gives the loading level only, got " + storage.method_66354(b.method_8324(), false)
                    + " / " + storage.method_66354(b.method_8324(), true));

            // a timed LOADING ticket counts down even where its chunk is loaded (1.21.8 froze only simulation tickets)
            class_3218 level = server.method_30002();
            level.method_8497(0, 0);
            class_1923 here = new class_1923(0, 0);
            var map = level.method_14178().field_17254;
            boolean held = map.method_17255(here.method_8324()) != null;
            storage.method_66358(loading, here, 0);
            for (int i = 0; i < 3; i++) {
                storage.method_66381(map);
            }
            boolean left = false;
            for (class_3228 t : storage.method_66352(here.method_8324())) {
                left |= t.method_14281() == loading;
            }
            S9TestMod.check(!left, "a timed (1) LOADING ticket expired with its chunk loaded (holder present " + held + ")");
        });
    }
}
