package isaForever;

public class utils {
	protected final static long longmask=0x00000000ffffffffL;
	protected final static int intmask=0xFFFFFFFF;
	/**
     * преобразовывает строку в массив байт 
     * @param input строка ввода
     * @return массив байт
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
     * преобразовать в long из массива 8 байт, указанных в intOff
     * @param in массив для преобразования
     * @param inOff с какого места массива начать преобраз-е
     * @return число длиной long
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
	 * @param num число типа long для преобразования
	 * @param out массив байт с рез-том
	 * @param outOff место в массиве куда занести рез-т
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
     * выбрать из массива байт неск-ко элементов и преобразовать их в массив типа short
     * @param S исходный массив
     * @param wS рез-щий массив
     */
	public void cpyBytesToShort(byte[] S, short[] wS) {
        for(int i=0; i<S.length/2; i++) {
            wS[i] = (short)(((S[i*2+1]<<8)&0xFF00)|(S[i*2]&0xFF));
        }
    }
    /**
     * выбрать из массива short неск-ко элементов и преобразовать их в массив типа байт
     * @param wS исходный массив
     * @param S результир.массив
     */
	public void cpyShortToBytes(short[] wS, byte[] S) {
        for(int i=0; i<S.length/2; i++)  {
            S[i*2 + 1] = (byte)(wS[i] >> 8);
            S[i*2] = (byte)wS[i];
        }
    }
	/**
	 * массив байт в тип integer
	 * @param in массив байт
	 * @param inOff начальный позиция в массиве для преобразов.
	 * @return число типа int
	 */
	public int bytesToint(byte[]  in,  int  inOff)    {
	  return  ((in[inOff + 3] << 24) & 0xff000000) + ((in[inOff + 2] << 16) & 0xff0000) +
	          ((in[inOff + 1] << 8) & 0xff00) + (in[inOff] & 0xff);

	}
	/**
	 * integer в массив байт (int To Little Endian)
	 * @param num число типа int для преобраз.в массив байт
	 * @param out массив байт с рез-том
	 * @param outOff позиц.в массиве куда записать рез-т
	 */
	public void intTobytes(int     num, byte[]  out,  int     outOff) {
	      out[outOff + 3] = (byte)(num >>> 24);
	      out[outOff + 2] = (byte)(num >>> 16);
	      out[outOff + 1] = (byte)(num >>> 8);
	      out[outOff] =     (byte)num;

	}
	/**
	 * long в массив байт in little endian
	 * @param n число для записи в массив
	 * @param bs массив байт в который записываем число n
	 * @param off место начала массива с которого записываем число n
	 */
	public void longToLittleEndian(long n, byte[] bs, int off)
	    {
	    	intTobytes((int)(n & 0xffffffffL), bs, off);
	    	intTobytes((int)(n >>> 32), bs, off + 4);
	    }

	/**
	 * вместо Hotspot-реализации System.arraycopy используем свою платформонезависимую
	 * для массива типа short[]
	 * @param from массив источник
	 * @param startfrom место начала копирования из источника
	 * @param to массив приемник
	 * @param startto место начала записи в приемнике
	 * @param len длина копируемых данных
	 */
	public void array2array(short[] from,int startfrom, short[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("Время выполнения: " + (end - start));
	}
	/**
	 * вместо Hotspot-реализации System.arraycopy используем свою платформонезависимую
	 * для массива типа int[]
	 * @param from массив источник
	 * @param startfrom место начала копирования из источника
	 * @param to массив приемник
	 * @param startto место начала записи в приемнике
	 * @param len длина копируемых данных
	 */
	public void array2array(int[] from,int startfrom, int[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("Время выполнения: " + (end - start));
	}
	/**
	 * вместо Hotspot-реализации System.arraycopy используем свою платформонезависимую
	 * для массива типа byte[]
	 * @param from массив источник
	 * @param startfrom место начала копирования из источника
	 * @param to массив приемник
	 * @param startto место начала записи в приемнике
	 * @param len длина копируемых данных
	 */
	public void array2array(byte[] from,int startfrom, byte[] to, int startto, int len) {
		//long start, end;
		//start = System.nanoTime();
		int predel=len+startfrom;
		for(; startfrom < predel; startfrom++) {
			to[startto++] = from[startfrom];
		}
		//end = System.nanoTime();
		//System.out.println("Время выполнения: " + (end - start));
	}
	/**
	 * сложение беззнаковых чисел
	 * @param a первый параметр сумматора
	 * @param b второй параметр сумматора
	 * @return рез-т выполения сумматора
	 */
	public long addUInt32(long a, long b) {
		long c = a + b;
		return (long)((long)c & (long)intmask);
	}
	/**
	 * объединить 2 входных int до long
	 * @param N1 первый параметр
	 * @param N2 второй параметр
	 * @return рез-т выполения 
	 */
	public long extension(int N1, int N2) {
		return ((long)N1<<32)+((long)N2&longmask);
	}
	/**
	 * вычисление разрядности числа  для int
	 * если возвращаемое число больше 0 то в старших разрядах (31-28) есть значащий бит
	 * @param Ask число типа int для анализа
	 * @return битовый разряд числа
	 */
	public int Razrad(int Ask) {
		return Ask&0xF0000000;
	}
	/**
	 * обмен м/д двумя перемен.int
	 * рез-том будет значение x в y, а значение y в x
	 * @param x 1 исходная переменная
	 * @param y 2 исходная переменная
	 */
	public void xorSwap(int x, int y) {
		x ^= (y ^= (x ^= y));
	}

}
