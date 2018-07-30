package com.tander.locationtracker;

import com.tander.locationtracker.galileo.CRC16Modbus;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GalileoTest {
    private static final byte[] msg = {
            0x01, 0x17, (byte) 0x80, 0x01,
            0x11, 0x02, (byte) 0xDF, 0x03,
            0x38, 0x36, 0x38, 0x32,
            0x30, 0x34, 0x30, 0x30,
            0x35, 0x36, 0x34, 0x37,
            0x38, 0x33, 0x38, 0x04,
            0x32, 0x00};

    @Test
    public void testChecksum() {
        CRC16Modbus crc = new CRC16Modbus();
        crc.update(msg, 0, msg.length);
        byte[] chk = crc.getCrcBytes();

        assertEquals((byte)0x86, chk[0]);
        assertEquals((byte)0x9C, chk[1]);
    }
}