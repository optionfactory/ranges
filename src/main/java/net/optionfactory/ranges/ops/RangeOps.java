package net.optionfactory.ranges.ops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import net.optionfactory.ranges.DenseRange;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.Range.Endpoint;
import net.optionfactory.ranges.SparseRange;
import net.optionfactory.ranges.DiscreteDomain;

public class RangeOps<T, D> {

    private final DiscreteDomain<T, D> domain;
    public final DenseRange<T, D> empty;

    public RangeOps(DiscreteDomain<T, D> domain, T emptyValue) {
        this.domain = domain;
        this.empty = new DenseRange<>(domain, Endpoint.Include, emptyValue, Optional.of(emptyValue), Endpoint.Exclude);

    }

    public Range<T, D> canonicalize(Stream<DenseRange<T, D>> wannaBeRange) {
        final Iterator<DenseRange<T, D>> nonEmptyRanges = wannaBeRange
                .sorted()
                .filter(dr -> !dr.isEmpty())
                .iterator();

        if (!nonEmptyRanges.hasNext()) {
            return empty;
        }
        final List<DenseRange<T, D>> densified = new ArrayList<>();
        DenseRange<T, D> current = nonEmptyRanges.next();
        while (nonEmptyRanges.hasNext()) {
            final DenseRange<T, D> next = nonEmptyRanges.next();
            final boolean canBeMerged = JustBeforeNothing.compare(domain, current.end(), Optional.of(next.begin())) == 0 || current.overlaps(next);
            if (canBeMerged) {
                final Optional<T> max = BinaryOperator.maxBy(new JustBeforeNothing<>(domain)).apply(current.end(), next.end());
                current = new DenseRange<>(domain, Endpoint.Include, current.begin(), max, Endpoint.Exclude);
            } else {
                densified.add(current);
                current = next;
            }
        }
        densified.add(current);
        if (densified.size() == 1) {
            return densified.get(0);
        }
        return new SparseRange<>(domain, densified);
    }

    public Range<T, D> difference(Range<T, D> lhs, Range<T, D> rhs) {
        List<DenseRange<T, D>> difference = lhs.densified();
        for (DenseRange<T, D> r : rhs.densified()) {
            difference = difference(difference, r);
        }
        return canonicalize(difference.stream());
    }

    private List<DenseRange<T, D>> difference(List<DenseRange<T, D>> lhss, DenseRange<T, D> rhs) {
        final List<DenseRange<T, D>> difference = new ArrayList<>();
        for (DenseRange<T, D> lhs : lhss) {
            if (!lhs.overlaps(rhs)) {
                difference.add(lhs);
                continue;
            }
            if (domain.compare(lhs.begin(), rhs.begin()) < 0) {
                difference.add(new DenseRange<>(domain, Endpoint.Include, lhs.begin(), Optional.of(rhs.begin()), Endpoint.Exclude));
            }
            if (JustBeforeNothing.compare(domain, lhs.end(), rhs.end()) > 0) {
                difference.add(new DenseRange<>(domain, Endpoint.Include, rhs.end().get(), lhs.end(), Endpoint.Exclude));
            }
        }
        return difference;
    }

    public Range<T, D> intersection(Range<T, D> lhs, Range<T, D> rhs) {
        final List<DenseRange<T, D>> intersection = new ArrayList<>();
        for (DenseRange<T, D> l : lhs.densified()) {
            for (DenseRange<T, D> r : rhs.densified()) {
                if (!l.overlaps(r)) {
                    continue;
                }
                final T greatestLowerBound = domain.compare(l.begin(), r.begin()) > 0 ? l.begin() : r.begin();
                final Optional<T> smallestUpperBound = JustBeforeNothing.compare(domain, l.end(), r.end()) > 0 ? r.end() : l.end();
                intersection.add(new DenseRange<>(
                        domain,
                        Endpoint.Include,
                        greatestLowerBound,
                        smallestUpperBound,
                        Endpoint.Exclude
                ));
            }
        }
        return canonicalize(intersection.stream());
    }

    public Range<T, D> union(Range<T, D> lhs, Range<T, D> rhs) {
        return canonicalize(Stream.concat(lhs.densified().stream(), rhs.densified().stream()));
    }

    public Range<T, D> symmetricDifference(Range<T, D> lhs, Range<T, D> rhs) {
        return union(difference(lhs, rhs), difference(rhs, lhs));
    }
}
