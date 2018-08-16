package net.optionfactory.ranges;

import org.junit.Assert;
import org.junit.Test;

public class DispatchingTest {
    
    @Test
    public void canDetectNonOverlappingSparseRangesWhenRhsIsNestedInLhs() {
        Range<Integer, Long> lhs = RangeMother.r(RangeMother.p(0, 1), RangeMother.p(4, 5));
        Assert.assertFalse(lhs.overlaps(RangeMother.r(2, 3)));
    }
    
}
