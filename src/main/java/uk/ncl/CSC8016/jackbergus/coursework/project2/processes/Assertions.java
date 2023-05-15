package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

public class Assertions {
    private Assertions(){}

    /**
     * Assert that given boolean is true
     */
    public static void assertTrue(boolean b) {
        try {
            if (!b)
                throw new AssertionError(buildMessage(true, b));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }

    public static void assertFalse(boolean b) {
        try {
            if (b)
                throw new AssertionError(buildMessage(false, b));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }

    private static String buildMessage(Object expected, Object actual) {
        final StringBuilder sb = new StringBuilder("expected ");
        sb.append("\"").append(expected).append("\"");
        sb.append(", actual value is ");
        sb.append("\"").append(actual).append("\"");

        return sb.toString();    }

}