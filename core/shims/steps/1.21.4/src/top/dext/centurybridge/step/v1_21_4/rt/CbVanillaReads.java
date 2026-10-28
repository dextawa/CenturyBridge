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
    /**
     * The priority of a layer mixin that restores members: below every mod's. Mixin lets an injector anchored
     * anywhere but HEAD / RETURN / TAIL (an INVOKE, FIELD, CONSTANT, NEW ... site, a @Redirect) into a method another
     * mixin merged only when the merging mixin's priority is lower than the injecting one's
     * (InjectionPoint.checkPriority); at the default 1000 a mod's 1.21.3 @Redirect into the restored body fails
     * ("cannot inject into ... merged by"). Such a mixin holds only the restored members and the state they read:
     * its injectors into 1.21.4's own methods would run before every mod's, so they stay in a mixin of default
     * priority ({@code <name>Hooks}).
     *
     * Layer order (mixin_priority.py rule 3): -8 = 1 - (this release's index - 1.20.2's index) in Chain.RELEASES,
     * one lower per later release. A climbing earlier layer's mixins @Shadow / @Accessor / @Invoker members a later
     * layer restores (the 1.20.2 layer's Realms bridge reads 1.21.6's restored leftX / rightX); Mixin applies a
     * target's mixins by priority and breaks ties by load order (MixinInfo.compareTo: priority, then the order the
     * configs were read), so the later restorer must sit strictly lower or the reader may apply first and the climb
     * strips it (Chain.layerDeclares, MixinJudge, MixinCheck judge strictly).
     */
    int PRIORITY = -8;

    String value();
}
