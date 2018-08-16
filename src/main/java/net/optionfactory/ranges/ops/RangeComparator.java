package net.optionfactory.ranges.ops;

import java.util.Comparator;
import net.optionfactory.ranges.Range;

/**
 * smallest lower bounds with greatest upper bounds ranges come first
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
        final int emptynessOrder = Boolean.compare(lhs.isEmpty(), rhs.isEmpty());
        if (emptynessOrder != 0) {
            return emptynessOrder;
        }
        final int beginOrder = comparator.compare(lhs.begin(), rhs.begin());
        if (beginOrder != 0) {
            return beginOrder;
        }
        return JustBeforeNothing.compare(comparator, rhs.end(), lhs.end());
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof RangeComparator == false) {
            return false;
        }
        final RangeComparator<T, D> other = (RangeComparator<T, D>) rhs;
        return this.comparator.equals(other.comparator);
    }

    @Override
    public int hashCode() {
        return comparator.hashCode();
    }
}
