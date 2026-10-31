package cb.s215n.gt;

import net.fabricmc.api.ModInitializer;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_4516;
import net.minecraft.class_7923;

/**
 * cb-s215n's 1.21.5 game test, run next to the converted 1.21.4 tests: a test function in 1.21.5's TEST_FUNCTION
 * registry (registered while it is open) and its test instance as data (data/cb_s215n/test_instance/native_passes.json,
 * 1.21.5's own format). Not fabric-gametest-api-v1 3.1.3's @GameTest: its rotation() default names the enum constant by
 * its intermediary field name (field_11467), which Enum.valueOf cannot find on a production server (name() is NONE), so
 * the annotation fails to parse there (AnnotationFormatError, its own TestAnnotationLocator included).
 */
public class NativeGameTests implements ModInitializer {
    @Override
    public void onInitialize() {
        if (System.getProperty("fabric-api.gametest") == null) {
            return;
        }
        class_2378.method_10230(class_7923.field_56156, class_2960.method_60655("cb_s215n", "native_passes"), NativeGameTests::nativePasses);
    }

    static void nativePasses(class_4516 context) {
        System.out.println("CB-S215GT test native_passes");
        context.method_36036();
    }
}
