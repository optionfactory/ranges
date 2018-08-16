package net.optionfactory.ranges.ops;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.optionfactory.ranges.DenseRange;
import net.optionfactory.ranges.Range;
import org.junit.Assert;
import org.junit.Test;

public class RangeComparatorTest {

    public static class MockRange implements Range<Integer, Long> {

        private final int lower;
        private final int upper;

        public MockRange(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public Integer begin() {
            return lower;
        }

        @Override
        public Optional<Integer> end() {
            return Optional.of(upper);
        }

        @Override
        public boolean contains(Integer element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean overlaps(Range<Integer, Long> rhs) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator<Integer> iterator() {
            return Arrays.asList(lower, upper).iterator();
        }

        @Override
        public int compareTo(Range<Integer, Long> o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<DenseRange<Integer, Long>> densified() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Stream<Integer> stream() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Stream<Integer> parallelStream() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Long size() {
            return (long)upper - lower;
        }

        @Override
        public boolean isEmpty() {
            return size() > 0;
        }
        
    }

    @Test
    public void lowestRangesComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(0, 2);
        MockRange greater = new MockRange(3, 5);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowestRangesComesFirstWithNegativeRanges() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(-5, -3);
        MockRange greater = new MockRange(-2, -1);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowerBoundsHasPriorityWhileDecidingOrder() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(0, 2);
        MockRange greater = new MockRange(1, 2);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void lowerBoundsHasPriorityWhileDecidingOrderWithNegativeRanges() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(-5, -3);
        MockRange greater = new MockRange(-4, -3);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void withRangesWithSameLowerBoundNarrowerComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(0, 4);
        MockRange greater = new MockRange(0, 3);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }

    @Test
    public void forNegativeRangesWithSameLowerBoundNarrowerComesFirst() {
        RangeComparator<Integer, Long> comparator = new RangeComparator<>(Integer::compareTo);
        MockRange lesser = new MockRange(-5, 0);
        MockRange greater = new MockRange(-5, -3);
        Assert.assertEquals(-1, comparator.compare(lesser, greater));
    }
}
