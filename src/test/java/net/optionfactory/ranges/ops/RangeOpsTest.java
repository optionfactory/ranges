package net.optionfactory.ranges.ops;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.optionfactory.ranges.DenseRange;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.RangeMother;
import net.optionfactory.ranges.SparseRange;
import static net.optionfactory.ranges.RangeMother.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangeOpsTest {

    final RangeOps<Integer, Long> ops = new RangeOps<>(RangeMother.domain);

    @Test
    public void canPerformDifferenceOnTwoDisjointRanges() {
        final Range<Integer, Long> lhs = r(0, 10);
        final Range<Integer, Long> rhs = r(11, 20);
        Assertions.assertEquals(r(0, 10), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 10);
        Range<Integer, Long> rhs = r(8, 20);
        Assertions.assertEquals(r(0, 7), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoOverlappingRangesWithRhsLower() {
        Range<Integer, Long> lhs = r(8, 20);
        Range<Integer, Long> rhs = r(0, 10);
        Assertions.assertEquals(r(11, 20), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoNestedRanges() {
        Range<Integer, Long> lhs = r(0, 20);
        Range<Integer, Long> rhs = r(4, 10);
        Assertions.assertEquals(r(p(0, 3), p(11, 20)), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnSelf() {
        Range<Integer, Long> x = r(0, 20);
        Assertions.assertEquals(empty(), ops.difference(x, x));
    }

    @Test
    public void canPerformDifferenceYieldingEmptyRange() {
        Range<Integer, Long> lhs = r(0, 10);
        Range<Integer, Long> rhs = r(0, 100);
        Assertions.assertFalse(ops.difference(lhs, rhs).iterator().hasNext());
    }

    @Test
    public void canPerformIntersectOnTwoNonOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 1);
        Range<Integer, Long> rhs = r(2, 3);
        Assertions.assertFalse(ops.intersection(lhs, rhs).iterator().hasNext());
    }

    @Test
    public void canPerformIntersectWithSparseRanges() {
        Range<Integer, Long> lhs = r(p(0, 1), p(2, 4));
        Range<Integer, Long> rhs = r(p(0, 1), p(5, 7));
        Assertions.assertEquals(r(0, 1), ops.intersection(lhs, rhs));
    }

    @Test
    public void canPerformIntersectOnTwoOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 20);
        Range<Integer, Long> rhs = r(4, 10);
        Assertions.assertEquals(r(4, 10), ops.intersection(lhs, rhs));
    }

    @Test
    public void canPerformsymmetriDifferenceOnTwoRanges() {
        Range<Integer, Long> lhs = r(0, 15);
        Range<Integer, Long> rhs = r(10, 20);
        Assertions.assertEquals(r(p(0, 9), p(16, 20)), ops.symmetricDifference(lhs, rhs));
    }

    @Test
    public void canMergeTwoContiguousNonOverlappingRanges() {
        final DenseRange<Integer, Long> a = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 1);
        final DenseRange<Integer, Long> b = (DenseRange<Integer, Long>) RangeMother.ranges.closed(2, 2);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b));
        final Range<Integer, Long> expected = RangeMother.r(1, 2);
        Assertions.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRanges() {
        final DenseRange<Integer, Long> a = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 2);
        final DenseRange<Integer, Long> b = (DenseRange<Integer, Long>) RangeMother.ranges.closed(2, 3);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b));
        final Range<Integer, Long> expected = RangeMother.r(1, 3);
        Assertions.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRangesWhenLatterIsSubset() {
        final DenseRange<Integer, Long> a = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 2);
        final DenseRange<Integer, Long> b = (DenseRange<Integer, Long>) RangeMother.ranges.closed(2, 2);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b));
        final Range<Integer, Long> expected = RangeMother.r(1, 2);
        Assertions.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRangesWhenFormerIsSubset() {
        final DenseRange<Integer, Long> a = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 1);
        final DenseRange<Integer, Long> b = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 2);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b));
        final Range<Integer, Long> expected = RangeMother.r(1, 2);
        Assertions.assertEquals(expected, got);
    }

    @Test
    public void canMergeSameEmptyRanges() {
        final Range<Integer, Long> a = RangeMother.ranges.closedOpen(0, 0);
        final Range<Integer, Long> b = RangeMother.ranges.closedOpen(0, 0);
        // We pass empty stream since they resolve directly to EmptyRange, preventing DenseRange casting
        final Range<Integer, Long> got = ops.canonicalize(List.of());
        Assertions.assertEquals(Long.valueOf(0), got.size());
    }

    @Test
    public void canMergeDifferentEmptyRanges() {
        final Range<Integer, Long> a = RangeMother.ranges.closedOpen(0, 0);
        final Range<Integer, Long> b = RangeMother.ranges.closedOpen(1, 1);
        final Range<Integer, Long> got = ops.canonicalize(List.of());
        Assertions.assertEquals(Long.valueOf(0), got.size());
    }

    @Test
    public void nonOverlappingRangesNonCountiguousRangesAreNotMerged() {
        final DenseRange<Integer, Long> a = (DenseRange<Integer, Long>) RangeMother.ranges.closed(1, 1);
        final DenseRange<Integer, Long> b = (DenseRange<Integer, Long>) RangeMother.ranges.closed(3, 3);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b));
        final SparseRange<Integer, Long> expected = new SparseRange<>(RangeMother.domain, Arrays.asList((DenseRange<Integer, Long>) RangeMother.r(1, 1), (DenseRange<Integer, Long>) RangeMother.r(3, 3)));
        Assertions.assertEquals(expected, got);
    }

    @Test
    public void densifiesRangesOnCreation() {
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(
                (DenseRange<Integer, Long>) RangeMother.ranges.closedOpen(0, 5),
                (DenseRange<Integer, Long>) RangeMother.ranges.closedOpen(5, 10))
        );
        Assertions.assertEquals(RangeMother.ranges.closedOpen(0, 10), got);
    }

    @Test
    public void desnifyEmptyRangesIntoEmptyRange() {
        final Range<Integer, Long> got = ops.canonicalize(List.of());
        Assertions.assertEquals(empty(), got);
    }
}
