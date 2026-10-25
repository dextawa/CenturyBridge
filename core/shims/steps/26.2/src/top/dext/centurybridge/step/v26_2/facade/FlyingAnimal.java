package top.dext.centurybridge.step.v26_2.facade;

/**
 * 26.1.2's FlyingAnimal (gone on 26.2): reached by classRenames. 26.2's Bee and Parrot, which implemented it,
 * implement it again (mixin/CbS26FlyingAnimalMixin; both still declare isFlying()), so a 26.1.2 type test, cast or
 * call through it answers as on 26.1.2, and a 26.1.2 mob implementing it loads.
 */
public interface FlyingAnimal {
    boolean isFlying();
}
