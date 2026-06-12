package net.optionfactory.ranges.iterators;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import net.optionfactory.ranges.DiscreteDomain;

public class SparseRangeSpliterator<T, D> implements Spliterator<T> {

    private final DiscreteDomain<T, D> domain;
    private final LinkedList<Spliterator<T>> spliterators;

    public SparseRangeSpliterator(DiscreteDomain<T, D> domain, LinkedList<Spliterator<T>> spliterators) {
        this.domain = domain;
        this.spliterators = spliterators;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        while (!spliterators.isEmpty()) {
            if (spliterators.getFirst().tryAdvance(action)) {
                return true;
            }
            spliterators.removeFirst();
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        final int size = spliterators.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return spliterators.getFirst().trySplit();
        }
        final int middle = size / 2;
        final List<Spliterator<T>> prefix = spliterators.subList(0, middle);
        final LinkedList<Spliterator<T>> prefixCopy = new LinkedList<>(prefix);
        
        prefix.clear();

        return prefixCopy.size() > 1
                ? new SparseRangeSpliterator<>(domain, prefixCopy)
                : prefixCopy.getFirst();
    }

    @Override
    public long estimateSize() {
        long total = 0;
        for (Spliterator<T> spliterator : spliterators) {
            long size = spliterator.estimateSize();
            if (size == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            total += size;            
            if (total < 0) {
                return Long.MAX_VALUE;
            }
        }
        return total;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE | NONNULL | ORDERED | SORTED | DISTINCT | SIZED | SUBSIZED;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return domain;
    }
}