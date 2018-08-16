package net.optionfactory.ranges.iterators;

import java.util.Iterator;
import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;
import net.optionfactory.ranges.ops.JustBeforeNothing;

public class RangeIterator<T, D> implements Iterator<T> {

    private final DiscreteDomain<T, D> domain;
    private T current;
    private final Optional<T> end;

    public RangeIterator(DiscreteDomain<T, D> domain, T begin, Optional<T> end) {
        this.domain = domain;
        this.current = begin;
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return JustBeforeNothing.compare(domain, Optional.of(current), end) < 0;
    }

    @Override
    public T next() {
        final T oldCurrent = current;
        current = domain.next(current).get();
        return oldCurrent;
    }

}
