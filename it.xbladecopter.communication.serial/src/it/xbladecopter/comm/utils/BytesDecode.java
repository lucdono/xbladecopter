/*
 XBladeCopter 
 Copyright (C) 2013 RCP & Embedded Solutions - Luca D'Onofrio.

 This file is part of XBladeCopter Project

 XBladeCopter is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 XBladeCopter is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.xbladecopter.comm.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class BytesDecode {

	static final String HEXES = "0123456789ABCDEF";

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 3];
		for (int i = 0; i < len; i += 3) {
			data[i / 3] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String getHex(byte[] raw) {
		StringBuilder hex = null;
		if (raw == null) {
			return null;
		}
		hex = new StringBuilder(3 * raw.length);
		for (final byte b : raw)
			hex.append(HEXES.charAt((b & 0xF0) >> 4))
					.append(HEXES.charAt((b & 0x0F))).append(" ");

		return hex.toString();
	}

	public static String removeZero(byte[] bytes) {
		String string;

		if (bytes[bytes.length - 1] == 0)
			string = new String(Arrays.copyOf(bytes, bytes.length - 1));
		else
			string = new String(bytes);

		return string;
	}

	public static byte[] reverse(byte bytes[], int start, int end) {
		int size = bytes.length;
		byte[] b = new byte[size];

		for (int i = start, j = end; i < end + 1; i++, j--)
			b[j] = bytes[i];

		return b;
	}

	public static long bytesToLong(byte[] bytes) {
		long l = 0;

		for (int i = 0; i < bytes.length; i++) {
			l <<= 8;
			l |= (long) bytes[i] & 0xFF;
		}

		return l;
	}

	public static int byteToIntBE(byte[] bytes, int offset) {
		short high = (short) (bytes[offset] & 0x00FF);
		short low = bytes[offset + 1];

		return ((high & 0xFF) << 8) | (low & 0xFF);

	}

	public static int bitsToInt(byte oneByte, int offset, int n) {
		return oneByte >> offset & (1 << n) - 1;
	}

	public static boolean bitRead(byte oneByte, int bit) {
		BitSet bits = new BitSet(8);

		for (int i = 0; i < 8; i++) {
			bits.set(i, (oneByte & 1) == 1);
			oneByte >>= 1;
		}

		return bits.get(bit);
	}

	public static byte[] numToBytes(int value, boolean msb, int length) {
		byte[] bytes = new byte[length];

		if (msb) {
			for (int i = 0; i < length; i++) {
				int offset = (bytes.length - 1 - i) * 8;
				bytes[i] = (byte) ((value >>> offset) & 0xFF);
			}
		} else {
			for (int i = length - 1; i >= 0; i--) {
				int offset = (bytes.length - 1 - i) * 8;
				bytes[i] = (byte) ((value >>> offset) & 0xFF);
			}

		}

		return bytes;
	}

	public static byte[] numToBytes(long value, boolean msb, int length) {
		byte[] bytes = new byte[length];

		if (msb) {
			for (int i = 0; i < length; i++) {
				int offset = (bytes.length - 1 - i) * 8;
				bytes[i] = (byte) ((value >>> offset) & 0xFF);
			}
		} else {
			for (int i = length - 1; i >= 0; i--) {
				int offset = (bytes.length - 1 - i) * 8;
				bytes[i] = (byte) ((value >>> offset) & 0xFF);
			}

		}

		return bytes;
	}

	public static int bytesToInt(byte[] buffer, int start, int length,
			boolean msb) {
		byte[] word = new byte[length];

		int num = 0;

		System.arraycopy(buffer, start, word, 0, length);
		if (msb) {
			for (int i = 0; i < word.length; i++) {
				num <<= 8;
				num |= (int) word[i] & 0xFF;
			}
		} else {
			for (int i = word.length - 1; i >= 0; i--) {
				num <<= 8;
				num |= (int) word[i] & 0xFF;
			}
		}

		return num;
	}

	public static long bytesToLong(byte[] buffer, int start, int length,
			boolean msb) {
		byte[] word = new byte[length];

		long num = 0;

		System.arraycopy(buffer, start, word, 0, length);
		if (msb) {
			for (int i = 0; i < word.length; i++) {
				num <<= 8;
				num |= (long) word[i] & 0xFF;
			}
		} else {
			for (int i = word.length - 1; i >= 0; i--) {
				num <<= 8;
				num |= (long) word[i] & 0xFF;
			}
		}

		return num;
	}

	public static float bytesToFloat(byte[] buffer, int start, int length,
			boolean msb) {

		byte[] word = new byte[length];

		System.arraycopy(buffer, start, word, 0, length);

		if (msb)
			word = reverse(word, 0, length - 1);
		ByteBuffer bBuffer = ByteBuffer.wrap(word);

		return Float.intBitsToFloat(bBuffer.getInt());
	}

	public static byte[] floatToBytes(float value, boolean msb) {

		int intbits = Float.floatToIntBits(value);
		byte word[] = numToBytes(intbits, msb, 4);

		if (msb)
			word = reverse(word, 0, 3);

		return word;
	}

	public static float asFixedPoint(long value, float quantization, long offset) {
		return (float) (value / Math.pow(2, offset)) + quantization;
	}
}