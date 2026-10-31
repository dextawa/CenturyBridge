package cb.s215.gt;

import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.class_3218;
import net.minecraft.class_4516;
import net.minecraft.class_4529;
import net.minecraft.class_6300;
import net.minecraft.class_6301;
import net.minecraft.class_6302;
import net.minecraft.class_6303;

/**
 * cb-s215's 1.21.4 game tests (fabric-gametest-api-v1 2.0.26 style: listed in the "fabric-gametest" entrypoint,
 * FabricGameTest). Batch cbs215a: pass, an optional (required=false) test that fails, a generated test; batch cbs215b:
 * one pass. Each batch has a @BeforeBatch and an @AfterBatch. Every step logs "CB-S215GT <event>" so the order can be
 * read from the server log; the outcomes come from the JUnit report.
 */
public class LegacyGameTests implements FabricGameTest {
    static void log(String e) {
        System.out.println("CB-S215GT " + e);
    }

    @class_6301(method_35931 = "cbs215a")
    public void beforeA(class_3218 level) {
        log("before cbs215a");
    }

    @class_6300(method_35930 = "cbs215a")
    public void afterA(class_3218 level) {
        log("after cbs215a");
    }

    @class_6301(method_35931 = "cbs215b")
    public void beforeB(class_3218 level) {
        log("before cbs215b");
    }

    @class_6300(method_35930 = "cbs215b")
    public void afterB(class_3218 level) {
        log("after cbs215b");
    }

    @class_6302(method_35936 = FabricGameTest.EMPTY_STRUCTURE, method_35933 = "cbs215a")
    public void passes(class_4516 helper) {
        log("test passes");
        helper.method_36036();
    }

    @class_6302(method_35936 = FabricGameTest.EMPTY_STRUCTURE, method_35933 = "cbs215a", method_35935 = false)
    public void optionalFails(class_4516 helper) {
        log("test optionalfails");
        helper.method_35995("CB-S215GT optional failure (expected)");
    }

    @class_6302(method_35936 = FabricGameTest.EMPTY_STRUCTURE, method_35933 = "cbs215b")
    public void passesB(class_4516 helper) {
        log("test passesb");
        helper.method_36036();
    }

    @class_6303
    public List<class_4529> generated() {
        return List.of(new class_4529("cbs215a", "legacygametests.generated_one", FabricGameTest.EMPTY_STRUCTURE, 100, 0L, true, helper -> {
            log("test generated_one");
            helper.method_36036();
        }));
    }
}
