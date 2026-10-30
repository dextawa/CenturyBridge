package cb.s215;

import net.minecraft.class_1299;
import net.minecraft.class_1430;
import net.minecraft.class_1452;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3419;
import net.minecraft.class_5146;

/** 1.21.4 Saddleables: a Pig subclass overriding the saddle members, and a mod's own Saddleable (a Cow subclass) */
public final class Saddles {
    private Saddles() {
    }

    public static final class OwnPig extends class_1452 {
        public boolean saddled;
        public int equips;
        public class_3419 lastSource;

        public OwnPig(class_1299<? extends class_1452> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public boolean method_6765() {
            return true;
        }

        @Override
        public boolean method_6725() {
            return saddled;
        }

        @Override
        public void method_6576(class_1799 stack, class_3419 source) {
            saddled = true;
            equips++;
            lastSource = source;
        }
    }

    public static final class SaddleCow extends class_1430 implements class_5146 {
        public boolean saddled;
        public int equips;
        public class_3419 lastSource;

        public SaddleCow(class_1299<? extends class_1430> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public boolean method_6765() {
            return this.method_5805() && !this.method_6109();
        }

        @Override
        public void method_6576(class_1799 stack, class_3419 source) {
            saddled = stack.method_7947() == 1;
            equips++;
            lastSource = source;
        }

        @Override
        public boolean method_6725() {
            return saddled;
        }

        /** 1.21.4's protected Entity list helpers and bubble-aware water checks, called from a subclass */
        public String entityHelpers() {
            net.minecraft.class_2499 d = this.method_5846(1.5, -2.0);
            net.minecraft.class_2499 f = this.method_5726(0.25F);
            return d.size() + ":" + d.method_10611(1) + ":" + f.size() + ":" + f.method_10604(0) + ":" + this.method_5816() + ":" + this.method_5637();
        }
    }
}
