package net.optionfactory.ranges;

import java.util.Optional;
import java.util.stream.Stream;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.RangeOps;

public class Ranges<T, D> {

    private final DiscreteDomain<T, D> domain;
    private final RangeOps<T, D> ops;

    public Ranges(DiscreteDomain<T, D> domain, T emptyValue) {
        Ensure.precondition(domain != null, "cannot create Ranges<T> with a null DiscreteDomain<T, D>");
        this.domain = domain;
        this.ops = new RangeOps<>(domain, emptyValue);
    }

    public Range<T, D> of(Endpoint left, T lower, Optional<T> upper, Endpoint right) {
        return new DenseRange<>(domain, left, lower, upper, right);
    }

    /**
     * returns [ lower, upper )
     *
     * @param lower
     * @param upper
     * @return [lower, upper)
     */
    public Range<T, D> closedOpen(T lower, Optional<T> upper) {
        return new DenseRange<>(domain, Endpoint.Include, lower, upper, Endpoint.Exclude);
    }

    /**
     * returns ( lower, upper ]
     *
     * @param lower
     * @param upper
     * @return (lower, upper]
     */
    public Range<T, D> openClosed(T lower, T upper) {
        return new DenseRange<>(domain, Endpoint.Exclude, lower, Optional.of(upper), Endpoint.Include);
    }

    /**
     * returns ( lower, upper )
     *
     * @param lower
     * @param upper
     * @return (lower, upper)
     */
    public Range<T, D> open(T lower, T upper) {
        return new DenseRange<>(domain, Endpoint.Exclude, lower, Optional.of(upper), Endpoint.Exclude);
    }

    /**
     * returns [ lower, upper ]
     *
     * @param lower
     * @param upper
     * @return [ lower, upper ]
     */
    public Range<T, D> closed(T lower, T upper) {
        return new DenseRange<>(domain, Endpoint.Include, lower, Optional.of(upper), Endpoint.Include);
    }

    /**
     * Creates a singleton Range with the passed get. returns [ get, get ]
     *
     * @param value
     * @return [ value, value+1 )
     */
    public Range<T, D> singleton(T value) {
        return new DenseRange<>(domain, Endpoint.Include, value, Optional.of(value), Endpoint.Include);
    }

    /**
     * @return [ emptyValue, emptyValue )
     */
    public Range<T, D> empty() {
        return ops.empty;
    }

    public Range<T, D> union(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.union(lhs, rhs);
    }

    public Range<T, D> union(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.union(ops.union(first, second), third);
    }

    public Range<T, D> union(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate union for a null iterator of ranges");
        return ranges.reduce(ops::union).orElse(ops.empty);
    }

    public Range<T, D> intersect(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.intersection(lhs, rhs);
    }

    public Range<T, D> intersect(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.intersection(ops.intersection(first, second), third);
    }

    public Range<T, D> intersect(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot intersection a null iterator of ranges");
        return ranges.reduce(ops::intersection).orElse(ops.empty);
    }

    public Range<T, D> symmetricDifference(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.symmetricDifference(lhs, rhs);
    }

    public Range<T, D> symmetricDifference(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.symmetricDifference(ops.symmetricDifference(first, second), third);
    }

    public Range<T, D> symmetricDifference(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate symmetric difference for a null iterator of ranges");
        return ranges.reduce(ops::symmetricDifference).orElse(ops.empty);
    }
    
    public Range<T, D> difference(Range<T, D> lhs, Range<T, D> rhs) {
        return ops.difference(lhs, rhs);
    }

    public Range<T, D> difference(Range<T, D> first, Range<T, D> second, Range<T, D> third) {
        return ops.difference(ops.difference(first, second), third);
    }

    public Range<T, D> difference(Stream<Range<T, D>> ranges) {
        Ensure.precondition(ranges != null, "cannot evaluate difference for a null iterator of ranges");
        return ranges.reduce(ops::difference).orElse(ops.empty);
    }

}
