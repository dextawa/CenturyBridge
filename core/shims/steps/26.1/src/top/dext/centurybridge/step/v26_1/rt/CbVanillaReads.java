package top.dext.centurybridge.step.v26_1.rt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member a layer mixin restores on a game class (a 1.21.11 method 26.1 dropped) whose 26.1 read path
 * vanilla takes through it: the value names the layer mixin that routes 26.1's read through the restored member,
 * then how. A mod's 1.21.11 mixin on that member then changes what vanilla does, as it did on 1.21.11; MixinJudge
 * (engine LayerMembers) counts it bridged only with this marker and only when the named mixin is in the layer.
 * Class retention: read from the bytecode, never loaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CbVanillaReads {
    String value();
}
