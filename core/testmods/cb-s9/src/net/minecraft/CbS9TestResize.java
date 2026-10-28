package net.minecraft;

import java.util.List;

/**
 * cb-s9's PaletteResize: the interface is package-private in 1.21.8, so a 1.21.8 mod implementing it lives in its package.
 * Records each resize and answers 99.
 */
public final class CbS9TestResize<T> implements class_2835<T> {
    public final List<String> calls;

    public CbS9TestResize(List<String> calls) {
        this.calls = calls;
    }

    @Override
    public int onResize(int bits, T value) {
        this.calls.add(bits + ":" + value);
        return 99;
    }
}
