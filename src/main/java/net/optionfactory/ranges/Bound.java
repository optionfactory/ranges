package net.optionfactory.ranges;

public sealed interface Bound<T> permits Bound.Finite, Bound.PositiveInfinity, Bound.NegativeInfinity {

    static final PositiveInfinity<?> POSITIVE_INFINITY = new PositiveInfinity<>();
    static final NegativeInfinity<?> NEGATIVE_INFINITY = new NegativeInfinity<>();

    public record Finite<T>(T value) implements Bound<T> {

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public record PositiveInfinity<T>() implements Bound<T> {

        @Override
        public String toString() {
            return "+∞";
        }
    }

    public record NegativeInfinity<T>() implements Bound<T> {

        @Override
        public String toString() {
            return "-∞";
        }
    }

    public static <T> Bound<T> finite(T value) {
        return new Finite<>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Bound<T> positiveInfinity() {
        return (Bound<T>) POSITIVE_INFINITY;
    }

    @SuppressWarnings("unchecked")
    static <T> Bound<T> negativeInfinity() {
        return (Bound<T>) NEGATIVE_INFINITY;
    }
}
