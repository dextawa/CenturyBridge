package top.dext.centurybridge.rt.v1_20_2;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 1.20.1 LootDataManager.createComposite(conditions[]) / createComposite(functions[]) on 1.20.2,
 * where both statics are gone with the private classes they instantiated
 * (LootDataManager.CompositePredicate / FunctionSequence). Those classes are ported here as they
 * were: the conjunction / composition of the parts, a validate() that names each part ".term[i]" /
 * ".function[i]", and a getType() that throws -- a composite is a runtime value with no data type,
 * in 1.20.1 as here.
 */
public final class CbLootComposites {

    private CbLootComposites() {
    }

    /** LootDataManager.createComposite(LootCondition[]) */
    public static net.minecraft.class_5341 method_51201(net.minecraft.class_5341[] terms) {
        return new Conditions(terms);
    }

    /** LootDataManager.createComposite(LootFunction[]) */
    public static net.minecraft.class_117 method_51200(net.minecraft.class_117[] functions) {
        return new Functions(functions);
    }

    static final class Conditions implements net.minecraft.class_5341 {
        private final net.minecraft.class_5341[] terms;
        private final Predicate<net.minecraft.class_47> composite;

        Conditions(net.minecraft.class_5341[] terms) {
            this.terms = terms;
            this.composite = net.minecraft.class_217.method_924(Arrays.asList(terms));
        }

        @Override
        public boolean test(net.minecraft.class_47 context) {
            return this.composite.test(context);
        }

        @Override
        public void method_292(net.minecraft.class_58 reporter) {
            net.minecraft.class_5341.super.method_292(reporter);
            for (int i = 0; i < this.terms.length; i++) {
                this.terms[i].method_292(reporter.method_364(".term[" + i + "]"));
            }
        }

        @Override
        public net.minecraft.class_5342 method_29325() {
            throw new UnsupportedOperationException();
        }
    }

    static final class Functions implements net.minecraft.class_117 {
        private final net.minecraft.class_117[] functions;
        private final BiFunction<net.minecraft.class_1799, net.minecraft.class_47, net.minecraft.class_1799> composite;

        Functions(net.minecraft.class_117[] functions) {
            this.functions = functions;
            this.composite = net.minecraft.class_131.method_594(Arrays.asList(functions));
        }

        @Override
        public net.minecraft.class_1799 apply(net.minecraft.class_1799 stack, net.minecraft.class_47 context) {
            return this.composite.apply(stack, context);
        }

        @Override
        public void method_292(net.minecraft.class_58 reporter) {
            net.minecraft.class_117.super.method_292(reporter);
            for (int i = 0; i < this.functions.length; i++) {
                this.functions[i].method_292(reporter.method_364(".function[" + i + "]"));
            }
        }

        @Override
        public net.minecraft.class_5339 method_29321() {
            throw new UnsupportedOperationException();
        }
    }
}
