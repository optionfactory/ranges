package net.optionfactory.ranges;

public sealed interface Bound<T> permits Bound.Finite, Bound.PositiveInfinity, Bound.NegativeInfinity {

    static final PositiveInfinity<?> POS_INF = new PositiveInfinity<>();
    static final NegativeInfinity<?> NEG_INF = new NegativeInfinity<>();

    record Finite<T>(T value) implements Bound<T> {

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    record PositiveInfinity<T>() implements Bound<T> {

        @Override
        public String toString() {
            return "+∞";
        }
    }

    record NegativeInfinity<T>() implements Bound<T> {

        @Override
        public String toString() {
            return "-∞";
        }
    }

    static <T> Bound<T> finite(T value) {
        return new Finite<>(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Bound<T> posInf() {
        return (Bound<T>) POS_INF;
    }

    @SuppressWarnings("unchecked")
    static <T> Bound<T> negInf() {
        return (Bound<T>) NEG_INF;
    }
}
