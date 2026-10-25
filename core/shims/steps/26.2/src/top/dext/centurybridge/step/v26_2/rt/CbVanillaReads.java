package top.dext.centurybridge.step.v26_2.rt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member a layer mixin restores on a game class (a 26.1.2 method 26.2 dropped) whose 26.2 read path
 * vanilla takes through it: the value names the layer mixin that routes 26.2's read through the restored member,
 * then how. A mod's 26.1.2 mixin on that member then changes what vanilla does, as it did on 26.1.2; MixinJudge
 * (engine LayerMembers) counts it bridged only with this marker and only when the named mixin is in the layer.
 * When vanilla's path takes only part of the member -- its answer, not its body -- {@link #carries} lists what it
 * carries ("Inject@HEAD", "ModifyReturnValue@RETURN": injector annotation and @At); any other injector on the member
 * stays open as "vanilla path partial". Left out, the route runs the whole restored body.
 * Class retention: read from the bytecode, never loaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CbVanillaReads {
    /**
     * The priority of a layer mixin that restores members: below every mod's. Mixin lets an injector anchored
     * anywhere but HEAD / RETURN / TAIL into a method another mixin merged only when the merging mixin's priority is
     * lower than the injecting one's (InjectionPoint.checkPriority); at the default 1000 a mod's 26.1.2 @Redirect into
     * the restored body fails ("cannot inject into ... merged by"). Such a mixin holds only the restored members; its
     * injectors into 26.2's own methods stay in a mixin of default priority.
     */
    int PRIORITY = 1;

    String value();

    /** what vanilla's path carries, "<injector>@<At value>"; absent = the whole member */
    String[] carries() default {};
}
