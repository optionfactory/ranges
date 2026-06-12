package net.optionfactory.ranges.ops;

import java.util.Comparator;
import net.optionfactory.ranges.Bound;

public class BoundComparator<T> implements Comparator<Bound<T>> {

    private final Comparator<T> domainComparator;

    public BoundComparator(Comparator<T> domainComparator) {
        this.domainComparator = domainComparator;
    }

    @Override
    public int compare(Bound<T> a, Bound<T> b) {
        if (a.equals(b)) {
            return 0;
        }
        if (a instanceof Bound.NegativeInfinity) {
            return -1;
        }
        if (b instanceof Bound.NegativeInfinity) {
            return 1;
        }

        if (a instanceof Bound.PositiveInfinity) {
            return 1;
        }
        if (b instanceof Bound.PositiveInfinity) {
            return -1;
        }

        return domainComparator.compare(((Bound.Finite<T>) a).value(), ((Bound.Finite<T>) b).value());
    }
}
