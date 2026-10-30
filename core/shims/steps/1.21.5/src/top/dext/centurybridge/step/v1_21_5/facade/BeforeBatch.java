package top.dext.centurybridge.step.v1_21_5.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 1.21.4's net.minecraft.gametest.framework.BeforeBatch (net/minecraft/class_6301), gone in 1.21.5; see GameTest */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeBatch {
    /** batch */
    String method_35931();
}
