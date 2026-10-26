package top.dext.centurybridge.step.v26_1.rt;

import java.util.function.Predicate;

/**
 * The filter 1.21.11's EditBox.setFilter(Predicate) (class_342.method_1890) set, restored on EditBox
 * (mixin/CbWallEditBoxFilterMixin) and tested by mixin/CbWallEditBoxFilterReadsMixin. Null until a mod sets one: then
 * the box is 26.1's, untouched.
 */
public interface CbWallEditBoxFilter {

    Predicate<String> cb$filter();
}
