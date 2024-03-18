package org.owasp.netryx.util;

import java.util.ArrayList;

/**
 * UInts
 * Unsigned integers utilities
 */
public final class UInts {
    private UInts() {}

    public static byte[] toOneDimensional(byte[][] input){
        var list = new ArrayList<Byte>();
        
        for (byte[] bytes : input) {
            for (byte b : bytes) {
                list.add(b);
            }
        }

        var bytes = new byte[list.size()];

        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }

        return bytes;
    }

    public static byte[] toUInt8(int n) {
        var buffer = new byte[1];

        buffer[0] = (byte) ((n & 0x00FF));

        return buffer;
    }
    public static byte[] toUInt16(int n) {
        var buffer = new byte[2];

        buffer[0] = (byte) ((n & 0x00FF00) >> 8);
        buffer[1] = (byte) (n & 0x00FF);

        return buffer;
    }

    public static byte[] toUInt24(int n) {
        var buffer = new byte[3];

        buffer[0] = (byte) ((n & 0x00FF0000) >> 16);
        buffer[1] = (byte) ((n & 0x00FF00) >> 8);
        buffer[2] = (byte) (n & 0x00FF);

        return buffer;
    }

    public static byte[] toUInt32(long n) {
        var buffer = new byte[4];

        buffer[0] = (byte) ((n & 0x00FF000000L) >> 24);
        buffer[1] = (byte) ((n & 0x00FF0000) >> 16);
        buffer[2] = (byte) ((n & 0x00FF00) >> 8);
        buffer[3] = (byte) (n & 0x00FF);

        return buffer;
    }

    public static byte[] longToBytes(long value, int size) {
        var result = new byte[size];

        var shift = 0;
        var finalPosition = ((size > Long.BYTES) ? (size - Long.BYTES) : 0);

        for (int i = size - 1; i >= finalPosition; i--) {
            result[i] = (byte) (value >>> shift);
            shift += 8;
        }

        return result;
    }

    public static byte[] reverseByteOrder(byte[] array) {
        var length = array.length;
        var counter = length - 1;

        var temp = new byte[length];

        for (int i = 0; i < length; i++) {
            temp[i] = array[counter--];
        }

        return temp;
    }

    public static byte[] concatenate(final byte[] array1, final byte[] array2, int numberOfArray2Bytes) {
        var length = array1.length + numberOfArray2Bytes;
        var result = new byte[length];

        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, numberOfArray2Bytes);

        return result;
    }

    public static byte[] concatenate(final byte[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            throw new IllegalArgumentException("The minimal number of parameters for this function is one");
        }

        var length = 0;

        for (final byte[] a : arrays) {
            if (a != null) {
                length += a.length;
            }
        }

        var result = new byte[length];
        var currentOffset = 0;

        for (var a : arrays) {
            System.arraycopy(a, 0, result, currentOffset, a.length);
            currentOffset += a.length;
        }

        return result;
    }
}
