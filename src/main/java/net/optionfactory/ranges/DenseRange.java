package net.optionfactory.ranges;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.iterators.DenseRangeSpliterator;
import net.optionfactory.ranges.iterators.RangeIterator;
import net.optionfactory.ranges.ops.BoundComparator;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.RangeComparator;

public record DenseRange<T, D>(DiscreteDomain<T, D> domain, Bound<T> begin, Bound<T> end) implements Range<T, D> {

    public DenseRange {
        Ensure.precondition(domain != null, "trying to create a DenseRange<T> with a null DiscreteDomain<T>");
        Ensure.precondition(begin != null, "trying to create a DenseRange<T> with null lower bound wrapper");
        Ensure.precondition(end != null, "trying to create a DenseRange<T> with null upper bound wrapper");
        Ensure.precondition(new BoundComparator<>(domain).compare(begin, end) < 0, "DenseRange size must be > 0");
    }

    @Override
    public boolean contains(T element) {
        if (begin instanceof Bound.Finite<T> f) {
            if (domain.compare(element, f.value()) < 0) {
                return false;
            }
        }
        if (end instanceof Bound.Finite<T> f) {
            if (domain.compare(element, f.value()) >= 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Range<T, D> other) {
        Ensure.precondition(other != null, "comparing (compareTo) a DenseRange<T> with null");
        return new RangeComparator<T, D>(domain).compare(this, other);
    }

    @Override
    public String toString() {
        return String.format("[%s-%s)", begin, end);
    }

    @Override
    public boolean overlaps(Range<T, D> other) {
        Ensure.precondition(other != null, "checking for overlaps between a DenseRange<T> and null");
        if (other instanceof EmptyRange) {
            return false;
        }
        if (!(other instanceof DenseRange)) {
            return other.overlaps(this);
        }

        BoundComparator<T> cmp = new BoundComparator<>(domain);
        return cmp.compare(this.begin, other.end()) < 0
                && cmp.compare(other.begin(), this.end) < 0;
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
        if (begin instanceof Bound.NegativeInfinity) {
            throw new UnsupportedOperationException("Cannot compute size of left-unbounded ranges using the current DiscreteDomain API.");
        }
        T start = ((Bound.Finite<T>) begin).value();
        Optional<T> endOpt = (end instanceof Bound.Finite<T> f)
                ? Optional.of(f.value())
                : Optional.empty();

        return domain.distance(start, endOpt);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
