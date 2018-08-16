package net.optionfactory.ranges;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class SparseRangeTest {

    @Test
    public void canDetectNonOverlappingSparseRangesNested() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(4, 5));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(2, 2), RangeMother.p(3, 3));
        Assert.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectNonOverlappingSparseRangesWhenLowerOrderIsLhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(1, 1), RangeMother.p(2, 2));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(3, 3), RangeMother.p(4, 4));
        Assert.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectNonOverlappingSparseRangesWhenLowerOrderIsRhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(3, 3), RangeMother.p(4, 4));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(1, 1), RangeMother.p(2, 2));
        Assert.assertFalse(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectOverlappingSparseRangesWhenLhsContainsRhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(1, 10), RangeMother.p(11, 11));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(2, 3), RangeMother.p(5, 6));
        Assert.assertTrue(lhs.overlaps(rhs));
    }

    @Test
    public void canDetectOverlappingSparseRangesWhenRhsContainsLhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(2, 3), RangeMother.p(5, 6));
        Range<Integer, Long> rhs = RangeMother.r(RangeMother.p(1, 10), RangeMother.p(11, 11));
        Assert.assertTrue(lhs.overlaps(rhs));
    }

    @Test
    public void rangeIsNotEqualsToNull() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assert.assertFalse(sr.equals(null));
    }

    @Test
    public void rangeIsEqualsToItSelf() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assert.assertTrue(sr.equals(sr));
    }

    @Test
    public void sameRangesLeadsToSameHashCode() {
        SparseRange<Integer, Long> former = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(3, 5));
        SparseRange<Integer, Long> latter = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(3, 5));
        Assert.assertEquals(former.hashCode(), latter.hashCode());
    }

    @Test
    public void containsElementIfAnyRangeContainsIt() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assert.assertTrue(sr.contains(4));
    }

    @Test
    public void canIterateSparseRange() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 2), RangeMother.p(3, 5));
        Assert.assertNotNull(sr.iterator());
    }

    @Test
    public void canCreateSparseRangeWithUnsortedDenseRanges() {
        SparseRange<Integer, Long> sr = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(2, 3), RangeMother.r(0, 1)));
    }

    @Test
    public void sameDensifiedRangeHaveSameOrder() {
        SparseRange<Integer, Long> former = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 1), RangeMother.r(1, 2), RangeMother.r(4, 5)));
        SparseRange<Integer, Long> latter = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 2), RangeMother.r(4, 5)));
        Assert.assertEquals(0, former.compareTo(latter));
    }

    @Test
    public void lowestRangeLowerBoundIsLowerBound() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 1), RangeMother.r(2, 3)));
        Assert.assertEquals(Integer.valueOf(0), range.begin());
    }

    @Test
    public void upperRangeAfterLastIsAfterLast() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 1), RangeMother.r(2, 3)));
        Assert.assertEquals(Optional.of(Integer.valueOf(4)), range.end());
    }

    @Test
    public void canDensifySparseRange() {
        SparseRange<Integer, Long> range = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 2), RangeMother.r(4, 5)));
        Assert.assertEquals(Arrays.asList(RangeMother.r(0, 2), RangeMother.r(4, 5)), range.densified());
    }

    @Test
    public void toStringShowsDensifiedRanges() {
        SparseRange<Integer, Long> sr = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(0, 2), RangeMother.r(4, 5), RangeMother.r(6, 7)));
        Assert.assertEquals("[[0-3),[4-6),[6-8)]", sr.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeWithNullSequencerYieldsException() {
        new SparseRange<>(null, Arrays.asList(RangeMother.r(0, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeWithNullRangeYieldsException() {
        new SparseRange<>(RangeMother.domain, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeWithEmptyRangeYieldsException() {
        new SparseRange<>(RangeMother.domain, Arrays.<DenseRange<Integer, Long>>asList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeFromListWithNullSequencerYieldsException() {
        new SparseRange<>(null, RangeMother.r(0, 1).densified());
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeFromListWithNullRangeYieldsException() {
        new SparseRange<>(RangeMother.domain, (List<DenseRange<Integer, Long>>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingSparseRangeFromListWithEmptyRangeYieldsException() {
        new SparseRange<>(RangeMother.domain, Collections.<DenseRange<Integer, Long>>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkingOverlapsAgainstNullYieldsException() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 10), RangeMother.p(11, 20));
        sr.overlaps(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void comparingAgainstNullYieldsException() {
        SparseRange<Integer, Long> sr = RangeMother.r(RangeMother.p(0, 10), RangeMother.p(11, 20));
        sr.compareTo(null);
    }


}
