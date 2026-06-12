package net.optionfactory.ranges;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.examples.IntegerDomain;

public class RangeMother {

    public static final DiscreteDomain<Integer, Long> domain = new IntegerDomain();
    public static final Ranges<Integer, Long> ranges = new Ranges<>(domain);

    public static Range<Integer, Long> r(int lower, int upper) {
        return ranges.closed(lower, upper);
    }

    public static Range<Integer, Long> r(Endpoint left, int lower, int upper, Endpoint right) {
        return ranges.of(left, Bound.finite(lower), Bound.finite(upper), right);
    }

    public static EmptyRange<Integer, Long> empty() {
        return new EmptyRange<>(domain);
    }

    public static Pair<Integer, Integer> p(int lower, int upper) {
        return Pair.of(lower, upper);
    }

    public static SparseRange<Integer, Long> r(Pair<Integer, Integer> former, Pair<Integer, Integer> latter) {
        return sparse(Arrays.asList(former, latter).iterator());
    }

    public static SparseRange<Integer, Long> r(Pair<Integer, Integer> first, Pair<Integer, Integer> second, Pair<Integer, Integer> third) {
        return sparse(Arrays.asList(first, second, third).iterator());
    }

    private static SparseRange<Integer, Long> sparse(Iterator<Pair<Integer, Integer>> pairs) {
        final List<DenseRange<Integer, Long>> densified = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(pairs, Spliterator.ORDERED),
                false)
                // Use the factory to resolve bounds, cast back to DenseRange since we know p() provides valid bounds
                .map(pair -> (DenseRange<Integer, Long>) ranges.closed(pair.first(), pair.second()))
                .collect(Collectors.toList());
        return new SparseRange<Integer, Long>(domain, densified);
    }

    public static class Pair<T1, T2> {

        private final T1 first;
        private final T2 second;

        public Pair(T1 f, T2 l) {
            this.first = f;
            this.second = l;
        }

        public T1 first() {
            return first;
        }

        public T2 second() {
            return second;
        }

        public Pair<T2, T1> flip() {
            return Pair.of(second, first);
        }

        public <R1, R2> Pair<R1, R2> map(Function<T1, R1> withFirst, Function<T2, R2> withSecond) {
            return Pair.of(withFirst.apply(first), withSecond.apply(second));
        }

        @Override
        public boolean equals(Object rhs) {
            if (!(rhs instanceof Pair)) {
                return false;
            }
            final Pair<?, ?> other = (Pair<?, ?>) rhs;
            return Objects.equals(this.first, other.first)
                    && Objects.equals(this.second, other.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        @Override
        public String toString() {
            return String.format("(%s,%s)", first, second);
        }

        public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
            return new Pair<T1, T2>(first, second);
        }
    }
}
