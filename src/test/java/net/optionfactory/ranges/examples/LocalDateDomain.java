package net.optionfactory.ranges.examples;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class LocalDateDomain implements DiscreteDomain<LocalDate, Long> {

    @Override
    public Optional<LocalDate> next(LocalDate element) {
        if(element.equals(LocalDate.MAX)){
            return Optional.empty();
        }
        return Optional.of(element.plus(1, ChronoUnit.DAYS));
    }

    @Override
    public LocalDate mid(LocalDate start, Optional<LocalDate> end) {
        return start.plus(distance(start, end) / 2, ChronoUnit.DAYS);
    }

    @Override
    public Long distance(LocalDate start, Optional<LocalDate> end) {
        return ChronoUnit.DAYS.between(start, end.orElse(LocalDate.MAX));
    }

    @Override
    public Long sumDistances(Long a, Long b) {
        return a+b;
    }

    @Override
    public long distanceToLong(Long d) {
        return d;
    }

    @Override
    public int compare(LocalDate o1, LocalDate o2) {
        return o1.compareTo(o2);
    }
    
}
