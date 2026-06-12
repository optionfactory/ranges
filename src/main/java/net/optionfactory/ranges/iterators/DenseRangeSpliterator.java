package net.optionfactory.ranges.iterators;

import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import net.optionfactory.ranges.Bound;
import net.optionfactory.ranges.DiscreteDomain;
import net.optionfactory.ranges.ops.Ensure;

public class DenseRangeSpliterator<T, D> implements Spliterator<T> {

    private final DiscreteDomain<T, D> domain;
    private final Bound<T> end;
    private final Optional<T> endOpt;

    private T currentVal;
    private boolean exhausted;

    public DenseRangeSpliterator(DiscreteDomain<T, D> domain, Bound<T> begin, Bound<T> end) {
        Ensure.precondition(!(begin instanceof Bound.NegativeInfinity), "Cannot iterate a range with an unbounded lower limit");
        this.domain = domain;
        this.end = end;
        this.endOpt = (end instanceof Bound.Finite<T> f) ? Optional.of(f.value()) : Optional.empty();

        this.currentVal = ((Bound.Finite<T>) begin).value();
        this.exhausted = false;
    }

    private boolean isPastEnd(T val) {
        if (end instanceof Bound.PositiveInfinity) {
            return false;
        }
        return domain.compare(val, ((Bound.Finite<T>) end).value()) >= 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (exhausted || isPastEnd(currentVal)) {
            return false;
        }
        T yielded = currentVal;
        action.accept(yielded);
        Optional<T> nextOpt = domain.next(yielded);
        if (nextOpt.isPresent()) {
            currentVal = nextOpt.get();
        } else {
            exhausted = true;
        }
        return true;
    }

    @Override
    public Spliterator<T> trySplit() {
        if (exhausted || isPastEnd(currentVal)) {
            return null;
        }
        final long size = domain.distanceToLong(domain.distance(currentVal, endOpt));
        if (size < 10) {
            return null;
        }
        final T middle = domain.mid(currentVal, size / 2);
        final Bound<T> prefixStart = Bound.finite(currentVal);
        final Bound<T> prefixEnd = Bound.finite(middle);
        currentVal = middle;
        return new DenseRangeSpliterator<>(domain, prefixStart, prefixEnd);
    }

    @Override
    public long estimateSize() {
        if (exhausted || isPastEnd(currentVal)) {
            return 0;
        }
        if (end instanceof Bound.PositiveInfinity) {
            return Long.MAX_VALUE;
        }
        return domain.distanceToLong(domain.distance(currentVal, endOpt));
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
