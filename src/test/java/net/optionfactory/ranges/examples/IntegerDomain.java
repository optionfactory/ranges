package net.optionfactory.ranges.examples;

import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class IntegerDomain implements DiscreteDomain<Integer, Long> {

    @Override
    public Optional<Integer> next(Integer element) {
        return element.equals(Integer.MAX_VALUE) ? Optional.<Integer>empty() : Optional.of(element + 1);
    }
    
    @Override
    public Integer mid(Integer start, Optional<Integer> end) {
        return start/2 + end.orElse(Integer.MAX_VALUE)/2;
    }
    
    @Override
    public Long distance(Integer start, Optional<Integer> end) {
        if(end.isPresent()){
            return (long)end.get() - start;
        }
        return (long)Integer.MAX_VALUE - start;
    }    

    @Override
    public Long sumDistances(Long a, Long b) {
        return a+b;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }
    
    @Override
    public long distanceToLong(Long d) {
        return d;
    }
    

    
    
    
    
    
    @Override
    public int hashCode() {
        return IntegerDomain.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntegerDomain;
    }
}
