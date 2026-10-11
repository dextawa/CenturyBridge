package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_8551$class_8553 (AnyOfCondition.Serializer), deleted in
 * 1.20.2. 1.20.1's create(terms) was new AnyOfCondition(terms); the 1.20.2 constructor from a list
 * is package-private, and the public AnyOfCondition.Builder builds exactly that constructor's
 * result from builders that answer the given terms.
 */
public class class_8551$class_8553 extends class_186$class_188 {

    public class_8551$class_8553() {
    }

    /** create(terms) */
    public net.minecraft.class_8551 method_51728(net.minecraft.class_5341[] terms) {
        net.minecraft.class_5341.class_210[] builders = new net.minecraft.class_5341.class_210[terms.length];
        for (int i = 0; i < terms.length; i++) {
            net.minecraft.class_5341 term = terms[i];
            builders[i] = () -> term;
        }
        return (net.minecraft.class_8551) new net.minecraft.class_8551.class_8552(builders).build();
    }

    @Override
    public net.minecraft.class_186 method_51726(net.minecraft.class_5341[] terms) {
        return method_51728(terms);
    }
}
