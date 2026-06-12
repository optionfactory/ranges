package net.optionfactory.ranges.ops;

import java.util.Comparator;
import net.optionfactory.ranges.Range;

/**
 * empty, then smallest lower bounds with greatest upper bounds ranges come
 * first
 */
public class RangeComparator<T, D> implements Comparator<Range<T, D>> {

    private final Comparator<T> domainComparator;
    private final BoundComparator<T> boundCmp;

    public RangeComparator(Comparator<T> domainComparator) {
        this.domainComparator = domainComparator;
        this.boundCmp = new BoundComparator<>(domainComparator);
    }

    @Override
    public int compare(Range<T, D> lhs, Range<T, D> rhs) {
        final int emptinessOrder = Boolean.compare(rhs.isEmpty(), lhs.isEmpty());
        if (emptinessOrder != 0) {
            return emptinessOrder;
        }

        if (lhs.isEmpty()) {
            return 0;
        }

        final int beginOrder = boundCmp.compare(lhs.begin(), rhs.begin());
        if (beginOrder != 0) {
            return beginOrder;
        }
        return boundCmp.compare(rhs.end(), lhs.end());
    }

    @Override
    public boolean equals(Object rhs) {
        if (!(rhs instanceof RangeComparator)) {
            return false;
        }
        final RangeComparator<?, ?> other = (RangeComparator<?, ?>) rhs;
        return this.domainComparator.equals(other.domainComparator);
    }

    @Override
    public int hashCode() {
        return domainComparator.hashCode();
    }
}
