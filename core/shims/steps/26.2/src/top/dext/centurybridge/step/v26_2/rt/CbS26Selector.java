package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.InvertableSetOptionState;
import net.minecraft.commands.arguments.selector.options.SetOnceOptionState;

/**
 * 26.1.2's EntitySelectorParser option flags on 26.2's option states. 26.1.2 kept one boolean per fact
 * (hasNameEquals, hasNameNotEquals, isLimited, ...); 26.2 keeps one state object per option:
 * InvertableSetOptionState (name, gamemode, team, type: NONE, SINGLE once a positive element is parsed, MULTIPLE
 * once a negative element or a tag is) and SetOnceOptionState (limit, sort, scores, advancements: parsed or not).
 *
 * The 26.1.2 flags read the same facts back: "equals" is SINGLE, "not equals" (and 26.1.2's typeInverse) is
 * MULTIPLE, a set-once flag is "parsed". Setting a flag true is the state 26.2's own options move to for the same
 * parse (markParsedPositiveElement / markParsedNegativeElement / markParsed); setting it false undoes exactly
 * that flag (the state goes back to NONE only from the state the flag stands for). One 26.1.2 combination has no
 * 26.2 state: both "equals" and "not equals" true. 26.1.2's own options never produce it (a positive element is
 * refused once a negative one was parsed, and the other way round); a mod that sets both gets the last one set.
 * isTypeLimited is 26.1.2's own test, a type set by limitToType (the field, layer AW).
 */
public final class CbS26Selector {
    private CbS26Selector() {}

    private static boolean single(InvertableSetOptionState s) {
        return s.state == InvertableSetOptionState.Limitation.SINGLE;
    }

    private static boolean multiple(InvertableSetOptionState s) {
        return s.state == InvertableSetOptionState.Limitation.MULTIPLE;
    }

    private static void setSingle(InvertableSetOptionState s, boolean v) {
        if (v) {
            s.markParsedPositiveElement();
        } else if (single(s)) {
            s.state = InvertableSetOptionState.Limitation.NONE;
        }
    }

    private static void setMultiple(InvertableSetOptionState s, boolean v) {
        if (v) {
            s.markParsedNegativeElement();
        } else if (multiple(s)) {
            s.state = InvertableSetOptionState.Limitation.NONE;
        }
    }

    private static boolean parsed(SetOnceOptionState s) {
        return !s.canParse();
    }

    private static void setParsed(SetOnceOptionState s, boolean v) {
        s.hasValue = v;
    }

    public static boolean hasNameEquals(EntitySelectorParser p) {
        return single(p.nameOption());
    }

    public static void setHasNameEquals(EntitySelectorParser p, boolean v) {
        setSingle(p.nameOption(), v);
    }

    public static boolean hasNameNotEquals(EntitySelectorParser p) {
        return multiple(p.nameOption());
    }

    public static void setHasNameNotEquals(EntitySelectorParser p, boolean v) {
        setMultiple(p.nameOption(), v);
    }

    public static boolean isLimited(EntitySelectorParser p) {
        return parsed(p.limitedOption());
    }

    public static void setLimited(EntitySelectorParser p, boolean v) {
        setParsed(p.limitedOption(), v);
    }

    public static boolean isSorted(EntitySelectorParser p) {
        return parsed(p.sortedOption());
    }

    public static void setSorted(EntitySelectorParser p, boolean v) {
        setParsed(p.sortedOption(), v);
    }

    public static boolean hasGamemodeEquals(EntitySelectorParser p) {
        return single(p.gamemodeOption());
    }

    public static void setHasGamemodeEquals(EntitySelectorParser p, boolean v) {
        setSingle(p.gamemodeOption(), v);
    }

    public static boolean hasGamemodeNotEquals(EntitySelectorParser p) {
        return multiple(p.gamemodeOption());
    }

    public static void setHasGamemodeNotEquals(EntitySelectorParser p, boolean v) {
        setMultiple(p.gamemodeOption(), v);
    }

    public static boolean hasTeamEquals(EntitySelectorParser p) {
        return single(p.teamOption());
    }

    public static void setHasTeamEquals(EntitySelectorParser p, boolean v) {
        setSingle(p.teamOption(), v);
    }

    public static boolean hasTeamNotEquals(EntitySelectorParser p) {
        return multiple(p.teamOption());
    }

    public static void setHasTeamNotEquals(EntitySelectorParser p, boolean v) {
        setMultiple(p.teamOption(), v);
    }

    public static void setTypeLimitedInversely(EntitySelectorParser p) {
        p.typeOption().markParsedNegativeElement();
    }

    public static boolean isTypeLimited(EntitySelectorParser p) {
        return p.type != null;
    }

    public static boolean isTypeLimitedInversely(EntitySelectorParser p) {
        return multiple(p.typeOption());
    }

    public static boolean hasScores(EntitySelectorParser p) {
        return parsed(p.scoresOption());
    }

    public static void setHasScores(EntitySelectorParser p, boolean v) {
        setParsed(p.scoresOption(), v);
    }

    public static boolean hasAdvancements(EntitySelectorParser p) {
        return parsed(p.advancementsOption());
    }

    public static void setHasAdvancements(EntitySelectorParser p, boolean v) {
        setParsed(p.advancementsOption(), v);
    }
}
