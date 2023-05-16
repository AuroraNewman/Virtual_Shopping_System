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

    public static void assertEquals(Object expected, Object actual) {
        try {
            if (expected != actual)
                throw new AssertionError(buildMessage(expected, actual));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }

    public static void assertNotEquals(Object expected, Object actual) {
        try {
            if (expected == actual)
                throw new AssertionError(buildMessage(expected, actual));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }

    public static void assertNull(Object expected) {
        try {
            if (expected != null)
                throw new AssertionError(buildMessage(null, expected));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }

    public static void assertNotNull(Object expected) {
        try {
            if (expected == null)
                throw new AssertionError(buildMessage("not null", expected));
        } catch (AssertionError e) {
            System.err.println("Assertion failed: " + e.getMessage());
        }
    }
    public static void assertExpectedThrowable(
            Class<? extends Throwable> expectedClass, Throwable t) {
        if (!expectedClass.isInstance(t)) {
            AssertionError e = new AssertionError(
                    buildMessage(expectedClass.getName(), t));

            // update cause of assertion error with t
            e.initCause(t);

            throw e;
        }
    }

    private static String buildMessage(Object expected, Object actual) {
        final StringBuilder sb = new StringBuilder("expected ");
        sb.append("\"").append(expected).append("\"");
        sb.append(", actual value is ");
        sb.append("\"").append(actual).append("\"");

        return sb.toString();
    }
}
