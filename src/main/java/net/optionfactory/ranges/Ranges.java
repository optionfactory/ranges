package net.optionfactory.ranges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.ops.BoundComparator;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.RangeOps;

public class Ranges<T, D> {

    private final DiscreteDomain<T, D> domain;
    private final RangeOps<T, D> ops;
    private final BoundComparator<T> boundCmp;

    public Ranges(DiscreteDomain<T, D> domain) {
        Ensure.precondition(domain != null, "cannot create Ranges<T> with a null DiscreteDomain<T, D>");
        this.domain = domain;
        this.ops = new RangeOps<>(domain);
        this.boundCmp = new BoundComparator<>(domain);
    }

    public Builder<T, D> builder() {
        return new Builder<>(this, this.ops);
    }

    public static class Builder<T, D> {

        private final Ranges<T, D> ranges;
        private final RangeOps<T, D> ops;
        private final List<DenseRange<T, D>> components = new ArrayList<>();

        public Builder(Ranges<T, D> ranges, RangeOps<T, D> ops) {
            this.ranges = ranges;
            this.ops = ops;
        }

        public Builder<T, D> add(Range<T, D> range) {
            Ensure.precondition(range != null, "cannot add a null range");
            if (!range.isEmpty()) {
                components.addAll(range.densified());
            }
            return this;
        }

        @SafeVarargs
        public final Builder<T, D> add(Range<T, D>... ranges) {
            Ensure.precondition(ranges != null, "ranges array cannot be null");
            for (Range<T, D> r : ranges) {
                add(r);
            }
            return this;
        }

        public Builder<T, D> addAll(Iterable<? extends Range<T, D>> ranges) {
            Ensure.precondition(ranges != null, "ranges iterable cannot be null");
            ranges.forEach(this::add);
            return this;
        }

        public Builder<T, D> addAll(Stream<? extends Range<T, D>> ranges) {
            Ensure.precondition(ranges != null, "ranges stream cannot be null");
            ranges.forEach(this::add);
            return this;
        }

        public Builder<T, D> combine(Builder<T, D> other) {
            Ensure.precondition(other != null, "cannot combine with a null builder");
            this.components.addAll(other.components);
            return this;
        }

        public Builder<T, D> add(Endpoint left, Bound<T> lower, Bound<T> upper, Endpoint right) {
            return add(ranges.of(left, lower, upper, right));
        }

        public Builder<T, D> addClosed(T lower, T upper) {
            return add(ranges.closed(lower, upper));
        }

        public Builder<T, D> addClosedOpen(T lower, T upper) {
            return add(ranges.closedOpen(lower, upper));
        }

        public Builder<T, D> addOpenClosed(T lower, T upper) {
            return add(ranges.openClosed(lower, upper));
        }

        public Builder<T, D> addOpen(T lower, T upper) {
            return add(ranges.open(lower, upper));
        }

        public Builder<T, D> addSingleton(T value) {
            return add(ranges.singleton(value));
        }

        public Builder<T, D> addAtLeast(T lower) {
            return add(ranges.atLeast(lower));
        }

        public Builder<T, D> addGreaterThan(T lower) {
            return add(ranges.greaterThan(lower));
        }

        public Builder<T, D> addAtMost(T upper) {
            return add(ranges.atMost(upper));
        }

        public Builder<T, D> addLessThan(T upper) {
            return add(ranges.lessThan(upper));
        }

        public Builder<T, D> addAllDomain() {
            return add(ranges.all());
        }

        public Range<T, D> build() {
            return ops.canonicalize(components);
        }
    }

    @SafeVarargs
    public final Range<T, D> canonicalize(DenseRange<T, D>... ranges) {
        Ensure.precondition(ranges != null, "cannot canonicalize a null array");
        return ops.canonicalize(new ArrayList<>(Arrays.asList(ranges)));
    }

    public Range<T, D> canonicalize(Iterable<DenseRange<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot canonicalize a null iterable");
        final var list = new ArrayList<DenseRange<T, D>>();
        ranges.forEach(list::add);
        return ops.canonicalize(list);
    }

    private Range<T, D> resolve(Endpoint left, Bound<T> lower, Bound<T> upper, Endpoint right) {
        Ensure.precondition(lower != null, "lower bound cannot be null");
        Ensure.precondition(upper != null, "upper bound cannot be null");
        Ensure.precondition(lower instanceof Bound.Finite || left != Endpoint.Include, "Cannot create a left-inclusive range with an unbounded lower limit");
        Ensure.precondition(upper instanceof Bound.Finite || right != Endpoint.Include, "Cannot create a right-inclusive range with an unbounded upper limit");
        final Bound<T> begin;
        if (lower instanceof Bound.Finite<T> f && left == Endpoint.Exclude) {
            begin = domain.next(f.value())
                    .map(v -> (Bound<T>) Bound.finite(v))
                    .orElseThrow(() -> new IllegalArgumentException("Lower bound exceeds domain limits"));
        } else {
            begin = lower;
        }
        final Bound<T> end;
        if (upper instanceof Bound.Finite<T> f && right == Endpoint.Include) {
            end = domain.next(f.value())
                    .map(v -> (Bound<T>) Bound.finite(v))
                    .orElseGet(Bound::positiveInfinity);
        } else {
            end = upper;
        }
        if (boundCmp.compare(begin, end) >= 0) {
            return empty();
        }
        return new DenseRange<>(domain, begin, end);
    }

