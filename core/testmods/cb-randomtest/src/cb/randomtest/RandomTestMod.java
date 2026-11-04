package cb.randomtest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.class_5819;
import net.minecraft.class_6574;

/** A concrete child whose abstract jar superclass has no fromSeed implementation. */
public final class RandomTestMod implements ModInitializer {
    private abstract static class Base implements class_6574 {
        @Override
        public class_5819 method_38995(String seed) {
            return class_5819.method_43053();
        }

        @Override
        public class_5819 method_38418(int x, int y, int z) {
            return class_5819.method_43053();
        }

        @Override
        public void method_39039(StringBuilder out) {
        }
    }

    private static final class Impl extends Base {
    }

    @Override
    public void onInitialize() {
        try {
            Impl impl = new Impl();
            Object random = Impl.class.getMethod("method_60628", long.class).invoke(impl, 123L);
            if (random == null) {
                throw new AssertionError("null random source");
            }
            System.out.println("CB-RANDOMTEST OK abstract-base/concrete-child fromSeed");
        } catch (Throwable t) {
            System.out.println("CB-RANDOMTEST FAIL " + t);
            t.printStackTrace();
        }
    }
}
