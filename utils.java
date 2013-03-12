package isaForever;

public class utils {
	protected final static long longmask=0x00000000ffffffffL;
	protected final static int intmask=0xFFFFFFFF;
	/**
     * ��������������� ������ � ������ ���� 
     * @param input ������ �����
     * @return ������ ����
     */
	public byte[] toByteArray(String input)
    {
        byte[] bytes = new byte[input.length()];
        
        for (int i = 0; i != bytes.length; i++)
        {
            bytes[i] = (byte)input.charAt(i);
        }
        
        return bytes;
    }
    /**
     * ������������� � long �� ������� 8 ����, ��������� � intOff
     * @param in ������ ��� ��������������
     * @param inOff � ������ ����� ������� ������ ��������-�
     * @return ����� ������ long
     */
	public long bytesTolong(byte[]  in,  int  inOff)    {
		long l=0L;
		int i=in.length-inOff;
		i=i>8?8:i;
		switch (i){
		case 8:	l+=((long)in[inOff + 7] << 56)	& 0xff00000000000000L;
		case 7:	l+=((long)in[inOff + 6] << 48) 	& 0x00ff000000000000L;
		case 6:	l+=((long)in[inOff + 5] << 40) 	& 0x0000ff0000000000L;
		case 5:	l+=((long)in[inOff + 4] << 32) 	& 0x000000ff00000000L;
		case 4:	l+=((long)in[inOff + 3] << 24) 	& 0x00000000ff000000L;
		case 3:	l+=((long)in[inOff + 2] << 16) 	& 0x0000000000ff0000L;
		case 2:	l+=((long)in[inOff + 1] << 8) 	& 0x000000000000ff00L;
		case 1:	l+=(long) in[inOff    ] 		& 0x00000000000000ffL;
		}
	    return l;
	}
	/**
	 * long to array of bytes
	 * @param num ����� ���� long ��� ��������������
	 * @param out ������ ���� � ���-���
	 * @param outOff ����� � ������� ���� ������� ���-�
	 */
	public void longTobytes(long num, byte[]  out,  int outOff) {
		int i=out.length-outOff;
		i=i>8?8:i;
		switch (i){
		case 8:	out[outOff + 7] = (byte)(num >>> 56);
		case 7:	out[outOff + 6] = (byte)(num >>> 48);
		case 6:	out[outOff + 5] = (byte)(num >>> 40);
		case 5:	out[outOff + 4] = (byte)(num >>> 32);
		case 4:	out[outOff + 3] = (byte)(num >>> 24);
		case 3:	out[outOff + 2] = (byte)(num >>> 16);
		case 2:	out[outOff + 1] = (byte)(num >>> 8);
		case 1:	out[outOff    ] = (byte)num;
		}

	}
	
