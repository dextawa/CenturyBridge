package top.dext.centurybridge.step.v26_2.facade;

/**
 * 26.1.2's net.minecraft.util.Tuple, which 26.2 deleted (its callers moved to DFU's Pair): a mutable pair with no
 * game dependency, restored as it was (constructor, getA/setA, getB/setB; javap of 26.1.2's class) so a 26.1.2
 * mod's uses and subclasses keep working. classRenames in s26.json send the mod's references here.
 */
public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
