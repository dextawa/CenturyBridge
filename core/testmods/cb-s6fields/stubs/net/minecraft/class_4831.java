package net.minecraft;

/** Identity-bearing ExpirableValue test double with the same raw value accessor as V and W. */
public class class_4831<T> {
    private final T value;
    private final long expiry;

    public class_4831(T value, long expiry) {
        this.value = value;
        this.expiry = expiry;
    }

    public T method_24637() {
        return value;
    }

    public long method_35127() {
        return expiry;
    }
}
