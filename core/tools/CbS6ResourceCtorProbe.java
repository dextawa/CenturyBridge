package net.minecraft;

import com.mojang.serialization.Codec;

/** 1.21.5 call-site probe: same-package direct construction plus a mod-style subclass super constructor. */
public final class CbS6ResourceCtorProbe {
    private CbS6ResourceCtorProbe() {
    }

    public static <T> class_9433<T> direct(class_7157 lookup, class_5321 registry,
            Codec<class_6880<T>> codec) {
        return new class_9433<>(lookup, registry, codec);
    }

    public static final class Super<T> extends class_9433<T> {
        public Super(class_7157 lookup, class_5321 registry, Codec<class_6880<T>> codec) {
            super(lookup, registry, codec);
        }
    }
}
