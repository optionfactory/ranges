package net.optionfactory.ranges.iterators;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.RangeMother;
import org.junit.Assert;
import org.junit.Test;

public class SparseRangeSpliteratorTest {

    @Test
    public void canStreamRange() {
        final Range<Integer, Long> range = RangeMother.r(RangeMother.p(1, 20), RangeMother.p(40, 50));
        List<Integer> got = range.parallelStream().collect(Collectors.toList());
        final List<Integer> expected = Stream.of(IntStream.range(1, 21), IntStream.range(40, 51)).flatMap(s -> s.boxed()).collect(Collectors.toList());
        Assert.assertEquals(expected, got);
    }

}
