package top.dext.centurybridge.step.v1_20_5.rt;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.Format;

/**
 * The super calls of {@link CbLibLog4j}'s FastDateFormat / Format members (superRedirects): a mod subclass's
 * super.parse(..) reached 2.19.0's FastDateFormat body -- its parser -- never the subclass's own override again.
 */
public final class CbLibLog4jSuper {
    private CbLibLog4jSuper() {
    }

    /** super.parse(String) on FastDateFormat */
    public static Date parse(FastDateFormat self, String source) throws ParseException {
        return CbLibLog4j.parser(self).parse(source);
    }

    /** super.parse(String, ParsePosition) on FastDateFormat */
    public static Date parse(FastDateFormat self, String source, ParsePosition pos) {
        return CbLibLog4j.parser(self).parse(source, pos);
    }

    /** super.parse(String, ParsePosition, Calendar) on FastDateFormat */
    public static boolean parse(FastDateFormat self, String source, ParsePosition pos, Calendar calendar) {
        return CbLibLog4j.parser(self).parse(source, pos, calendar);
    }

    /** super.parseObject(String, ParsePosition) on FastDateFormat */
    public static Object parseObject(FastDateFormat self, String source, ParsePosition pos) {
        return CbLibLog4j.parser(self).parseObject(source, pos);
    }

    /** super.parseObject(String) on Format: 2.19.0's body, whose parseObject(String, ParsePosition) call was virtual */
    public static Object parseObject(Format self, String source) throws ParseException {
        return CbLibLog4j.parseObject(self, source);
    }
}
