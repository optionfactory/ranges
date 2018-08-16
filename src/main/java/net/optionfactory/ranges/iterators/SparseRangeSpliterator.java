package net.optionfactory.ranges.iterators;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;
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
        for (final Iterator<Spliterator<T>> spliteratorIter = spliterators.iterator(); spliteratorIter.hasNext(); spliteratorIter.remove()) {
            final Spliterator<T> spliterator = spliteratorIter.next();
            if (spliterator.tryAdvance(action)) {
                return true;
            }
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

        final int middle = spliterators.size() / 2;

        final List<Spliterator<T>> prefix = spliterators.subList(0, middle);
        final LinkedList<Spliterator<T>> prefixCopy = new LinkedList<>(prefix);
        
        prefix.clear();

        return prefixCopy.size() > 1
                ? new SparseRangeSpliterator<>(domain, prefixCopy)
                : prefixCopy.getFirst();
    }

    @Override
    public long estimateSize() {
        return spliterators
                .stream()
                .map(Spliterator::estimateSize)
                .map(BigInteger::valueOf)
                .reduce(BigInteger::add)
                .get()
                .min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue();
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | CONCURRENT | DISTINCT | IMMUTABLE | ORDERED | SORTED;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return domain;
    }

}
