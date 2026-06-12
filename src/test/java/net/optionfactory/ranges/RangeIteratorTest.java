package net.optionfactory.ranges;

import java.util.Iterator;
import net.optionfactory.ranges.iterators.RangeIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RangeIteratorTest {

    @Test
    public void hasNextWhenInRange() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, Bound.finite(0), Bound.finite(1));
        Assertions.assertTrue(iter.hasNext());
    }

    @Test
    public void hasNoNextWhenConsumed() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, Bound.finite(0), Bound.finite(1));
        iter.next();
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    public void emptyRangeIteratorIsEmpty() {
        Iterator<Integer> iter = new RangeIterator<>(RangeMother.domain, Bound.finite(0), Bound.finite(0));
        Assertions.assertFalse(iter.hasNext());
    }

}
