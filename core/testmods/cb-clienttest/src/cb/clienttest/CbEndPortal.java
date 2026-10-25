package cb.clienttest;

import net.minecraft.class_1921;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2640;
import net.minecraft.class_5614;
import net.minecraft.class_840;

/**
 * A 1.20.1 mod's end portal renderer subclass (swall plan item 16): custom slab heights (getOffsetUp
 * 0.75, getOffsetDown 0.375) and its own renderType() hook, over its own portal block entity whose
 * shouldRenderFace answers every face. The renderer must call the three overrides when it draws.
 */
public final class CbEndPortal {
    static final float UP = 0.75f, DOWN = 0.375f;

    private CbEndPortal() {
    }

    public static class Entity extends class_2640 {
        public Entity() {
            super(new class_2338(0, 300, 0), class_2246.field_10027.method_9564());
        }

        @Override
        public boolean method_11400(class_2350 face) {
            return true;
        }
    }

    public static class Renderer extends class_840<Entity> {
        int ups, downs, types;

        public Renderer(class_5614.class_5615 ctx) {
            super(ctx);
        }

        @Override
        protected float method_3594() {
            ups++;
            return UP;
        }

        @Override
        protected float method_35793() {
            downs++;
            return DOWN;
        }

        @Override
        protected class_1921 method_34589() {
            types++;
            return super.method_34589();
        }
    }
}
