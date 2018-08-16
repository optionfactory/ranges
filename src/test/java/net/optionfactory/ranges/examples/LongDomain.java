package net.optionfactory.ranges.examples;

import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class LongDomain implements DiscreteDomain<Long, Long> {

    @Override
    public Optional<Long> next(Long element) {
        return element.equals(Long.MAX_VALUE) ? Optional.<Long>empty() : Optional.of(element + 1);
    }

    @Override
    public Long mid(Long start, Optional<Long> end) {
        return end.orElse(Long.MAX_VALUE) / 2 + start / 2;
    }

    @Override
    public Long distance(Long start, Optional<Long> end) {
        if (end.isPresent()) {
            return end.get() - start;
        }
        return Long.MAX_VALUE - start;
    }

    @Override
    public Long sumDistances(Long a, Long b) {
        return a + b;
    }

    @Override
    public int compare(Long o1, Long o2) {
        return o1.compareTo(o2);
    }

    @Override
    public long distanceToLong(Long d) {
        return d;
    }

    @Override
    public int hashCode() {
        return LongDomain.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongDomain;
    }
}
