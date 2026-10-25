package top.dext.centurybridge.step.v1_21_4.rt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member a layer mixin restores on a game class (a 1.21.3 method 1.21.4 dropped) whose 1.21.4 read path
 * vanilla takes through it: the value names the layer mixin that routes 1.21.4's read through the restored member,
 * then how. A mod's 1.21.3 mixin on that member then changes what vanilla does, as it did on 1.21.3; MixinJudge
 * (engine LayerMembers) counts it bridged only with this marker and only when the named mixin is in the layer.
 * Class retention: read from the bytecode, never loaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CbVanillaReads {
    String value();

    /**
     * When the route carries only part of the member (vanilla takes its answer, not its body): the injectors it carries,
     * each "<injector annotation simple name>@<@At value>" ("Inject@HEAD"). Absent: vanilla runs the restored body itself.
     */
    String[] carries() default {};
}
