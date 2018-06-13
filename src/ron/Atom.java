package ron;

import java.util.Arrays;

import static ron.Frame.DIGIT_OFFSETS;
import static ron.Frame.PREFIX_MASKS;
import static ron.Parser.ORIGIN;
import static ron.Parser.VALUE;

public class Atom extends UUID {
//	public static final long INT60_FLAGS = Long.parseUnsignedLong(Long.toString(public static final long INT60_FLAGS = ))  << 60;
	public static final long INT32_FULL = (1L << 32) - 1;
//	public static final long INT60_FLAGS = 15L << 60;
	public static final long INT60_FLAGS = Long.parseUnsignedLong("17293822569102704640");
	public static final long BIT60 = 1L << 60;
	public static final long BIT61 = 1L << 61;
	public static final long INT16_FULL = (1L << 16) - 1;
	public static UUID ZERO_UUID_ATOM = new Atom(ZERO_UUID);

	public Atom(long value, long origin) {
		super(value, origin);
	}

	public Atom(UUID uuid) {
		super(uuid);
	}

	public Atom clone() {
		return new Atom(this);
	}

	public static Atom NewAtom() {
		return new Atom(0L, 0L);
	}

	public UUID UUID() {
		return new UUID(this);
	}
	// a[half] |= uint64(value) << DIGIT_OFFSETS[dgt] /
	public void set6(int half, int dgt, long value) {
		uuid[half] = uuid[half]  | (value << DIGIT_OFFSETS[dgt]); // FIXME reverse numbering
		// System.out.println("set6 " + uuid[half] + " half " + half);
	}

	public void set2(int half, long idx, long value) {
		uuid[half] |= value << (idx << 1);
		// System.out.println("set2 " + uuid[half] + " half " + half);
	}

	public void trim6(int half, int dgt) {
		uuid[half] &= INT60_FLAGS | PREFIX_MASKS[dgt];
		// System.out.println("trim6 " + uuid[half] + " half " + half);
	}

	public long get6(int half, int dgt) {
		return (uuid[half] >>> DIGIT_OFFSETS[dgt]) & 63L;
	}

	public void init64(int half, long flags) {
		uuid[half] = flags << 60;
		// System.out.println("init64 " + uuid[half]+ " half " + half );
	}


	public void reset4(int half, long idx, long value) {
		uuid[half] &= uuid[half] ^ (15L << (idx << 2));
//		uuid[half] = value << 60;
		uuid[half] |= value << (idx << 2);
		// System.out.println("reset4 " + uuid[half] + " half " + half);
	}

	public void arab64(int half, long value) {
		uuid[half] *= 10;
		uuid[half] += value;
		// System.out.println("arab64 " + uuid[half]+ " half " + half);
	}

	public void set1(int half, int idx) {
		uuid[half] = uuid[half] | (1L << idx);
				// System.out.println("set1 " + uuid[half]+ " half " + half);

	}

	public void inc16(int half, long idx) {
		long shift = idx << 4;
		long i = uuid[half] >>> shift;
		i++;
		uuid[half] &= uuid[half] ^ (INT16_FULL << shift);
		uuid[half] |= (i & INT16_FULL) << shift;
				// System.out.println("inc16 " + uuid[half]+ " half " + half);

	}

	public void arab16(int half, long value) {
		long i = uuid[half] & INT16_FULL;
		i *= 10;
		i += value;
		uuid[half] &= uuid[half] ^ INT16_FULL;
		uuid[half] |= i & INT16_FULL;
		// System.out.println("arab16 " + uuid[half]+ " half " + half);
	}

	public void set32(int half, int idx, long value) {
		uuid[half] |= value << (idx << 5);
		// System.out.println("set32 " + uuid[half]+ " half " + half);
	}

	public void setOrigin(long origin) {
		this.uuid[1] = origin;
	}

	public int type() {
		return (int) (uuid[1] >>> 62);
	}

	public long integer() {
		int neg = (int) (uuid[1] & (1L << 60));
		long ret = uuid[0];
		if (neg == 0) {
			return ret;
		} else {
			return -ret;
		}
	}

	public int Type() {
		return type();
	}

	public double Float() {
		int pow = this.pow();
		double ret = (double) (uuid[VALUE]) * Math.pow(10, pow);
		if ((uuid[ORIGIN] & BIT60) != 0) {
			ret = -ret;
		}
		return ret;
	}


	public int pow() {
		Atom a = this;
		int pow = (int) (a.uuid[ORIGIN] & INT16_FULL);
		if ((a.uuid[ORIGIN] & BIT61) != 0) {
			pow = -pow;
		}
		pow -= (int) ((a.uuid[ORIGIN] >>> 16) & INT16_FULL);
		return pow;
	}

	public byte[] escString(Slice body) {
		int from = (int) (uuid[0] >>> 32);
		int till = (int) (uuid[0] & INT32_FULL);
		// FIXME check if binary;
		return Arrays.copyOfRange(body.array(), from, till);
	}

	public String rawString(byte[] body) {
		long from = uuid[0] >>> 32;
		long till = uuid[0] & INT32_FULL;
		// FIXME check if binary
		return new String(unesc(Arrays.copyOfRange(body, (int )from, (int)till)));
	}

	// add JSON escapes
	public byte[] esc(byte[] str) {
		return str;
	}

	// remove JSON escapes
	public byte[] unesc(byte[] str) {
		// TODO
		return str;
	}

}