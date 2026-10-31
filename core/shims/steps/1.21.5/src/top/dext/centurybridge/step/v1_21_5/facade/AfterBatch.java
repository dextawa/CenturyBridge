package top.dext.centurybridge.step.v1_21_5.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 1.21.4's net.minecraft.gametest.framework.AfterBatch (net/minecraft/class_6300), gone in 1.21.5; see GameTest */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterBatch {
    /** batch */
    String method_35930();
}
