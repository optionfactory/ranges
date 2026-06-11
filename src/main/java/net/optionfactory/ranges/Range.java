package net.optionfactory.ranges;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public sealed interface Range<T, D> extends Iterable<T>, Comparable<Range<T, D>> permits DenseRange, SparseRange, EmptyRange {

    enum Endpoint {
        Include, Exclude
    }

    boolean contains(T element);

    boolean overlaps(Range<T, D> rhs);

    T begin();

    Optional<T> end();

    List<DenseRange<T, D>> densified();

    Stream<T> stream();

    Stream<T> parallelStream();

    boolean isEmpty();

    D size();
}