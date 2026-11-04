package net.minecraft;

import java.util.Optional;

/** In-memory Brain test double recording the typed values the runtime redirects delegate. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class class_4095 {
    public Optional answer;
    public Object value;
    public long expiry;
    public final java.util.Map memories = new java.util.HashMap();

    public java.util.Map method_35058() {
        return memories;
    }

    public Optional method_18904(class_4140 type) {
        return answer;
    }

    public Optional method_46873(class_4140 type) {
        return answer;
    }

    public void method_18878(class_4140 type, Object value) {
        this.value = value;
        this.answer = Optional.ofNullable(value);
    }

    public void method_24525(class_4140 type, Object value, long expiry) {
        this.value = value;
        this.expiry = expiry;
        this.answer = Optional.ofNullable(value);
    }

    public void method_18879(class_4140 type, Optional value) {
        this.answer = value;
        this.value = value == null ? null : value.orElse(null);
    }

    public boolean method_29519(class_4140 type, Object value) {
        return answer != null && answer.isPresent() && answer.get() == value;
    }
}
