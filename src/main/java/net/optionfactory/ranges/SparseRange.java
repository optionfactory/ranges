package net.optionfactory.ranges;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.optionfactory.ranges.iterators.SparseRangeSpliterator;
import net.optionfactory.ranges.ops.Ensure;
import net.optionfactory.ranges.ops.RangeComparator;

public record SparseRange<T, D>(DiscreteDomain<T, D> domain, List<DenseRange<T, D>> densified) implements Range<T, D> {

    public SparseRange {
        Ensure.precondition(domain != null, "trying to create a SparseRange<T> with a null SequencingPolicy<T>");
        Ensure.precondition(densified != null, "trying to create a SparseRange<T> from null ranges");
        Ensure.precondition(!densified.isEmpty(), "trying to create a SparseRange<T> from zero non-empty ranges");
        Ensure.precondition(densified.size() > 1, "trying to create a SparseRange<T> when a DenseRange<T> should be created");

        densified = Collections.unmodifiableList(densified);
    }

    @Override
    public boolean contains(final T element) {
        return densified.stream().anyMatch(dr -> dr.contains(element));
    }

    @Override
    public Bound<T> begin() {
        return densified.get(0).begin();
    }

    @Override
    public Bound<T> end() {
        return densified.get(densified.size() - 1).end();
    }

    @Override
    public int compareTo(Range<T, D> other) {
        Ensure.precondition(other != null, "Comparing (compareTo) a SparseRange<T>(%s) with null");
        return RangeComparator.compare(domain, this, other);
    }

    @Override
    public Iterator<T> iterator() {
        return densified.stream().flatMap(DenseRange::stream).iterator();
    }

    @Override
    public boolean overlaps(final Range<T, D> other) {
        Ensure.precondition(other != null, "checking for overlaps between a SparseRange<T> and null");
        return densified.stream().anyMatch(other::overlaps);
    }

    @Override
    public Spliterator<T> spliterator() {
        return new SparseRangeSpliterator<>(
                domain,
                densified.stream().map(DenseRange::spliterator).collect(Collectors.toCollection(java.util.LinkedList::new))
        );
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
        return densified.stream()
                .map(DenseRange::size)
                .reduce(domain::sumDistances)
                .orElse(domain.zero());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        final String interposed = densified.stream().map(DenseRange::toString).collect(Collectors.joining(","));
        return String.format("[%s]", interposed);
    }
}