    /**
     * ������� �� ������� ���� ����-�� ��������� � ������������� �� � ������ ���� short
     * @param S �������� ������
     * @param wS ���-��� ������
     */
	public void cpyBytesToShort(byte[] S, short[] wS) {
        for(int i=0; i<S.length/2; i++) {
            wS[i] = (short)(((S[i*2+1]<<8)&0xFF00)|(S[i*2]&0xFF));
        }
    }
    /**
     * ������� �� ������� short ����-�� ��������� � ������������� �� � ������ ���� ����
     * @param wS �������� ������
     * @param S ���������.������
     */
	public void cpyShortToBytes(short[] wS, byte[] S) {
        for(int i=0; i<S.length/2; i++)  {
            S[i*2 + 1] = (byte)(wS[i] >> 8);
            S[i*2] = (byte)wS[i];
        }
    }
	/**
	 * ������ ���� � ��� integer
	 * @param in ������ ����
	 * @param inOff ��������� ������� � ������� ��� ����������.
	 * @return ����� ���� int
	 */
	public int bytesToint(byte[]  in,  int  inOff)    {
	  return  ((in[inOff + 3] << 24) & 0xff000000) + ((in[inOff + 2] << 16) & 0xff0000) +
	          ((in[inOff + 1] << 8) & 0xff00) + (in[inOff] & 0xff);

	}
	/**
	 * integer � ������ ���� (int To Little Endian)
	 * @param num ����� ���� int ��� ��������.� ������ ����
	 * @param out ������ ���� � ���-���
	 * @param outOff �����.� ������� ���� �������� ���-�
	 */
	public void intTobytes(int     num, byte[]  out,  int     outOff) {
	      out[outOff + 3] = (byte)(num >>> 24);
	      out[outOff + 2] = (byte)(num >>> 16);
	      out[outOff + 1] = (byte)(num >>> 8);
	      out[outOff] =     (byte)num;

	}
	/**
	 * long � ������ ���� in little endian
	 * @param n ����� ��� ������ � ������
	 * @param bs ������ ���� � ������� ���������� ����� n
	 * @param off ����� ������ ������� � �������� ���������� ����� n
	 */
	public void longToLittleEndian(long n, byte[] bs, int off)
	    {
	    	intTobytes((int)(n & 0xffffffffL), bs, off);
	    	intTobytes((int)(n >>> 32), bs, off + 4);
	    }

	/**
	 * ������ Hotspot-���������� System.arraycopy ���������� ���� ��������������������
	 * ��� ������� ���� short[]
	 * @param from ������ ��������
	 * @param startfrom ����� ������ ����������� �� ���������
	 * @param to ������ ��������
	 * @param startto ����� ������ ������ � ���������
	 * @param len ����� ���������� ������
	 */
	public void array2array(short[] from,int startfrom, short[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("����� ����������: " + (end - start));
	}
	/**
	 * ������ Hotspot-���������� System.arraycopy ���������� ���� ��������������������
	 * ��� ������� ���� int[]
	 * @param from ������ ��������
	 * @param startfrom ����� ������ ����������� �� ���������
	 * @param to ������ ��������
	 * @param startto ����� ������ ������ � ���������
	 * @param len ����� ���������� ������
	 */
	public void array2array(int[] from,int startfrom, int[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("����� ����������: " + (end - start));
	}
	/**
	 * ������ Hotspot-���������� System.arraycopy ���������� ���� ��������������������
	 * ��� ������� ���� byte[]
	 * @param from ������ ��������
	 * @param startfrom ����� ������ ����������� �� ���������
	 * @param to ������ ��������
	 * @param startto ����� ������ ������ � ���������
	 * @param len ����� ���������� ������
	 */
	public void array2array(byte[] from,int startfrom, byte[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("����� ����������: " + (end - start));
	}
	/**
	 * �������� ����������� �����
	 * @param a ������ �������� ���������
	 * @param b ������ �������� ���������
	 * @return ���-� ��������� ���������
	 */
	public long addUInt32(long a, long b) {
		long c = a + b;
		return (long)((long)c & (long)intmask);
	}
	/**
	 * ���������� 2 ������� int �� long
	 * @param N1 ������ ��������
	 * @param N2 ������ ��������
	 * @return ���-� ��������� 
	 */
	public long extension(int N1, int N2) {
		return ((long)N1<<32)+((long)N2&longmask);
	}
	/**
	 * ���������� ����������� �����  ��� int
	 * ���� ������������ ����� ������ 0 �� � ������� �������� (31-28) ���� �������� ���
	 * @param Ask ����� ���� int ��� �������
	 * @return ������� ������ �����
	 */
	public int Razrad(int Ask) {
		return Ask&0xF0000000;
	}
	/**
	 * ����� �/� ����� �������.int
	 * ���-��� ����� �������� x � y, � �������� y � x
	 * @param x 1 �������� ����������
	 * @param y 2 �������� ����������
	 */
	public void xorSwap(int x, int y) {
		x ^= (y ^= (x ^= y));
	}

}
