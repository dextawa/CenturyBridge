package cb.swall120;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ModInitializer;

/**
 * A 1.20.1-era mod (intermediary names) using 1.20.1's JSON API of the advancement predicates, which 1.20.2 replaced by
 * codecs and the 1.20.2 layer restores (CbAdv*Json, read through CbAdv*Access invokers). Climbed 1.20.1 -> 26.1 with
 * every layer, it checks that a 1.20.1-format entity predicate (with flags), item predicate and location predicate
 * still read and write, and that 1.20.1's EntitySubPredicate.ANY and Types table still initialise. Prints "CBTEST120 PASS|FAIL name" and "CBTEST120 SUMMARY n/m".
 * Known failures on 26.1 (queue_swall.md, expected-fail): adv120.mixedReader (CASE E: the invokers are stripped at the wall),
 * adv120.flags (CbAdvJson's static EntitySubPredicate.ANY does not parse), adv120.anyNull (null is not ANY).
 */
public class Swall120 implements ModInitializer {
    private static final List<String> FAILS = new ArrayList<>();
    /** reads of the restored 1.20.1 EntityPredicate.fromJson (mixin/EntityPredicateJsonMixin) */
    public static final java.util.concurrent.atomic.AtomicInteger ENTITY_READS = new java.util.concurrent.atomic.AtomicInteger();
    private static int passed;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST120 PASS " + name);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST120 FAIL " + name + " : " + describe(detail));
        }
    }

    /** a failure with its cause chain and the first frames of the deepest cause */
    private static String describe(Object detail) {
        if (!(detail instanceof Throwable t)) {
            return String.valueOf(detail);
        }
        StringBuilder b = new StringBuilder(String.valueOf(t));
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
            b.append(" <- ").append(root);
        }
        StackTraceElement[] st = root.getStackTrace();
        for (int i = 0; i < Math.min(4, st.length); i++) {
            b.append(" @ ").append(st[i]);
        }
        return b.toString();
    }

    private static JsonElement json(String s) {
        return JsonParser.parseString(s);
    }

    @Override
    public void onInitialize() {
        try {
            net.minecraft.class_2048 entity = net.minecraft.class_2048.method_8913(json("{\"flags\":{\"is_on_fire\":true,\"is_baby\":false}}"));
            String back = String.valueOf(entity.method_8912());
            check("adv120.entityFlags", entity != net.minecraft.class_2048.field_9599 && back.contains("is_on_fire") && back.contains("is_baby"), back);
            // the read went through the restored 1.20.1 reader this mod mixes (its flags read by the CbAdv*Access invokers)
            check("adv120.mixedReader", ENTITY_READS.get() > 0, "reads " + ENTITY_READS.get());
        } catch (Throwable t) {
            check("adv120.entityFlags", false, t);
        }
        try {
            net.minecraft.class_2040 flags = net.minecraft.class_2040.method_8893(json("{\"is_sneaking\":true}"));
            String back = String.valueOf(flags.method_8894());
            check("adv120.flags", flags != net.minecraft.class_2040.field_9581 && back.contains("is_sneaking"), back);
        } catch (Throwable t) {
            check("adv120.flags", false, t);
        }
        try {
            net.minecraft.class_2073 item = net.minecraft.class_2073.method_8969(json("{\"count\":{\"min\":2}}"));
            String back = String.valueOf(item.method_8971());
            check("adv120.item", item != net.minecraft.class_2073.field_9640 && back.contains("count"), back);
        } catch (Throwable t) {
            check("adv120.item", false, t);
        }
        try {
            net.minecraft.class_2090 location = net.minecraft.class_2090.method_9021(json("{\"position\":{\"y\":{\"min\":10}}}"));
            String back = String.valueOf(location.method_9019());
            check("adv120.location", location != net.minecraft.class_2090.field_9685 && back.contains("position"), back);
        } catch (Throwable t) {
            check("adv120.location", false, t);
        }
        try {
            check("adv120.anyNull", net.minecraft.class_2048.method_8913(null) == net.minecraft.class_2048.field_9599, "");
        } catch (Throwable t) {
            check("adv120.anyNull", false, t);
        }
        try {
            // 1.20.1's Types table names the variant kinds by the variant predicates' own types (TYPES.put("cat", CAT.type()))
            Object byName = net.minecraft.class_7376.class_7378.field_38731.get("cat");
            Object own = net.minecraft.class_7376.class_7378.field_38729.method_43114();
            Object name = net.minecraft.class_7376.class_7378.field_38731.inverse().get(own);
            check("adv120.typesIdentity", byName != null && byName == own && "cat".equals(name),
                "TYPES.get(cat) " + byName + ", CAT.type() " + own + ", name of CAT.type() " + name);
        } catch (Throwable t) {
            check("adv120.typesIdentity", false, t);
        }
        // 1.20.1's EntitySubPredicate.ANY and its Types table (class_7376$class_7378: the "type" -> deserializer BiMap and
        // the variant types CAT / AXOLOTL): the 1.20.2 layer builds its ANY from Types, the 1.20.5 layer's Types read 1.21.4's
        // EntitySubPredicates.CAT, which 1.21.5 removed (queue_smx4: field_38729)
        try {
            Object any = net.minecraft.class_7376.field_38723;
            Object cat = net.minecraft.class_7376.class_7378.field_38729;
            Object axolotl = net.minecraft.class_7376.class_7378.field_41534;
            com.google.common.collect.BiMap<String, net.minecraft.class_7376.class_7377> types = net.minecraft.class_7376.class_7378.field_38731;
            boolean anyNull = net.minecraft.class_7376.method_43097(null) == any;
            check("adv120.subPredicateTypes", any != null && cat != null && axolotl != null && types.containsKey("cat")
                && types.containsKey("axolotl") && types.containsKey("lightning") && anyNull,
                "any=" + any + " fromJson(null)==ANY " + anyNull + " cat=" + cat + " types=" + types.keySet());
        } catch (Throwable t) {
            check("adv120.subPredicateTypes", false, t);
        }
        // 1.20.1 PlayerPredicate.fromJson: the 1.20.2 layer builds the record (CbAdvRecords.playerPredicate), whose
        // constructor 26.1 widened with a food condition (wall ctorRedirect, FoodPredicate.ANY)
        try {
            net.minecraft.class_4553 player = net.minecraft.class_4553.method_22499(
                json("{\"level\":{\"min\":3},\"gamemode\":\"survival\"}").getAsJsonObject());
            String back = String.valueOf(player.method_22494());
            check("adv120.playerPredicate", player != null && back.contains("level") && back.contains("survival"), back);
        } catch (Throwable t) {
            check("adv120.playerPredicate", false, t);
        }
        // 1.20.1 DimensionDataStorage.readTagFromDisk(id, version) (the 1.20.2 layer's CbSavedDataReadCover): reads
        // <dataFolder>/<id>.dat, the file 1.20.1 named (its private getDataFile; wall instanceRedirect)
        try {
            java.io.File dir = java.nio.file.Files.createTempDirectory("cbswall120-data").toFile();
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10569("cbValue", 42);
            net.minecraft.class_2507.method_30614(tag, new java.io.File(dir, "cbswall120.dat"));
            net.minecraft.class_26 storage = new net.minecraft.class_26(dir, net.minecraft.class_3551.method_15450());
            net.minecraft.class_2487 read = storage.method_17923("cbswall120", 3465);
            check("savedData120.readTagFromDisk", read != null && read.method_10550("cbValue") == 42, read);
        } catch (Throwable t) {
            check("savedData120.readTagFromDisk", false, t);
        }
        System.out.println("CBTEST120 SUMMARY " + passed + "/" + (passed + FAILS.size()) + " passed" + (FAILS.isEmpty() ? "" : " fails=" + FAILS));
    }
}
