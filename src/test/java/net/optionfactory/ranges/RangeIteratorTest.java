package net.optionfactory.ranges;

import java.util.Iterator;
import java.util.Optional;
import net.optionfactory.ranges.iterators.RangeIterator;
import org.junit.Assert;
import org.junit.Test;

public class RangeIteratorTest {

    @Test
    public void hasNextWhenInRange() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, 0, Optional.of(1));
        Assert.assertTrue(iter.hasNext());
    }

    @Test
    public void hasNoNextWhenConsumed() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, 0, Optional.of(1));
        iter.next();
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void emptyRangeIteratorIsEmpty() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, 0, Optional.of(0));
        Assert.assertFalse(iter.hasNext());
    }

}
