/****************************************************************************************
 *  Volatile Wings - a multiplayer networked aircraft shooter game
 *  Copyright (C) 2019 Alexis Le Conte (lecontea@helsinki.fi) Student ID: 015148054
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***************************************************************************************/

package common.network;

public class Hamming {
    private static final byte[] ENCODING_LOOKUP_TABLE = {
        0x00,   /* 0 */
        0x71,   /* 1 */
        0x62,   /* 2 */
        0x13,   /* 3 */
        0x54,   /* 4 */
        0x25,   /* 5 */
        0x36,   /* 6 */
        0x47,   /* 7 */
        0x38,   /* 8 */
        0x49,   /* 9 */
        0x5A,   /* A */
        0x2B,   /* B */
        0x6C,   /* C */
        0x1D,   /* D */
        0x0E,   /* E */
        0x7F    /* F */
    };
    
    private static final byte[] DECODING_LOOKUP_TABLE = {
        0x00, 0x00, 0x00, 0x03, 0x00, 0x05, 0x0E, 0x07,     /* 0x00 to 0x07 */
        0x00, 0x09, 0x0E, 0x0B, 0x0E, 0x0D, 0x0E, 0x0E,     /* 0x08 to 0x0F */
        0x00, 0x03, 0x03, 0x03, 0x04, 0x0D, 0x06, 0x03,     /* 0x10 to 0x17 */
        0x08, 0x0D, 0x0A, 0x03, 0x0D, 0x0D, 0x0E, 0x0D,     /* 0x18 to 0x1F */
        0x00, 0x05, 0x02, 0x0B, 0x05, 0x05, 0x06, 0x05,     /* 0x20 to 0x27 */
        0x08, 0x0B, 0x0B, 0x0B, 0x0C, 0x05, 0x0E, 0x0B,     /* 0x28 to 0x2F */
        0x08, 0x01, 0x06, 0x03, 0x06, 0x05, 0x06, 0x06,     /* 0x30 to 0x37 */
        0x08, 0x08, 0x08, 0x0B, 0x08, 0x0D, 0x06, 0x0F,     /* 0x38 to 0x3F */
        0x00, 0x09, 0x02, 0x07, 0x04, 0x07, 0x07, 0x07,     /* 0x40 to 0x47 */
        0x09, 0x09, 0x0A, 0x09, 0x0C, 0x09, 0x0E, 0x07,     /* 0x48 to 0x4F */
        0x04, 0x01, 0x0A, 0x03, 0x04, 0x04, 0x04, 0x07,     /* 0x50 to 0x57 */
        0x0A, 0x09, 0x0A, 0x0A, 0x04, 0x0D, 0x0A, 0x0F,     /* 0x58 to 0x5F */
        0x02, 0x01, 0x02, 0x02, 0x0C, 0x05, 0x02, 0x07,     /* 0x60 to 0x67 */
        0x0C, 0x09, 0x02, 0x0B, 0x0C, 0x0C, 0x0C, 0x0F,     /* 0x68 to 0x6F */
        0x01, 0x01, 0x02, 0x01, 0x04, 0x01, 0x06, 0x0F,     /* 0x70 to 0x77 */
        0x08, 0x01, 0x0A, 0x0F, 0x0C, 0x0F, 0x0F, 0x0F      /* 0x78 to 0x7F */
    };
    
    public static byte[] encode(byte[] packet) {
        byte[] encoded = new byte[2 * packet.length];
        for (int i = 0; i < 2 * packet.length; ++i) {
            encoded[i] = ENCODING_LOOKUP_TABLE[read4(packet, 4 * i)];
        }
        return encoded;
    }
    
    public static byte[] decode(byte[] packet, int length) {
        byte[] decoded = new byte[length / 2];
        for (int i = 0; i < length; ++i) {
            write4(decoded, DECODING_LOOKUP_TABLE[packet[i]], 4 * i);
        }
        return decoded;
    }
    
    private static byte read4(byte[] packet, int pos) {
        byte shift = (pos % 8 >= 4) ? (byte) 0 : (byte) 4;
        return (byte) ((packet[pos / 8] >> shift) & 0x0F);
    }
    
    private static void write4(byte[] packet, byte data, int pos) {
        byte shift = (pos % 8 >= 4) ? (byte) 0 : (byte) 4;
        packet[pos / 8] |= (data << shift);
    }
    
    /*
    private static void write7(byte block, byte[] packet, int pos) {
        int first = pos / 8;
        int second = (pos + 6) / 8;
        int offset = pos % 8;
        if (offset == 0) {
            packet[first] |= block << 1;
        } else {
            packet[first] |= block >> (offset - 1);
        }
        if (second > first) {
            packet[second] |= block << (9 - offset);
        }
    }
    */
}
