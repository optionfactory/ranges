package net.optionfactory.ranges;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

public record EmptyRange<T, D>(DiscreteDomain<T, D> domain) implements Range<T, D> {

    @Override
    public boolean contains(T element) {
        return false;
    }

    @Override
    public boolean overlaps(Range<T, D> rhs) {
        return false;
    }

    @Override
    public T begin() {
        throw new IllegalStateException("An empty range does not have bounds.");
    }

    @Override
    public Optional<T> end() {
        return Optional.empty();
    }

    @Override
    public List<DenseRange<T, D>> densified() {
        return Collections.emptyList();
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public Stream<T> parallelStream() {
        return Stream.empty();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public D size() {
        return domain.zero();
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.emptySpliterator();
    }

    @Override
    public int compareTo(Range<T, D> other) {
        return other.isEmpty() ? 0 : -1;
    }

    @Override
    public String toString() {
        return "[]";
    }
}