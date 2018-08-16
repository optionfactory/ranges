package net.optionfactory.ranges.examples;

import java.math.BigInteger;
import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class BigIntegerDomain implements DiscreteDomain<BigInteger, BigInteger> {

    @Override
    public Optional<BigInteger> next(BigInteger element) {
        return Optional.of(element.add(BigInteger.ONE));
    }

    @Override
    public BigInteger mid(BigInteger start, Optional<BigInteger> end) {
        return end.get().add(start).divide(BigInteger.valueOf(2));
    }
    
    @Override
    public BigInteger distance(BigInteger start, Optional<BigInteger> end) {
        return end.get().subtract(start);
    }    

    @Override
    public BigInteger sumDistances(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public int compare(BigInteger o1, BigInteger o2) {
        return o1.compareTo(o2);
    }

    @Override
    public long distanceToLong(BigInteger d) {
        return d.min(BigInteger.valueOf(Long.MAX_VALUE)).longValue();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BigIntegerDomain;
    }

    @Override
    public int hashCode() {
        return BigIntegerDomain.class.hashCode();
    }


}
