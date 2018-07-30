package com.tander.locationtracker.galileo;

import java.nio.ByteBuffer;

public class Galileo {
    private static int COEFF = 1_000_000;
    private static final byte GPS = 0;
    private static final byte NETWORK = 2;
    private static final byte INT_SIZE = 4;

    private static final int HEAD_SIZE = 1;
    private static final int PACK_LEN_SIZE = 2;
    private static final int CHECKSUM_SIZE = 2;
    private static final int TAG_SIZE = 1;
    private static final int IMEI_SIZE = 15;
    private static final int COORDINATES_SIZE = 9;

    private enum Tag {
        IMEI((byte) 0x03),
        COORDINATES((byte) 0x30);

        private byte tagValue;

        Tag(byte i) {
            tagValue = i;
        }

        public byte getTagValue() {
            return tagValue;
        }
    }

    private byte[] getChecksum(byte[] data) {
        CRC16Modbus crc = new CRC16Modbus();
        crc.update(data, 0, data.length);
        return crc.getCrcBytes();
    }

    public byte[] createHeadPack(String imei) {
        byte[] payload = new byte[(TAG_SIZE + IMEI_SIZE)];
        payload[0] = Tag.IMEI.getTagValue();

        byte[] imeiBytes = imei.getBytes();
        System.arraycopy(imeiBytes, 0, payload, 1, IMEI_SIZE);

        return createPack(payload);
    }

    public byte[] createMainPack(int satellitesCount, boolean isGps, double latitude, double longitude) {
        byte[] payload = new byte[(TAG_SIZE + COORDINATES_SIZE)];
        payload[0] = Tag.COORDINATES.getTagValue();

        byte mark = isGps ? GPS : NETWORK;
        payload[1] = (byte)((satellitesCount & 0xF) ^ (mark << 4));

        byte[] lat = ByteBuffer.allocate(INT_SIZE).putInt((int) (latitude * COEFF)).array();
        byte[] lon = ByteBuffer.allocate(INT_SIZE).putInt((int) (longitude * COEFF)).array();
        System.arraycopy(lat, 0, payload, 2, INT_SIZE);
        System.arraycopy(lon, 0, payload, 6, INT_SIZE);

        return createPack(payload);
    }

    private byte[] createPack(byte[] payload) {
        int packLength = calculatePackLength(payload.length);
        byte[] pack = new byte[packLength];

        pack[0] = 0x01;
        pack[1] = (byte) (payload.length & 0xFF);
        pack[2] = (byte) 0x80;

        System.arraycopy(payload, 0, pack, 3, payload.length);

        byte[] extPayload = new byte[packLength - 2];
        System.arraycopy(pack, 0, extPayload, 0, packLength - 2);

        byte[] checksum = getChecksum(extPayload);
        pack[packLength - 2] = checksum[0];
        pack[packLength - 1] = checksum[1];

        return pack;
    }

    private int calculatePackLength(int payloadLength) {
        return HEAD_SIZE + PACK_LEN_SIZE + payloadLength + CHECKSUM_SIZE;
    }

    private static final byte[] msg = {0x01, 0x17, (byte) 0x80, 0x01, 0x11, 0x02, (byte) 0xDF, 0x03, 0x38, 0x36, 0x38, 0x32, 0x30, 0x34, 0x30, 0x30, 0x35, 0x36, 0x34, 0x37, 0x38, 0x33, 0x38, 0x04, 0x32, 0x00};

    public static void main(String[] args) {
        Galileo galileo = new Galileo();
        byte[] bytes = galileo.createHeadPack("868204005647838");

        for (byte b : msg)
            System.out.format("0x%02x ", b);

        System.out.println("\n");
        CRC16Modbus crc = new CRC16Modbus();
        crc.update(msg, 0, msg.length);
        byte[] chk = crc.getCrcBytes();
        System.out.printf("0x%02x 0x%02x", chk[0], chk[1]);

    }



}