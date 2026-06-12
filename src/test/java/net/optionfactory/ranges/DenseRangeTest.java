package net.optionfactory.ranges;

import java.util.Arrays;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.examples.IntegerDomain;
import net.optionfactory.ranges.ops.RangeOps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DenseRangeTest {

    private final Ranges<Integer, Long> ranges = new Ranges<>(new IntegerDomain());

    @Test
    public void emptyRangeHasZeroSize() {
        Assertions.assertEquals(Long.valueOf(0), ranges.empty().size());
    }

    @Test
    public void emptyRangeIsEmpty() {
        Assertions.assertTrue(ranges.empty().isEmpty());
    }

    @Test
    public void sameRangeHasSameHashcode() {
        Assertions.assertEquals(RangeMother.r(0, 10).hashCode(), RangeMother.r(0, 10).hashCode());
    }

    @Test
    public void emptyRangesHaveSameHashcode() {
        final Range<Integer, Long> anEmptyRange = ranges.closedOpen(10, 10);
        final Range<Integer, Long> anotherEmptyRange = ranges.closedOpen(0, 0);

        Assertions.assertFalse(anEmptyRange.iterator().hasNext());
        Assertions.assertEquals(anEmptyRange.hashCode(), anotherEmptyRange.hashCode());
    }

    @Test
    public void emptyRangesAreEquals() {
        final Range<Integer, Long> anEmptyRange = ranges.closedOpen(10, 10);
        final Range<Integer, Long> anotherEmptyRange = ranges.closedOpen(0, 0);

        Assertions.assertEquals(anEmptyRange, anotherEmptyRange);
    }

    @Test
    public void toStringReflectsRange() {
        Assertions.assertEquals("[0-11)", RangeMother.r(0, 10).toString());
    }

    @Test
    public void canToStringOnUnboundedRange() {
        final DenseRange<Integer, Long> unboundRange = new DenseRange<>(new IntegerDomain(), Bound.finite(0), Bound.positiveInfinity());
        Assertions.assertEquals("[0-+∞)", unboundRange.toString());
    }

    @Test
    public void rangeIsNotEqualToNull() {
        Assertions.assertFalse(RangeMother.r(0, 10).equals(null));
    }

    @Test
    public void rangeIsEqualToItself() {
        Range<Integer, Long> range = RangeMother.r(0, 1);
        Assertions.assertTrue(range.equals(range));
    }

    @Test
    public void rangeIsEqualToRangeWithDifferentBounds() {
        Range<Integer, Long> former = RangeMother.r(0, 1);
        Range<Integer, Long> latter = RangeMother.r(0, 2);
        Assertions.assertFalse(former.equals(latter));
    }

    @Test
    public void rangeIsEqualToEquivalentRangeWithDifferentRightEndpoint() {
        Range<Integer, Long> former = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Include);
        Range<Integer, Long> latter = RangeMother.r(Endpoint.Include, 1, 3, Endpoint.Exclude);
        Assertions.assertTrue(former.equals(latter));
    }

    @Test
    public void rangeIsEqualToEquivalentRangeWithDifferentLeftEndpoint() {
        Range<Integer, Long> former = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Include);
        Range<Integer, Long> latter = RangeMother.r(Endpoint.Exclude, 0, 2, Endpoint.Include);
        Assertions.assertTrue(former.equals(latter));
    }

    @Test
    public void rangesWithSameBoundsHaveSameOrder() {
        Range<Integer, Long> former = RangeMother.r(0, 10);
        Range<Integer, Long> latter = RangeMother.r(0, 10);
        Assertions.assertEquals(0, former.compareTo(latter));
    }

    @Test
    public void densifiedDenseRangeIsEqualsToItselfInList() {
        Range<Integer, Long> range = RangeMother.r(0, 10);
        Assertions.assertEquals(Arrays.asList(range), range.densified());
    }

    @Test
    public void elementInRangeIsContained() {
        Range<Integer, Long> range = RangeMother.r(0, 2);
        Assertions.assertTrue(range.contains(1));
    }

    @Test
    public void elementPriorToLowerBoundIsNotContained() {
        Range<Integer, Long> range = RangeMother.r(1, 2);
        Assertions.assertFalse(range.contains(0));
    }

    @Test
    public void elementAfterUpperBoundIsNotContained() {
        Range<Integer, Long> range = RangeMother.r(1, 2);
        Assertions.assertFalse(range.contains(3));
    }

    @Test
    public void upperBoundIsNotContainedIfRightOpen() {
        Range<Integer, Long> range = RangeMother.r(Endpoint.Include, 1, 2, Endpoint.Exclude);
        Assertions.assertFalse(range.contains(2));
    }

    @Test
    public void canPerformUnion() {
        Assertions.assertEquals(RangeMother.r(0, 20), new RangeOps<>(RangeMother.domain).union(RangeMother.r(0, 10), RangeMother.r(11, 20)));
    }

    @Test
    public void canDetectNonOverlappingRanges() {
        Assertions.assertFalse(RangeMother.r(0, 10).overlaps(RangeMother.r(13, 15)));
    }

    @Test
    public void canDetectNonOverlappingRangesInverted() {
        Assertions.assertFalse(RangeMother.r(13, 15).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void canDetectOverlappingRanges() {
        Assertions.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(5, 15)));
    }

    @Test
    public void canDetectOverlappingRangesInverted() {
        Assertions.assertTrue(RangeMother.r(5, 15).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void canDetectOverlapping() {
        Assertions.assertTrue(RangeMother.r(10, 15).overlaps(RangeMother.r(0, 11)));
    }

    @Test
    public void canDetectOverlappingInverted() {
        Assertions.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(10, 15)));
    }

    @Test
    public void sameRangeOverlaps() {
        Assertions.assertTrue(RangeMother.r(0, 10).overlaps(RangeMother.r(0, 10)));
    }

    @Test
    public void lowerBoundIsIncludedInRange() {
        Assertions.assertTrue(RangeMother.r(0, 10).contains(0));
    }

    @Test
    public void upperBoundIsIncludedInRange() {
        Assertions.assertTrue(RangeMother.r(0, 10).contains(10));
    }

    @Test
    public void canIterateDegenerateRange() {
        Assertions.assertEquals(0, RangeMother.r(0, 0).iterator().next());
    }

    @Test
    public void creatingDenseRangeWithNullSequencerYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DenseRange<>(null, Bound.finite(0), Bound.finite(1));
        });
    }

    @Test
    public void creatingDenseRangeWithNullLowerBoundYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DenseRange<>(RangeMother.domain, null, Bound.finite(1));
        });
    }

    @Test
    public void creatingDenseRangeWithNullUpperBoundYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DenseRange<>(RangeMother.domain, Bound.finite(0), null);
        });
    }

    @Test
    public void creatingDenseRangeWithUpperBoundLesserThenLowerBoundYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DenseRange<>(RangeMother.domain, Bound.finite(10), Bound.finite(0));
        });
    }

    @Test
    public void creatingWithNothingAsUpperValueAndIncludedUpperEndpointYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ranges.of(Endpoint.Include, Bound.finite(10), Bound.positiveInfinity(), Endpoint.Include);
        });
    }

    @Test
    public void checkingForOverlapsWithNullYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RangeMother.r(0, 10).overlaps(null);
        });
    }

    @Test
    public void comparingWithNullYieldsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RangeMother.r(0, 10).compareTo(null);
        });
    }
}
