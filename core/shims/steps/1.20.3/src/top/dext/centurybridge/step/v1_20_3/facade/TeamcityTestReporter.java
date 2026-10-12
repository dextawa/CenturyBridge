package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2 TeamcityTestReporter (class_6308), gone in 1.20.3: a game-test reporter
 * writing TeamCity service messages to the log. Its 1.20.2 body on 1.20.3's TestReporter and
 * GameTestInfo (which still have every method it used); members public, as a facade's must be.
 */
public class TeamcityTestReporter implements net.minecraft.class_4531 {

    /** LOGGER */
    public static final org.slf4j.Logger field_33176 = com.mojang.logging.LogUtils.getLogger();
    /** ESCAPER: TeamCity's escaping of ' \n \r | [ ] */
    public static final com.google.common.escape.Escaper field_33177 = com.google.common.escape.Escapers.builder()
        .addEscape('\'', "|'")
        .addEscape('\n', "|n")
        .addEscape('\r', "|r")
        .addEscape('|', "||")
        .addEscape('[', "|[")
        .addEscape(']', "|]")
        .build();

    public TeamcityTestReporter() {
    }

    /** onTestFailed(test): a required test fails, an optional one is ignored */
    @Override
    public void method_22304(net.minecraft.class_4517 test) {
        String name = field_33177.escape(test.method_22169());
        String message = field_33177.escape(test.method_22182().getMessage());
        String details = field_33177.escape(net.minecraft.class_156.method_22321(test.method_22182()));
        field_33176.info("##teamcity[testStarted name='{}']", name);
        if (test.method_22183()) {
            field_33176.info("##teamcity[testFailed name='{}' message='{}' details='{}']", name, message, details);
        } else {
            field_33176.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", name, message, details);
        }
        field_33176.info("##teamcity[testFinished name='{}' duration='{}']", name, test.method_36063());
    }

    /** onTestSuccess(test) */
    @Override
    public void method_33322(net.minecraft.class_4517 test) {
        String name = field_33177.escape(test.method_22169());
        field_33176.info("##teamcity[testStarted name='{}']", name);
        field_33176.info("##teamcity[testFinished name='{}' duration='{}']", name, test.method_36063());
    }
}
