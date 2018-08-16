package net.optionfactory.ranges.iterators;

import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import net.optionfactory.ranges.DiscreteDomain;

public class DenseRangeSpliterator<T, D> implements Spliterator<T> {

    private final DiscreteDomain<T,D> domain;
    private Optional<T> current;
    private final Optional<T> end;

    public DenseRangeSpliterator(DiscreteDomain<T,D> domain, T begin, Optional<T> end) {
        this.domain = domain;
        this.current = Optional.of(begin);
        this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if(!current.isPresent() || current.equals(end)){
            return false;
        }
        action.accept(current.get());
        current = domain.next(current.get());
        return true;
    }

    @Override
    public Spliterator<T> trySplit() {
        if(!current.isPresent()){
            return null;
        }
        final long size = domain.distanceToLong(domain.distance(current.get(), end));
        if (size < 10) {
            return null;
        }
        final T middle = domain.mid(current.get(), end);
        final T prefixStart = current.get();
        current = Optional.of(middle);
        return new DenseRangeSpliterator<>(domain, prefixStart, Optional.of(middle));
    }

    @Override
    public long estimateSize() {
        if(!current.isPresent()){
            return 0;
        }
        return domain.distanceToLong(domain.distance(current.get(), end));
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
