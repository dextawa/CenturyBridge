package top.dext.centurybridge.step.v1_20_5.facade;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_2378;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7225;

/**
 * HolderLookup.RegistryLookup.Delegate: 1.20.4's abstract class (1.20.5 made it an interface whose
 * parent() is public, which a 1.20.4 subclass's protected parent() cannot implement). A registry lookup
 * forwarding everything to its parent; 1.20.4's class as it was.
 */
public abstract class RegistryLookupDelegate<T> implements class_7225.class_7226<T> {

    public RegistryLookupDelegate() {
    }

    /** parent() (protected in 1.20.4; public here, which a 1.20.4 subclass's protected override still overrides) */
    public abstract class_7225.class_7226<T> method_46725();

    @Override
    public class_5321<? extends class_2378<? extends T>> method_46765() {
        return this.method_46725().method_46765();
    }

    @Override
    public Lifecycle method_46766() {
        return this.method_46725().method_46766();
    }

    @Override
    public Optional<class_6880.class_6883<T>> method_46746(class_5321<T> id) {
        return this.method_46725().method_46746(id);
    }

    @Override
    public Stream<class_6880.class_6883<T>> method_42017() {
        return this.method_46725().method_42017();
    }

    @Override
    public Optional<class_6885.class_6888<T>> method_46733(class_6862<T> tag) {
        return this.method_46725().method_46733(tag);
    }

    @Override
    public Stream<class_6885.class_6888<T>> method_42020() {
        return this.method_46725().method_42020();
    }
}
