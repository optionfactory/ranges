package net.optionfactory.ranges.ops;

import java.util.Comparator;
import java.util.Optional;

public class JustBeforeNothing<T> implements Comparator<Optional<T>> {

    private final Comparator<T> inner;

    public JustBeforeNothing(Comparator<T> inner) {
        this.inner = inner;
    }

    @Override
    public int compare(Optional<T> lhs, Optional<T> rhs) {
        return compare(inner, lhs, rhs);
    }

    @Override
    public int hashCode() {
        return inner.hashCode();
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof JustBeforeNothing == false) {
            return false;
        }
        final JustBeforeNothing<T> other = (JustBeforeNothing<T>) rhs;
        return this.inner.equals(other.inner);
    }

    public static <T> int compare(Comparator<T> inner, Optional<T> lhs, Optional<T> rhs) {
        if (!lhs.isPresent() && !rhs.isPresent()) {
            return 0;
        }
        if (!lhs.isPresent()) {
            return 1;
        }
        if (!rhs.isPresent()) {
            return -1;
        }
        return inner.compare(lhs.get(), rhs.get());
    }

}
