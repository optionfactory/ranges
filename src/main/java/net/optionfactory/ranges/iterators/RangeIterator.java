package net.optionfactory.ranges.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.optionfactory.ranges.Bound;
import net.optionfactory.ranges.DiscreteDomain;
import net.optionfactory.ranges.ops.BoundComparator;
import net.optionfactory.ranges.ops.Ensure;

public class RangeIterator<T, D> implements Iterator<T> {

    private final DiscreteDomain<T, D> domain;
    private final BoundComparator<T> cmp;
    private Bound<T> current;
    private final Bound<T> end;

    public RangeIterator(DiscreteDomain<T, D> domain, Bound<T> begin, Bound<T> end) {
        Ensure.precondition(!(begin instanceof Bound.NegativeInfinity), "Cannot iterate a range with an unbounded lower limit");

        this.domain = domain;
        this.cmp = new BoundComparator<>(domain);
        this.current = begin;
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return cmp.compare(current, end) < 0;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final T yielded = ((Bound.Finite<T>) current).value();
        this.current = domain.next(yielded)
                .map(v -> (Bound<T>) Bound.finite(v))
                .orElseGet(Bound::posInf);

        return yielded;
    }
}
