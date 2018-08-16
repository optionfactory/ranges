package net.optionfactory.ranges;

import java.util.Arrays;
import java.util.Optional;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.examples.IntegerDomain;
import net.optionfactory.ranges.ops.RangeOps;
import org.junit.Assert;
import org.junit.Test;

public class DenseRangeTest {

    @Test
    public void emptyRangeHasZeroSize(){
        Assert.assertEquals(Long.valueOf(0), RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude).size());
    }
    
    @Test
    public void emptyRangeIsEmpty(){
        Assert.assertEquals(true, RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude).isEmpty());
    }
    
    @Test
    public void sameRangeHasSameHashcode() {
        Assert.assertEquals(RangeMother.r(0, 10).hashCode(), RangeMother.r(0, 10).hashCode());
    }

    @Test
    public void emptyRangesHaveSameHashcode() {
        final DenseRange<Integer, Long> anEmptyRange = new DenseRange<>(new IntegerDomain(), Endpoint.Include, 10, Optional.of(10), Endpoint.Exclude);
        final DenseRange<Integer, Long> anotherEmptyRange = new DenseRange<>(new IntegerDomain(), Endpoint.Include, 0, Optional.of(0), Endpoint.Exclude);
        Assert.assertFalse(anEmptyRange.iterator().hasNext());
        Assert.assertEquals(anEmptyRange.hashCode(), anotherEmptyRange.hashCode());
    }

    @Test
    public void emptyRangesAreEquals() {
        final DenseRange<Integer, Long> anEmptyRange = new DenseRange<>(new IntegerDomain(), Endpoint.Include, 10, Optional.of(10), Endpoint.Exclude);
        final DenseRange<Integer, Long> anotherEmptyRange = new DenseRange<>(new IntegerDomain(), Endpoint.Include, 0, Optional.of(0), Endpoint.Exclude);
        Assert.assertEquals(anEmptyRange, anotherEmptyRange);
    }

    @Test
    public void toStringReflectsRange() {
        Assert.assertEquals("[0-11)", RangeMother.r(0, 10).toString());
    }

    @Test
    public void canToStringOnUnboundedRange() {
        final DenseRange<Integer, Long> unboundRange = new DenseRange<>(new IntegerDomain(), Endpoint.Include, 0, Optional.<Integer>empty(), Endpoint.Exclude);
        Assert.assertEquals("[0-...)", unboundRange.toString());
    }

    @Test
    public void rangeIsNotEqualToNull() {
        Assert.assertFalse(RangeMother.r(0, 10).equals(null));
    }

    @Test
    public void rangeIsEqualToItself() {
        DenseRange<Integer, Long> range = RangeMother.r(0, 1);
        Assert.assertTrue(range.equals(range));
    }

    @Test
    public void rangeIsEqualToRangeWithDifferentBounds() {
        DenseRange<Integer, Long> former = RangeMother.r(0, 1);
        DenseRange<Integer, Long> latter = RangeMother.r(0, 2);
        Assert.assertFalse(former.equals(latter));
    }

    @Test
    public void rangeIsEqualToEquivalentRangeWithDifferentRightEndpoint() {
        DenseRange<Integer, Long> former = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Include);
        DenseRange<Integer, Long> latter = RangeMother.r(Endpoint.Include, 1, 3, Endpoint.Exclude);
        Assert.assertTrue(former.equals(latter));
    }

    @Test
    public void rangeIsEqualToEquivalentRangeWithDifferentLeftEndpoint() {
        DenseRange<Integer, Long> former = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Include);
        DenseRange<Integer, Long> latter = RangeMother.r(Endpoint.Exclude, 0, 2, Endpoint.Include);
        Assert.assertTrue(former.equals(latter));
    }

    @Test
    public void rangesWithSameBoundsHaveSameOrder() {
        DenseRange<Integer, Long> former = RangeMother.r(0, 10);
        DenseRange<Integer, Long> latter = RangeMother.r(0, 10);
        Assert.assertEquals(0, former.compareTo(latter));
    }

    @Test
    public void densifiedDenseRangeIsEqualsToItselfInList() {
        DenseRange<Integer, Long> range = RangeMother.r(0, 10);
        Assert.assertEquals(Arrays.asList(range), range.densified());
    }

    @Test
    public void elementInRangeIsContained() {
        DenseRange<Integer, Long> range = RangeMother.r(0, 2);
        Assert.assertTrue(range.contains(1));
    }

    @Test
    public void elementPriorToLowerBoundIsNotContained() {
        DenseRange<Integer, Long> range = RangeMother.r(1, 2);
        Assert.assertFalse(range.contains(0));
    }

    @Test
    public void elementAfterUpperBoundIsNotContained() {
        DenseRange<Integer, Long> range = RangeMother.r(1, 2);
        Assert.assertFalse(range.contains(3));
    }

    @Test
    public void upperBoundIsNotContainedIfRightOpen() {
        DenseRange<Integer, Long> range = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Exclude);
        Assert.assertFalse(range.contains(2));
    }

    @Test
    public void canPerformUnion() {
        Assert.assertEquals(RangeMother.r(0, 20), new RangeOps<>(RangeMother.domain, 0).union(RangeMother.r(0, 10), RangeMother.r(11, 20)));
    }

    @Test
    public void canDetectNonOverlappingRanges() {
        Assert.assertFalse(RangeMother.r(0, 10).overlaps(RangeMother.r(13, 15)));
    }

    @Test
    public void canDetectNonOverlappingRangesInverted() {
        Assert.assertFalse(RangeMother.r(13, 15).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void canDetectOverlappingRanges() {
        Assert.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(5, 15)));
    }

    @Test
    public void canDetectOverlappingRangesInverted() {
        Assert.assertTrue(RangeMother.r(5, 15).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void canDetectOverlapping() {
        Assert.assertTrue(RangeMother.r(10, 15).overlaps(RangeMother.r(0, 11)));
    }

    @Test
    public void canDetectOverlappingInverted() {
        Assert.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(10, 15)));
    }

    @Test
    public void sameRangeOverlaps() {
        Assert.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void lowerBoundIsIncludedInRange() {
        Assert.assertTrue(RangeMother.r(0, 10).contains(0));
    }

    @Test
    public void upperBoundIsIncludedInRange() {
        Assert.assertTrue(RangeMother.r(0, 10).contains(10));
    }

    @Test
    public void canIterateDegenerateRange() {
        Assert.assertEquals(new Integer(0), RangeMother.r(0, 0).iterator().next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingDenseRangeWithNullSequencerYieldsException() {
        new DenseRange<>(null, Endpoint.Include, 0, Optional.of(1), Endpoint.Include);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingDenseRangeWithNullLowerBoundYieldsException() {
        new DenseRange<>(RangeMother.domain, Endpoint.Include, null, Optional.of(1), Endpoint.Include);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingDenseRangeWithNullUpperBoundYieldsException() {
        new DenseRange<>(RangeMother.domain, Endpoint.Include, 0, null, Endpoint.Include);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingDenseRangeWithUpperBoundLesserThenLowerBoundYieldsException() {
        new DenseRange<>(RangeMother.domain, Endpoint.Include, 10, Optional.of(0), Endpoint.Include);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingWithNothingAsUpperValueAndIncludedUpperEndpointYieldsException() {
        new DenseRange<>(RangeMother.domain, Endpoint.Include, 10, Optional.<Integer>empty(), Endpoint.Include);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkingForOverlapsWithNullYieldsException() {
        RangeMother.r(0, 10).overlaps(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void comparingWithNullYieldsException() {
        RangeMother.r(0, 10).compareTo(null);
    }

}
