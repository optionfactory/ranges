package net.optionfactory.ranges;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.iterators.DenseRangeSpliterator;
import net.optionfactory.ranges.iterators.RangeIterator;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.JustBeforeNothing;
import net.optionfactory.ranges.ops.RangeComparator;

public class DenseRange<T, D> implements Range<T, D> {

    private final DiscreteDomain<T, D> domain;
    private final T begin;
    private final Optional<T> end;

    public DenseRange(DiscreteDomain<T, D> domain, Endpoint left, T lower, Optional<T> upper, Endpoint right) {
        Ensure.precondition(domain != null, "trying to create a DenseRange<T> with a null DiscreteDomain<T>");
        Ensure.precondition(lower != null, "trying to create a DenseRange<T> with null lower bound");
        Ensure.precondition(upper != null, "trying to create a DenseRange<T> with null upper bound");
        Ensure.precondition(JustBeforeNothing.compare(domain, Optional.of(lower), upper) <= 0, "trying to create a DenseRange<T> a lower bound greater than upper bound");
        Ensure.precondition(upper.isPresent() || right != Endpoint.Include, "cannot create a right inclusive range with right bound set as Optional.empty");
        this.domain = domain;
        this.begin = left == Endpoint.Include ? lower : domain.next(lower).get();
        this.end = upper.isPresent() && right == Endpoint.Include ? domain.next(upper.get()) : upper;
    }

    @Override
    public boolean contains(T element) {
        return domain.compare(element, begin) >= 0
                && JustBeforeNothing.compare(domain, Optional.of(element), end) < 0;
    }

    @Override
    public T begin() {
        return begin;
    }

    @Override
    public Optional<T> end() {
        return end;
    }

    @Override
    public int compareTo(Range<T, D> other) {
        Ensure.precondition(other != null, "comparing (compareTo) a DenseRange<T> with null");
        return new RangeComparator<T, D>(domain).compare(this, other);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof DenseRange == false) {
            return false;
        }
        final DenseRange<T, D> other = (DenseRange<T, D>) rhs;
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (this.isEmpty() && other.isEmpty()) {
            return true;
        }
        return Objects.equals(this.begin, other.begin) && Objects.equals(this.end, other.end);
    }

    @Override
    public int hashCode() {
        return isEmpty() ? Objects.hash(domain) : Objects.hash(domain, begin, end);
    }

    @Override
    public String toString() {
        return String.format("[%s-%s)", begin, end.isPresent() ? end.get() : "...");
    }

    /**
     * Apples to apples (dense to dense) : yields false if this.lower >
     * other.upper or other.lower > this.upper Apples to oranges: (dense to
     * nonDense) yields nonDense.overlaps(dense)
     *
     * @param other
     * @return TODO
     */
    @Override
    public boolean overlaps(Range<T, D> other) {
        Ensure.precondition(other != null, "checking for overlaps between a DenseRange<T> and null");
        if (other instanceof DenseRange == false) {
            return other.overlaps(this);
        }
        return JustBeforeNothing.compare(domain, Optional.of(this.begin()), other.end()) < 0
                && JustBeforeNothing.compare(domain, Optional.of(other.begin()), this.end()) < 0;
    }

    @Override
    public List<DenseRange<T, D>> densified() {
        return Collections.singletonList(this);
    }

    @Override
    public Iterator<T> iterator() {
        return new RangeIterator<>(domain, begin, end);
    }

    @Override
    public Spliterator<T> spliterator() {
        return new DenseRangeSpliterator<>(domain, begin, end);
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    @Override
    public D size() {
        return domain.distance(begin, end);
    }

    @Override
    public boolean isEmpty() {
        return domain.distanceToLong(size()) == 0;
    }

}
