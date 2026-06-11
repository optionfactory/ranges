package net.optionfactory.ranges.ops;

import java.util.Comparator;
import net.optionfactory.ranges.Range;

/**
 * empty, then smallest lower bounds with greatest upper bounds ranges come first
 *
 * @param <T>
 * @author rferranti
 */
public class RangeComparator<T, D> implements Comparator<Range<T, D>> {

    private final Comparator<T> comparator;

    public RangeComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(Range<T, D> lhs, Range<T, D> rhs) {
        final int emptinessOrder = Boolean.compare(rhs.isEmpty(), lhs.isEmpty());
        if (emptinessOrder != 0) {
            return emptinessOrder;
        }
        final int beginOrder = comparator.compare(lhs.begin(), rhs.begin());
        if (beginOrder != 0) {
            return beginOrder;
        }
        return JustBeforeNothing.compare(comparator, rhs.end(), lhs.end());
    }

    @Override
    public boolean equals(Object rhs) {
        if (!(rhs instanceof RangeComparator)) {
            return false;
        }
        final RangeComparator<?, ?> other = (RangeComparator<?, ?>) rhs;
        return this.comparator.equals(other.comparator);
    }

    @Override
    public int hashCode() {
        return comparator.hashCode();
    }
}