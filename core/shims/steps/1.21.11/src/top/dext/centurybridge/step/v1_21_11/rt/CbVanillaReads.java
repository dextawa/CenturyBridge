package top.dext.centurybridge.step.v1_21_11.rt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member a layer mixin restores on a game class (a 1.21.10 method 1.21.11 dropped or reshaped) whose 1.21.11
 * read path vanilla takes through it: the value names the layer mixin that routes 1.21.11's read through the restored
 * member, then how. A mod's 1.21.10 mixin on that member then changes what vanilla does, as it did on 1.21.10;
 * MixinJudge (engine LayerMembers) counts it bridged only with this marker and only when the named mixin is in the
 * layer. When vanilla's path takes only part of the member -- its answer, not its body -- {@link #carries} lists what
 * it carries ("Inject@HEAD", "ModifyReturnValue@RETURN": injector annotation and @At); any other injector on the
 * member stays open as "vanilla path partial". Left out, the route runs the whole restored body.
 * Class retention: read from the bytecode, never loaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CbVanillaReads {
    /**
     * The priority of a layer mixin that restores members: below every mod's, so a mod's 1.21.10 injector may anchor
     * anywhere in a restored body (Mixin's InjectionPoint.checkPriority refuses an injection into a method another
     * mixin merged at a higher priority).
     */
    int PRIORITY = 1;

    /**
     * The priority of a layer route mixin (one that merges nothing) whose injectors into a 1.21.11 method must run
     * before every mod's: a HEAD route that cancels and runs the 1.21.11 method again inside the restored member (the
     * mods' injectors there then run once, inside, never on the cancelled outer call), or a RETURN route that replaces
     * 1.21.11's answer (a native mod's RETURN injector then sees, and may change, the routed answer instead of being
     * overwritten by it). Mixin applies mixins in ascending priority, and each injector at a point goes after the ones
     * applied before it. Restored members stay in their own mixin at {@link #PRIORITY}.
     */
    int ROUTE_FIRST = 1;

    String value();

    /** what vanilla's path carries, "&lt;injector&gt;@&lt;At value&gt;"; absent = the whole member */
    String[] carries() default {};
}
