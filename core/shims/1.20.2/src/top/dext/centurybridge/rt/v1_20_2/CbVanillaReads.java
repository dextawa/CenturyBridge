package top.dext.centurybridge.rt.v1_20_2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a member a layer mixin restores on a game class (a 1.20.1 method 1.20.2 dropped) whose 1.20.2 read path
 * vanilla takes through it: the value names the layer mixin that routes 1.20.2's read through the restored member,
 * then how. A mod's 1.20.1 mixin on that member then changes what vanilla does, as it did on 1.20.1; MixinJudge
 * (engine LayerMembers) counts it bridged only with this marker and only when the named mixin is in the layer.
 * Class retention: read from the bytecode, never loaded.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CbVanillaReads {
    /**
     * The priority of a layer mixin that restores members: below every mod's. Mixin lets an injector anchored
     * anywhere but HEAD / RETURN / TAIL (an INVOKE, FIELD, CONSTANT, NEW ... site, a @Redirect) into a method another
     * mixin merged only when the merging mixin's priority is lower than the injecting one's
     * (InjectionPoint.checkPriority); at the default 1000 a mod's 1.20.1 @Redirect into the restored body fails
     * ("cannot inject into ... merged by"). Such a mixin holds only the restored members (and the trampolines into
     * them); its injectors into 1.20.2's own methods stay in a mixin of default priority.
     *
     * Layer order (mixin_priority.py rule 3): 1 = 1 - (this release's index - 1.20.2's index) in Chain.RELEASES,
     * one lower per later release. A climbing earlier layer's mixins @Shadow / @Accessor / @Invoker members a later
     * layer restores (the 1.20.2 layer's Realms bridge reads 1.21.6's restored leftX / rightX); Mixin applies a
     * target's mixins by priority and breaks ties by load order (MixinInfo.compareTo: priority, then the order the
     * configs were read), so the later restorer must sit strictly lower or the reader may apply first and the climb
     * strips it (Chain.layerDeclares, MixinJudge, MixinCheck judge strictly).
     */
    int PRIORITY = 1;

    String value();

    /**
     * When vanilla's path takes only part of the member (its answer and the checks around a call it cannot reproduce),
     * what it carries, "<injector>@<At value>" ("Inject@HEAD", "ModifyReturnValue@RETURN"); any other injector on the
     * member stays open ("restored-member: vanilla path partial"). Absent = the whole member.
     */
    String[] carries() default {};
}
