package net.optionfactory.ranges.examples;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class Inet4AddressDomain implements DiscreteDomain<Inet4Address, Long> {

    @Override
    public Optional<Inet4Address> next(Inet4Address element) {
        final long longElement = Integer.toUnsignedLong(element.hashCode());
        if (0xffffffff == longElement) {
            return Optional.empty();
        }
        return Optional.of(ipv4(longElement + 1));
    }

    @Override
    public Inet4Address mid(Inet4Address start, Optional<Inet4Address> end) {
        final long longStart = Integer.toUnsignedLong(start.hashCode());
        final long longEnd = Integer.toUnsignedLong(end.map(Inet4Address::hashCode).orElse(0xffffffff));
        return ipv4((longEnd + longStart) /2);
    }
    
    @Override
    public Long distance(Inet4Address start, Optional<Inet4Address> end) {
        final long longStart = Integer.toUnsignedLong(start.hashCode());
        final long longEnd = Integer.toUnsignedLong(end.get().hashCode());
        return longEnd - longStart;
    }

    @Override
    public Long sumDistances(Long a, Long b) {
        return a+b;
    }

    

    @Override
    public int compare(Inet4Address lhs, Inet4Address rhs) {
        return Integer.compareUnsigned(lhs.hashCode(), rhs.hashCode());
    }
    
    @Override
    public long distanceToLong(Long d) {
        return d;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Inet4AddressDomain;
    }

    @Override
    public int hashCode() {
        return Inet4AddressDomain.class.hashCode();
    }

    private static Inet4Address ipv4(long address) {
        final byte[] octets = new byte[4];
        for (int i = 0; i != octets.length; ++i) {
            final int shiftBy = 24 - Byte.SIZE * i;
            octets[i] = (byte) ((address >>> shiftBy) & 0xff);
        }
        try {
            return (Inet4Address) Inet4Address.getByAddress(octets);
        } catch (UnknownHostException ex) {
            throw new IllegalStateException("Never happens: UnknownHostException building a Inet4Address from octets", ex);
        }
    }

}
