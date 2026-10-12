package top.dext.centurybridge.step.v1_21_1.rt;

/**
 * 1.21's EntitySelectorParser(StringReader) was {@code this(reader, true)}: a parser that allows selectors
 * (read off 1.21's bytecode). 1.21.1 keeps only the two-argument constructor, so a 1.21 mod's
 * {@code new EntitySelectorParser(reader)} and a subclass's {@code super(reader)} become the two-argument
 * call with that same true (superCtorReshapes).
 */
public final class CbSelectorParsers {

    private CbSelectorParsers() {
    }

    /** the allowSelectors value 1.21's one-argument constructor passed */
    public static boolean allowSelectors() {
        return true;
    }
}
