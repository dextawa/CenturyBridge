package cb.s9r;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_310;
import net.minecraft.class_434;
import net.minecraft.class_437;

/** 1.21.8 ReceivingLevelScreen: opened by the 1.21.8 mod, closed by its levelReceived supplier (read by the native driver) */
public final class LoadScreenCheck {
    public static final AtomicBoolean RECEIVED = new AtomicBoolean();

    private LoadScreenCheck() {
    }

    /** on the render thread: opens a ReceivingLevelScreen for the reason (NETHER_PORTAL / END_PORTAL / OTHER) */
    public static class_437 open(String reason) {
        RECEIVED.set(false);
        class_434.class_9678 r = switch (reason) {
            case "NETHER_PORTAL" -> class_434.class_9678.field_51487;
            case "END_PORTAL" -> class_434.class_9678.field_51488;
            default -> class_434.class_9678.field_51489;
        };
        class_434 screen = new class_434(RECEIVED::get, r);
        class_310.method_1551().method_1507(screen);
        return screen;
    }

    public static boolean closesOnEscape(class_437 screen) {
        return screen.method_25422();
    }
}
