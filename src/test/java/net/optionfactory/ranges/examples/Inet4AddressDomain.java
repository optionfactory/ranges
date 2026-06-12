package net.optionfactory.ranges.examples;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Optional;
import net.optionfactory.ranges.DiscreteDomain;

public class Inet4AddressDomain implements DiscreteDomain<Inet4Address, Long> {

    private static final int MAX_IPV4_INT = 0xffffffff; 

    @Override
    public Long zero() {
        return 0L;
    }
    
    @Override
    public Optional<Inet4Address> next(Inet4Address element) {
        final int value = toRawInt(element);
        if (value == MAX_IPV4_INT) {
            return Optional.empty();
        }
        return Optional.of(fromRawInt(value + 1));
    }

    @Override
    public Inet4Address mid(Inet4Address start, long distance) {
        long targetAddress = Integer.toUnsignedLong(toRawInt(start)) + distance;
        return fromRawInt((int) targetAddress);
    }
    
    @Override
    public Long distance(Inet4Address start, Optional<Inet4Address> end) {
        final int intStart = toRawInt(start);
        final int intEnd = end.map(Inet4AddressDomain::toRawInt).orElse(MAX_IPV4_INT);
        return Integer.toUnsignedLong(intEnd) - Integer.toUnsignedLong(intStart);
    }

    @Override
    public Long sumDistances(Long a, Long b) {
        return a + b;
    }

    @Override
    public int compare(Inet4Address lhs, Inet4Address rhs) {
        return Integer.compareUnsigned(toRawInt(lhs), toRawInt(rhs));
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

    private static int toRawInt(Inet4Address addr) {
        byte[] bytes = addr.getAddress();
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) <<  8) |
               (bytes[3] & 0xFF);
    }

    private static Inet4Address fromRawInt(int address) {
        final byte[] octets = new byte[] {
            (byte) ((address >>> 24) & 0xff),
            (byte) ((address >>> 16) & 0xff),
            (byte) ((address >>> 8)  & 0xff),
            (byte) (address & 0xff)
        };
        try {
            return (Inet4Address) Inet4Address.getByAddress(octets);
        } catch (UnknownHostException ex) {
            throw new IllegalStateException("Failed mapping raw bits back to Inet4Address", ex);
        }
    }
}