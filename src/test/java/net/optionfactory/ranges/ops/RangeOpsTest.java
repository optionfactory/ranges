package net.optionfactory.ranges.ops;

import java.util.Arrays;
import java.util.Optional;
import net.optionfactory.ranges.DenseRange;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.RangeMother;
import net.optionfactory.ranges.SparseRange;
import static net.optionfactory.ranges.RangeMother.*;
import net.optionfactory.ranges.ops.RangeOps;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rferranti
 */
public class RangeOpsTest {

    final RangeOps<Integer, Long> ops = new RangeOps<>(RangeMother.domain, 0);

    @Test
    public void canPerformDifferenceOnTwoDisjointRanges() {
        final Range<Integer, Long> lhs = r(0, 10);
        final Range<Integer, Long> rhs = r(11, 20);
        Assert.assertEquals(r(0, 10), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 10);
        Range<Integer, Long> rhs = r(8, 20);
        Assert.assertEquals(r(0, 7), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoOverlappingRangesWithRhsLower() {
        Range<Integer, Long> lhs = r(8, 20);
        Range<Integer, Long> rhs = r(0, 10);
        Assert.assertEquals(r(11, 20), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnTwoNestedRanges() {
        Range<Integer, Long> lhs = r(0, 20);
        Range<Integer, Long> rhs = r(4, 10);
        Assert.assertEquals(r(p(0, 3), p(11, 20)), ops.difference(lhs, rhs));
    }

    @Test
    public void canPerformDifferenceOnSelf() {
        Range<Integer, Long> x = r(0, 20);
        Assert.assertEquals(r(Endpoint.Include, 0, 0, Endpoint.Exclude), ops.difference(x, x));
    }

    @Test
    public void canPerformDifferenceYieldingEmptyRange() {
        Range<Integer, Long> lhs = r(0, 10);
        Range<Integer, Long> rhs = r(0, 100);
        Assert.assertFalse(ops.difference(lhs, rhs).iterator().hasNext());
    }

    @Test
    public void canPerformIntersectOnTwoNonOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 1);
        Range<Integer, Long> rhs = r(2, 3);
        Assert.assertFalse(ops.intersection(lhs, rhs).iterator().hasNext());
    }

    @Test
    public void canPerformIntersectWithSparseRanges() {
        Range<Integer, Long> lhs = r(p(0, 1), p(2, 4));
        Range<Integer, Long> rhs = r(p(0, 1), p(5, 7));
        Assert.assertEquals(r(0, 1), ops.intersection(lhs, rhs));
    }

    @Test
    public void canPerformIntersectOnTwoOverlappingRanges() {
        Range<Integer, Long> lhs = r(0, 20);
        Range<Integer, Long> rhs = r(4, 10);
        Assert.assertEquals(r(4, 10), ops.intersection(lhs, rhs));
    }

    @Test
    public void canPerformsymmetriDifferenceOnTwoRanges() {
        Range<Integer, Long> lhs = r(0, 15);
        Range<Integer, Long> rhs = r(10, 20);
        Assert.assertEquals(r(p(0, 9), p(16, 20)), ops.symmetricDifference(lhs, rhs));
    }

    @Test
    public void canMergeTwoContiguousNonOverlappingRanges() {
        final DenseRange<Integer, Long> a = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(1), Endpoint.Include);
        final DenseRange<Integer, Long> b = new DenseRange<>(RangeMother.domain, Endpoint.Include, 2, Optional.of(2), Endpoint.Include);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        final DenseRange<Integer, Long> expected = RangeMother.r(1, 2);
        Assert.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRanges() {
        final DenseRange<Integer, Long> a = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(2), Endpoint.Include);
        final DenseRange<Integer, Long> b = new DenseRange<>(RangeMother.domain, Endpoint.Include, 2, Optional.of(3), Endpoint.Include);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        final DenseRange<Integer, Long> expected = RangeMother.r(1, 3);
        Assert.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRangesWhenLatterIsSubset() {
        final DenseRange<Integer, Long> a = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(2), Endpoint.Include);
        final DenseRange<Integer, Long> b = new DenseRange<>(RangeMother.domain, Endpoint.Include, 2, Optional.of(2), Endpoint.Include);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        final DenseRange<Integer, Long> expected = RangeMother.r(1, 2);
        Assert.assertEquals(expected, got);
    }

    @Test
    public void canMergeTwoOverlappingRangesWhenFormerIsSubset() {
        final DenseRange<Integer, Long> a = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(1), Endpoint.Include);
        final DenseRange<Integer, Long> b = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(2), Endpoint.Include);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        final DenseRange<Integer, Long> expected = RangeMother.r(1, 2);
        Assert.assertEquals(expected, got);
    }

    @Test
    public void canMergeSameEmptyRanges() {
        final DenseRange<Integer, Long> a = RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude);
        final DenseRange<Integer, Long> b = RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        Assert.assertEquals(Long.valueOf(0), got.size());
    }

    @Test
    public void canMergeDifferentEmptyRanges() {
        final DenseRange<Integer, Long> a = RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude);
        final DenseRange<Integer, Long> b = RangeMother.r(Endpoint.Include, 1, 1, Endpoint.Exclude);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        Assert.assertEquals(Long.valueOf(0), got.size());
    }

    @Test
    public void nonOverlappingRangesNonCountiguousRangesAreNotMerged() {
        final DenseRange<Integer, Long> a = new DenseRange<>(RangeMother.domain, Endpoint.Include, 1, Optional.of(1), Endpoint.Include);
        final DenseRange<Integer, Long> b = new DenseRange<>(RangeMother.domain, Endpoint.Include, 3, Optional.of(3), Endpoint.Include);
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(a, b).stream());
        final SparseRange<Integer, Long> expected = new SparseRange<>(RangeMother.domain, Arrays.asList(RangeMother.r(1, 1), RangeMother.r(3, 3)));
        Assert.assertEquals(expected, got);
    }

    @Test
    public void densifiesRangesOnCreation() {
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(r(Endpoint.Include, 0, 5, Endpoint.Exclude),
                r(Endpoint.Include, 5, 10, Endpoint.Exclude)).stream());
        Assert.assertEquals(r(Endpoint.Include, 0, 10, Endpoint.Exclude), got);
    }

    @Test
    public void desnifyEmptyRangesIntoEmptyRange() {
        final Range<Integer, Long> got = ops.canonicalize(Arrays.asList(r(Endpoint.Include, 0, 0, Endpoint.Exclude),
                r(Endpoint.Include, 0, 0, Endpoint.Exclude)).stream());
        Assert.assertEquals(r(Endpoint.Include, 0, 0, Endpoint.Exclude), got);
    }

}