    public Range<T, D> of(Endpoint left, Bound<T> lower, Bound<T> upper, Endpoint right) {
        return resolve(left, lower, upper, right);
    }

    public Range<T, D> closedOpen(T lower, T upper) {
        return resolve(Endpoint.Include, Bound.finite(lower), Bound.finite(upper), Endpoint.Exclude);
    }

    public Range<T, D> openClosed(T lower, T upper) {
        return resolve(Endpoint.Exclude, Bound.finite(lower), Bound.finite(upper), Endpoint.Include);
    }

    public Range<T, D> open(T lower, T upper) {
        return resolve(Endpoint.Exclude, Bound.finite(lower), Bound.finite(upper), Endpoint.Exclude);
    }

    public Range<T, D> closed(T lower, T upper) {
        return resolve(Endpoint.Include, Bound.finite(lower), Bound.finite(upper), Endpoint.Include);
    }

    public Range<T, D> singleton(T value) {
        return resolve(Endpoint.Include, Bound.finite(value), Bound.finite(value), Endpoint.Include);
    }

    public Range<T, D> atLeast(T lower) {
        return resolve(Endpoint.Include, Bound.finite(lower), Bound.positiveInfinity(), Endpoint.Exclude);
    }

    public Range<T, D> greaterThan(T lower) {
        return resolve(Endpoint.Exclude, Bound.finite(lower), Bound.positiveInfinity(), Endpoint.Exclude);
    }

    public Range<T, D> atMost(T upper) {
        return resolve(Endpoint.Exclude, Bound.negativeInfinity(), Bound.finite(upper), Endpoint.Include);
    }

    public Range<T, D> lessThan(T upper) {
        return resolve(Endpoint.Exclude, Bound.negativeInfinity(), Bound.finite(upper), Endpoint.Exclude);
    }

    public Range<T, D> all() {
        return resolve(Endpoint.Exclude, Bound.negativeInfinity(), Bound.positiveInfinity(), Endpoint.Exclude);
    }

    public Range<T, D> empty() {
        return ops.empty;
    }

    public Range<T, D> union(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.union(lhs, rhs);
    }

    public Range<T, D> union(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.union(ops.union(first, second), third);
    }

    @SafeVarargs
    public final Range<T, D> union(Range<T, D>... ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate union for a null array of ranges");
        return Arrays.stream(ranges).collect(toRange());
    }

    public Range<T, D> union(Iterable<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate union for a null iterable of ranges");
        return StreamSupport.stream(ranges.spliterator(), false).collect(toRange());
    }

    public Range<T, D> union(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate union for a null stream of ranges");
        return ranges.collect(toRange());
    }

    public Range<T, D> intersect(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.intersection(lhs, rhs);
    }

    public Range<T, D> intersect(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.intersection(ops.intersection(first, second), third);
    }

    @SafeVarargs
    public final Range<T, D> intersect(Range<T, D>... ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate intersection for a null array of ranges");
        return intersect(Arrays.stream(ranges));
    }

    public Range<T, D> intersect(Iterable<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate intersection for a null iterable of ranges");
        return intersect(StreamSupport.stream(ranges.spliterator(), false));
    }

    public Range<T, D> intersect(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot intersect a null stream of ranges");
        final var it = ranges.iterator();
        if (!it.hasNext()) {
            return empty();
        }
        Range<T, D> current = it.next();
        while (it.hasNext() && !current.isEmpty()) {
            current = ops.intersection(current, it.next());
        }
        return current;
    }

    public Range<T, D> symmetricDifference(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.symmetricDifference(lhs, rhs);
    }

    public Range<T, D> symmetricDifference(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.symmetricDifference(ops.symmetricDifference(first, second), third);
    }

    @SafeVarargs
    public final Range<T, D> symmetricDifference(Range<T, D>... ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate symmetric difference for a null array of ranges");
        return symmetricDifference(Arrays.stream(ranges));
    }

    public Range<T, D> symmetricDifference(Iterable<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate symmetric difference for a null iterable of ranges");
        return symmetricDifference(StreamSupport.stream(ranges.spliterator(), false));
    }

    public Range<T, D> symmetricDifference(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate symmetric difference for a null stream of ranges");
        return ranges.reduce(ops::symmetricDifference).orElse(ops.empty);
    }

    public Range<T, D> difference(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.difference(lhs, rhs);
    }

    public Range<T, D> difference(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.difference(ops.difference(first, second), third);
    }

    @SafeVarargs
    public final Range<T, D> difference(Range<T, D>... ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate difference for a null array of ranges");
        return difference(Arrays.stream(ranges));
    }

    public Range<T, D> difference(Iterable<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate difference for a null iterable of ranges");
        return difference(StreamSupport.stream(ranges.spliterator(), false));
    }

    public Range<T, D> difference(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate difference for a null stream of ranges");
        return ranges.reduce(ops::difference).orElse(ops.empty);
    }

    public Collector<Range<T, D>, ?, Range<T, D>> toRange() {
        return Collector.of(
                this::builder,
                Builder::add,
                Builder::combine,
                Builder::build
        );
    }
}
