package net.optionfactory.ranges.ops;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import net.optionfactory.ranges.DenseRange;
import net.optionfactory.ranges.EmptyRange;
import net.optionfactory.ranges.Range;
import net.optionfactory.ranges.SparseRange;
import net.optionfactory.ranges.DiscreteDomain;

public class RangeOps<T, D> {

    private final DiscreteDomain<T, D> domain;
    private final JustBeforeNothing<T> endComparator;
    public final Range<T, D> empty;

    public RangeOps(DiscreteDomain<T, D> domain) {
        this.domain = domain;
        this.endComparator = new JustBeforeNothing<>(domain);
        this.empty = new EmptyRange<>(domain);
    }

    public Range<T, D> canonicalize(List<DenseRange<T, D>> rawRanges) {
        if (rawRanges.isEmpty()) {
            return empty;
        }
        if (rawRanges.size() == 1) {
            return rawRanges.get(0);
        }

        rawRanges.sort((a, b) -> domain.compare(a.begin(), b.begin()));

        final var densified = new ArrayList<DenseRange<T, D>>(rawRanges.size());
        var current = rawRanges.get(0);

        for (int i = 1; i < rawRanges.size(); i++) {
            final DenseRange<T, D> next = rawRanges.get(i);

            if (endComparator.compare(current.end(), Optional.of(next.begin())) >= 0) {
                Optional<T> maxEnd = endComparator.compare(current.end(), next.end()) >= 0 ? current.end() : next.end();
                current = new DenseRange<>(domain, current.begin(), maxEnd);
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

    public Range<T, D> intersection(Range<T, D> lhs, Range<T, D> rhs) {
        final var left = lhs.densified();
        final var right = rhs.densified();
        final var intersection = new ArrayList<DenseRange<T, D>>();

        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            var a = left.get(i);
            var b = right.get(j);

            T maxBegin = domain.compare(a.begin(), b.begin()) >= 0 ? a.begin() : b.begin();
            var minEnd = endComparator.compare(a.end(), b.end()) <= 0 ? a.end() : b.end();

            if (endComparator.compare(Optional.of(maxBegin), minEnd) < 0) {
                intersection.add(new DenseRange<>(domain, maxBegin, minEnd));
            }

            if (endComparator.compare(a.end(), b.end()) < 0) {
                i++;
            } else {
                j++;
            }
        }
        return canonicalize(intersection);
    }

    public Range<T, D> difference(Range<T, D> lhs, Range<T, D> rhs) {
        final var left = lhs.densified();
        final var right = rhs.densified();
        final var difference = new ArrayList<DenseRange<T, D>>();

        if (left.isEmpty()) {
            return empty;
        }

        int i = 0;
        int j = 0;
        var currentA = left.get(0);

        while (i < left.size() && j < right.size()) {
            var b = right.get(j);

            if (endComparator.compare(currentA.end(), Optional.of(b.begin())) <= 0) {
                difference.add(currentA);
                i++;
                if (i < left.size()) {
                    currentA = left.get(i);
                }
            } else if (endComparator.compare(b.end(), Optional.of(currentA.begin())) <= 0) {
                j++;
            } else {
                if (domain.compare(currentA.begin(), b.begin()) < 0) {
                    difference.add(new DenseRange<>(domain, currentA.begin(), Optional.of(b.begin())));
                }

                if (endComparator.compare(currentA.end(), b.end()) <= 0) {
                    i++;
                    if (i < left.size()) {
                        currentA = left.get(i);
                    }
                } else {
                    currentA = new DenseRange<>(domain, b.end().get(), currentA.end());
                    j++;
                }
            }
        }

        if (i < left.size()) {
            difference.add(currentA);
            i++;
            while (i < left.size()) {
                difference.add(left.get(i));
                i++;
            }
        }

        return canonicalize(difference);
    }

    public Range<T, D> union(Range<T, D> lhs, Range<T, D> rhs) {
        final var left = lhs.densified();
        final var right = rhs.densified();
        final var densified = new ArrayList<DenseRange<T, D>>();

        int i = 0;
        int j = 0;
        DenseRange<T, D> current = null;

        while (i < left.size() || j < right.size()) {
            DenseRange<T, D> next;

            if (i < left.size() && j < right.size()) {
                if (domain.compare(left.get(i).begin(), right.get(j).begin()) <= 0) {
                    next = left.get(i++);
                } else {
                    next = right.get(j++);
                }
            } else if (i < left.size()) {
                next = left.get(i++);
            } else {
                next = right.get(j++);
            }

            if (current == null) {
                current = next;
            } else {
                var canBeMerged = endComparator.compare(current.end(), Optional.of(next.begin())) >= 0;
                if (canBeMerged) {
                    Optional<T> maxEnd = BinaryOperator.maxBy(endComparator).apply(current.end(), next.end());
                    current = new DenseRange<>(domain, current.begin(), maxEnd);
                } else {
                    densified.add(current);
                    current = next;
                }
            }
        }

        if (current != null) {
            densified.add(current);
        }

        if (densified.isEmpty()) {
            return empty;
        }
        if (densified.size() == 1) {
            return densified.get(0);
        }
        return new SparseRange<>(domain, densified);
    }

    public Range<T, D> symmetricDifference(Range<T, D> lhs, Range<T, D> rhs) {
        return union(difference(lhs, rhs), difference(rhs, lhs));
    }
}
