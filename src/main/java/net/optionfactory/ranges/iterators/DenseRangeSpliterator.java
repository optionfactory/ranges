package net.optionfactory.ranges.iterators;

import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import net.optionfactory.ranges.Bound;
import net.optionfactory.ranges.DiscreteDomain;
import net.optionfactory.ranges.ops.BoundComparator;
import net.optionfactory.ranges.ops.Ensure;

public class DenseRangeSpliterator<T, D> implements Spliterator<T> {

    private final DiscreteDomain<T, D> domain;
    private final BoundComparator<T> cmp;
    private Bound<T> current;
    private final Bound<T> end;

    public DenseRangeSpliterator(DiscreteDomain<T, D> domain, Bound<T> begin, Bound<T> end) {
        Ensure.precondition(!(begin instanceof Bound.NegativeInfinity), "Cannot iterate a range with an unbounded lower limit");
        this.domain = domain;
        this.cmp = new BoundComparator<>(domain);
        this.current = begin;
        this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (cmp.compare(current, end) >= 0) {
            return false;
        }
        
        T yielded = ((Bound.Finite<T>) current).value();
        action.accept(yielded);
        
        current = domain.next(yielded)
                .map(v -> (Bound<T>) Bound.finite(v))
                .orElseGet(Bound::posInf);
        
        return true;
    }

    private Optional<T> endAsOptional() {
        if (end instanceof Bound.Finite<T> f) {
            return Optional.of(f.value());
        }
        return Optional.empty(); 
    }

    @Override
    public Spliterator<T> trySplit() {
        if (cmp.compare(current, end) >= 0) {
            return null;
        }
        
        T currentVal = ((Bound.Finite<T>) current).value();
        final long size = domain.distanceToLong(domain.distance(currentVal, endAsOptional()));
        
        if (size < 10) {
            return null;
        }
        final T middle = domain.mid(currentVal, size / 2);
        final Bound<T> prefixStart = current;
        current = Bound.finite(middle);
        
        return new DenseRangeSpliterator<>(domain, prefixStart, current);
    }

    @Override
    public long estimateSize() {
        if (cmp.compare(current, end) >= 0) {
            return 0;
        }
        if (end instanceof Bound.PositiveInfinity) {
            return Long.MAX_VALUE;
        }
        return domain.distanceToLong(domain.distance(((Bound.Finite<T>) current).value(), endAsOptional()));
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