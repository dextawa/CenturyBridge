package cb.s215;

import java.util.UUID;
import net.minecraft.class_1299;
import net.minecraft.class_1493;
import net.minecraft.class_1498;
import net.minecraft.class_1937;

/** 1.21.4 owner overrides: a Wolf subclass and a Horse subclass overriding getOwnerUUID / setOwnerUUID */
public final class Owners {
    private Owners() {
    }

    public static final class OwnWolf extends class_1493 {
        public UUID set;
        public int gets;

        public OwnWolf(class_1299<? extends class_1493> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public UUID method_6139() {
            gets++;
            return super.method_6139();
        }

        @Override
        public void method_6174(UUID uuid) {
            set = uuid;
            super.method_6174(uuid);
        }
    }

    public static final class OwnHorse extends class_1498 {
        public UUID set;
        public int gets;

        public OwnHorse(class_1299<? extends class_1498> type, class_1937 level) {
            super(type, level);
        }

        @Override
        public UUID method_6139() {
            gets++;
            return super.method_6139();
        }

        @Override
        public void method_6732(UUID uuid) {
            set = uuid;
            super.method_6732(uuid);
        }

        public int soundAsks;

        /** 1.21.4's Saddleable.getSaddleSoundEvent, overridden */
        @Override
        public net.minecraft.class_3414 method_45328() {
            soundAsks++;
            return net.minecraft.class_3417.field_14824;
        }

        /** 1.21.4's protected syncSaddleToClients, called from the subclass */
        public void sync() {
            this.method_56679();
        }
    }
}
