package net.optionfactory.ranges;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.iterators.SparseRangeSpliterator;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.RangeComparator;

/**
 *
 * @param <T>
 * @author rferranti
 */
public class SparseRange<T, D> implements Range<T, D> {

    private final DiscreteDomain<T, D> domain;
    private final List<DenseRange<T, D>> densified;

    public SparseRange(DiscreteDomain<T, D> domain, List<DenseRange<T, D>> densified) {
        Ensure.precondition(domain != null, "trying to create a SparseRange<T> with a null SequencingPolicy<T>");
        Ensure.precondition(densified != null, "trying to create a SparseRange<T> from a null ranges");
        Ensure.precondition(!densified.isEmpty(), "trying to create a SparseRange<T> from zero non-empty ranges");
        Ensure.precondition(densified.size() > 1, "trying to create a SparseRange<T> when a DenseRange<T> should be created");
        // We are not checking isSorted(densified) && !any(densified, isOverlapping) as it's definitely not cheap.
        // Using RangeOps.canonicalize (as any client code should not needing to mess with internals should) enforces SparseRange is 
        // constructed as it should.
        this.domain = domain;
        this.densified = densified;
    }

    @Override
    public boolean contains(final T element) {
        return densified.stream().anyMatch(dr -> dr.contains(element));
    }

    @Override
    public T begin() {
        return densified.get(0).begin();
    }

    @Override
    public Optional<T> end() {
        return densified.get(densified.size() - 1).end();
    }

    @Override
    public int compareTo(Range<T, D> other) {
        Ensure.precondition(other != null, "Comparing (compareTo) a SparseRange<T>(%s) with null");
        return new RangeComparator<T, D>(domain).compare(this, other);
    }

    @Override
    public Iterator<T> iterator() {
        return densified.stream().flatMap(dr -> dr.stream()).iterator();
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof SparseRange == false) {
            return false;
        }
        final SparseRange<T, D> other = (SparseRange<T, D>) rhs;
        return this.densified.equals(other.densified);
    }

    @Override
    public int hashCode() {
        return densified.hashCode();
    }

    @Override
    public String toString() {
        final String interposed = densified.stream().map(DenseRange::toString).collect(Collectors.joining(","));
        return String.format("[%s]", interposed);
    }

    @Override
    public boolean overlaps(final Range<T, D> other) {
        Ensure.precondition(other != null, "checking for overlaps between a SparseRange<T> and null");
        return densified.stream().anyMatch(other::overlaps);
    }

    @Override
    public List<DenseRange<T, D>> densified() {
        return densified;
    }

    @Override
    public Spliterator<T> spliterator() {
        final LinkedList<Spliterator<T>> spliterators = densified.stream().map(DenseRange::spliterator).collect(Collectors.toCollection(() -> new LinkedList<>()));
        return new SparseRangeSpliterator<>(domain, spliterators);
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
        return densified.stream().map(d -> d.size()).reduce(domain::sumDistances).get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
