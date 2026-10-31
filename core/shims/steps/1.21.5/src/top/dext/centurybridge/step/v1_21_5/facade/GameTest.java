package top.dext.centurybridge.step.v1_21_5.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1.21.4's net.minecraft.gametest.framework.GameTest (net/minecraft/class_6302), gone in 1.21.5 (tests are
 * test_instance registry entries there). Converted mods' annotations name this class (classRenames); the elements keep
 * 1.21.4's names and defaults (javap -v 1.21.4 class_6302): timeoutTicks 100, batch "defaultBatch", skyAccess false,
 * rotationSteps 0, required true, manualOnly false, template "", setupTicks 0, attempts 1, requiredSuccesses 1. Read
 * by facade/GameTestRegistry, which registers the tests on 1.21.5 (rt/CbLegacyGameTests).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GameTest {
    /** timeoutTicks */
    int method_35932() default 100;

    /** batch */
    String method_35933() default "defaultBatch";

    /** skyAccess */
    boolean method_57098() default false;

    /** rotationSteps */
    int method_35934() default 0;

    /** required */
    boolean method_35935() default true;

    /** manualOnly */
    boolean method_57962() default false;

    /** template */
    String method_35936() default "";

    /** setupTicks */
    long method_35937() default 0L;

    /** attempts */
    int method_35938() default 1;

    /** requiredSuccesses */
    int method_35939() default 1;
}
