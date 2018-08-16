package net.optionfactory.ranges.ops;

public abstract class Ensure {

    public static void precondition(boolean test, String format, Object... params) {
        if (test) {
            return;
        }
        throw new IllegalArgumentException(String.format(format, params));
    }

}
