package net.optionfactory.ranges;

import java.util.Comparator;
import java.util.Optional;

public interface DiscreteDomain<T, DISTANCE_TYPE> extends Comparator<T> {

    Optional<T> next(T element);

    T mid(T start, Optional<T> end);
    
    DISTANCE_TYPE distance(T start, Optional<T> end);

    DISTANCE_TYPE sumDistances(DISTANCE_TYPE a, DISTANCE_TYPE b);
    
    long distanceToLong(DISTANCE_TYPE d);
    
}
