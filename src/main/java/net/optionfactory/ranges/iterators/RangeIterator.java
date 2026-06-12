package net.optionfactory.ranges.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import net.optionfactory.ranges.Bound;
import net.optionfactory.ranges.DiscreteDomain;
import net.optionfactory.ranges.ops.Ensure;

public class RangeIterator<T, D> implements Iterator<T> {

    private final DiscreteDomain<T, D> domain;
    private final Bound<T> end;

    private T currentVal;
    private boolean exhausted;

    public RangeIterator(DiscreteDomain<T, D> domain, Bound<T> begin, Bound<T> end) {
        Ensure.precondition(!(begin instanceof Bound.NegativeInfinity), "Cannot iterate a range with an unbounded lower limit");
        this.domain = domain;
        this.end = end;
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
    public boolean hasNext() {
        return !exhausted && !isPastEnd(currentVal);
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final T yielded = currentVal;

        Optional<T> nextOpt = domain.next(yielded);
        if (nextOpt.isPresent()) {
            currentVal = nextOpt.get();
        } else {
            exhausted = true;
        }

        return yielded;
    }
}
