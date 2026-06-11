package net.optionfactory.ranges;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SparseRangeTest {

    @Test
    public void canDetectNonOverlappingSparseRangesNested() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(4, 5));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(2, 2), RangeMother.p(3, 3));
        Assertions.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectNonOverlappingSparseRangesWhenLowerOrderIsLhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(1, 1), RangeMother.p(2, 2));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(3, 3), RangeMother.p(4, 4));
        Assertions.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectNonOverlappingSparseRangesWhenLowerOrderIsRhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(3, 3), RangeMother.p(4, 4));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(1, 1), RangeMother.p(2, 2));
        Assertions.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectOverlappingSparseRangesWhenLhsContainsRhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(1, 10), RangeMother.p(11, 11));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(2, 3), RangeMother.p(5, 6));
        Assertions.assertTrue(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectOverlappingSparseRangesWhenRhsContainsLhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(2, 3), RangeMother.p(5, 6));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(1, 10), RangeMother.p(11, 11));
        Assertions.assertTrue(lhs.overlaps(rhs));
    }

    @Test
    public void rangeIsNotEqualsToNull() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assertions.assertFalse(sr.equals(null));
    }

    @Test
    public void rangeIsEqualsToItSelf() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assertions.assertTrue(sr.equals(sr));
    }

    @Test
    public void sameRangesLeadsToSameHashCode() {
        SparseRange<Integer, Long> former = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(3, 5));
        SparseRange<Integer, Long> latter = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(3, 5));
        Assertions.assertEquals(former.hashCode(), latter.hashCode());
    }

    @Test
    public void containsElementIfAnyRangeContainsIt() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assertions.assertTrue(sr.contains(4));
    }

    @Test
    public void canIterateSparseRange() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assertions.assertNotNull(sr.iterator());
    }

    @Test
    public void canCreateSparseRangeWithUnsortedDenseRanges() {
        SparseRange<Integer, Long> sr = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(2, 3), 
                (DenseRange<Integer, Long>) RangeMother.r(0, 1)
        ));
        Assertions.assertNotNull(sr);
    }

    @Test
    public void sameDensifiedRangeHaveSameOrder() {
        SparseRange<Integer, Long> former = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 1), 
                (DenseRange<Integer, Long>) RangeMother.r(2, 3), 
                (DenseRange<Integer, Long>) RangeMother.r(4, 5)
        ));
        SparseRange<Integer, Long> latter = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 1), 
                (DenseRange<Integer, Long>) RangeMother.r(2, 3), 
                (DenseRange<Integer, Long>) RangeMother.r(4, 5)
        ));
        Assertions.assertEquals(0, former.compareTo(latter));
    }

    @Test
    public void lowestRangeLowerBoundIsLowerBound() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 1), 
                (DenseRange<Integer, Long>) RangeMother.r(2, 3)
        ));
        Assertions.assertEquals(Integer.valueOf(0), range.begin());
    }

    @Test
    public void upperRangeAfterLastIsAfterLast() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 1), 
                (DenseRange<Integer, Long>) RangeMother.r(2, 3)
        ));
        // The factory transforms closed bounds [2, 3] into the interval [2, 4).
        Assertions.assertEquals(Optional.of(Integer.valueOf(4)), range.end());
    }

    @Test
    public void canDensifySparseRange() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 2), 
                (DenseRange<Integer, Long>) RangeMother.r(4, 5)
        ));
        Assertions.assertEquals(Arrays.asList(RangeMother.r(0, 2), RangeMother.r(4, 5)), range.densified());
    }

    @Test
    public void toStringShowsDensifiedRanges() {
        SparseRange<Integer, Long> sr = new SparseRange<>(RangeMother.domain, Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.r(0, 2), 
                (DenseRange<Integer, Long>) RangeMother.r(4, 5), 
                (DenseRange<Integer, Long>) RangeMother.r(6, 7)
        ));
        Assertions.assertEquals("[[0-3),[4-6),[6-8)]", sr.toString());
    }

    @Test
    public void creatingSparseRangeWithNullSequencerYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new SparseRange<>(null, Arrays.asList(
                    (DenseRange<Integer, Long>) RangeMother.r(0, 1), 
                    (DenseRange<Integer, Long>) RangeMother.r(3, 4)
            ));
        });
    }

    @Test
    public void creatingSparseRangeWithNullRangeYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new SparseRange<>(RangeMother.domain, null);
        });
    }

    @Test
    public void creatingSparseRangeWithEmptyRangeYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new SparseRange<>(RangeMother.domain, Collections.emptyList());
        });
    }

    @Test
    public void creatingSparseRangeWithSingletonListYieldsException() {
        // Sparse ranges are strictly for 2+ gaps. A singleton list should be a DenseRange.
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new SparseRange<>(RangeMother.domain, Arrays.asList(
                    (DenseRange<Integer, Long>) RangeMother.r(0, 1)
            ));
        });
    }

    @Test
    public void checkingOverlapsAgainstNullYieldsException() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 10), RangeMother.p(11, 20));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sr.overlaps(null);
        });
    }

    @Test
    public void comparingAgainstNullYieldsException() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 10), RangeMother.p(11, 20));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sr.compareTo(null);
        });
    }
}