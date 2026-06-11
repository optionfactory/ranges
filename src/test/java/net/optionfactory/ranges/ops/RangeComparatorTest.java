package net.optionfactory.ranges.ops;

import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.RangeMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangeComparatorTest {

    @Test
    public void lowestRangesComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(0, 2);
        Range<Integer, Long> greater = RangeMother.r(3, 5);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowestRangesComesFirstWithNegativeRanges() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(-5, -3);
        Range<Integer, Long> greater = RangeMother.r(-2, -1);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowerBoundsHasPriorityWhileDecidingOrder() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(0, 2);
        Range<Integer, Long> greater = RangeMother.r(1, 2);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowerBoundsHasPriorityWhileDecidingOrderWithNegativeRanges() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(-5, -3);
        Range<Integer, Long> greater = RangeMother.r(-4, -3);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void withRangesWithSameLowerBoundNarrowerComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(0, 4);
        Range<Integer, Long> greater = RangeMother.r(0, 3);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void forNegativeRangesWithSameLowerBoundNarrowerComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        Range<Integer, Long> lesser = RangeMother.r(-5, 0);
        Range<Integer, Long> greater = RangeMother.r(-5, -3);
        Assertions.assertEquals(-1, comparator.compare(lesser, greater));
    }
}