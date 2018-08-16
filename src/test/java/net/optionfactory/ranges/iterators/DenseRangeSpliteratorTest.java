package net.optionfactory.ranges.iterators;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.RangeMother;
import org.junit.Assert;
import org.junit.Test;

public class DenseRangeSpliteratorTest {

    @Test
    public void canStreamEmptyRange() {
        final Range<Integer, Long> range = RangeMother.r(Endpoint.Include, 0, 0, Endpoint.Exclude);
        List<Integer> got = range.stream().collect(Collectors.toList());
        Assert.assertEquals(Arrays.<Integer>asList(), got);
    }

    @Test
    public void canStreamSingleton() {
        final Range<Integer, Long> range = RangeMother.r(0, 0);
        List<Integer> got = range.stream().collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(0), got);
    }

    @Test
    public void canStreamRange() {
        final Range<Integer, Long> range = RangeMother.r(0, 3);
        List<Integer> got = range.stream().collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(0, 1, 2, 3), got);
    }

}
