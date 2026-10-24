package top.dext.centurybridge.facade.v1_20_2;

/**
 * ICU 71.1's com.ibm.icu.util.NounClass (gone in 73.2, whose DisplayOptions.NounClass adds UNDEFINED first): the same eight
 * constants in the same order, so ordinals, values() and valueOf answer as in 71.1
 */
public enum IcuNounClass {
    OTHER,
    NEUTER,
    FEMININE,
    MASCULINE,
    ANIMATE,
    INANIMATE,
    PERSONAL,
    COMMON
}
