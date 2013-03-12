package isaForever;
import isaForever.utils;
import isaForever.isaGOST;

public class isaHASH3411 {
	isaGOST ias= new isaGOST();	
	utils iUtils= new utils();
	
	 private byte[]   H = new byte[32], L = new byte[32],
	            M = new byte[32], Sum = new byte[32];
	  private byte[][] C = new byte[4][32];
	    byte[] a = new byte[8];
	    /**
	     * ГОСТ 34.11-94
	     * A (x) = (x0 ^ x1) || x3 || x2 || x1
	     * @param in
	     * @return A (x)
	     */
	    private byte[] A(byte[] in)
	    {
	        for(int j=0; j<8; j++)
	            a[j]=(byte)(in[j] ^ in[j+8]);
	        iUtils.array2array(in, 8, in, 0, 24);
	        iUtils.array2array(a, 0, in, 24, 8);
	        return in;
	    }
	    /**
	     * хеш-функция 34.11-94 массива байт
	     * рез-т хеширования в массиве H
	     * @param in исходный блок данных
	     */
	    public void hash(byte in[]) {
	    	iUtils.longToLittleEndian(in.length * 8, L, 0); // get length into L (byteCount * 8 = bitCount)
			byte[] wat = new byte[32];
			int i=0;
	    	for(i=0;(in.length-i)>32;i+=32) {
	    		iUtils.array2array(in, i, wat, 0, 32);
				sumByteArray(wat);  
		    	stephash(wat);    
	    	}
	    	if ((in.length-i)>0)  {
	    		for(int n=0;n<wat.length;n++) wat[n]=0;
	    		iUtils.array2array(in,i,wat,0,in.length-i);
	    		sumByteArray(wat);
	    		stephash(wat);
	    	}
	       	stephash(L);
	    	stephash(Sum);
	    	// рез-т хеширования в массиве H
	    }
	    /**
	     * хеширование строки
	     * @param in строка для хэширования
	     */
	    public void hash(String in) {
	    	byte[] m = iUtils.toByteArray(in);
	    	hash(m);
	    }
	    /**
	     * функция возвращает результат хеширования
	     * @return рез-т хеширования (из массива H)
	     */
	    public byte[] result() {
	    	return H;
	    }
	        
	        //
	        byte[] S = new byte[32];
	        byte[] U = new byte[32], V = new byte[32], W = new byte[32];
	        /**
	         * шаговая функция хеширования
	         * @param in исходный блок данных
	         */
	        private void stephash(byte in[]) {
	        	iUtils.array2array(in, 0, M, 0, 32); // и заодно дополнение 0 если текст короче 256 бит
	    	        //key step 1 вычисление ключей
	    	        // H = h3 || h2 || h1 || h0
	    	        // S = s3 || s2 || s1 || s0
	        	iUtils.array2array(H, 0, U, 0, 32);
	        	iUtils.array2array(M, 0, V, 0, 32);
	    	        for (int j=0; j<32; j++)   W[j] = (byte)(U[j]^V[j]);
	    	        // Encrypt gost28147-ECB   шифрующее преобразование
	    	        ias.ECB(P(W), H, 0, S, 0); 
	    	        //keys step 2,3,4       
	    	        for (int i=1; i<4; i++)  {
	    	            byte[] tmpA = A(U);
	    	            for (int j=0; j<32; j++) U[j] = (byte)(tmpA[j] ^ C[i][j]);
	    	            V = A(A(V));
	    	            for (int j=0; j<32; j++) W[j] = (byte)(U[j]^V[j]);	            
	    	            ias.ECB(P(W), H, i * 8, S, i * 8); // si = EKi [hi] (Encrypt gost28147-ECB)
	    	        }
	    	        // перемешивающее преобразование x(M, H) = y61(H^y(M^y12(S)))
	    	        for(int n = 0; n < 12; n++) ksi(S);
	    	        for(int n = 0; n < 32; n++) S[n] = (byte)(S[n] ^ M[n]);
	    	        ksi(S);
	    	        for(int n = 0; n < 32; n++) S[n] = (byte)(H[n] ^ S[n]);
	    	        for(int n = 0; n < 61; n++) ksi(S);
	    	        iUtils.array2array(S, 0, H, 0, H.length);
	    	}
	    	/**
	    	 * 256 bitsblock modul -> (Sum + a mod (2^256)) ГОСТ 34.11-94
	    	 * @param in блок данных
	    	 */
	        private void sumByteArray(byte[] in)
	        {
	            int carry = 0;

	            for (int i = 0; i != Sum.length; i++)
	            {
	                int sum = (Sum[i] & 0xff) + (in[i] & 0xff) + carry;

	                Sum[i] = (byte)sum;

	                carry = sum >>> 8;
	            }
	        }
	    // прямая запись (BigEndian)
	       private static final byte[]  C2 = {
	       	 0x00,(byte)0xFF,0x00,(byte)0xFF,0x00,(byte)0xFF,0x00,(byte)0xFF,
	       	 (byte)0xFF,0x00,(byte)0xFF,0x00,(byte)0xFF,0x00,(byte)0xFF,0x00,
	       	 0x00,(byte)0xFF,(byte)0xFF,0x00,(byte)0xFF,0x00,0x00,(byte)0xFF,
	       	 (byte)0xFF,0x00,0x00,0x00,(byte)0xFF,(byte)0xFF,0x00,(byte)0xFF};

	    	 /**
	    	 * очищение массивов и памяти перед хешированием
	    	 */
	        public void reset() {
	            for(int i=0; i<H.length; i++) H[i] = 0;  // start vector H
	            for(int i=0; i<L.length; i++) L[i] = 0;
	            for(int i=0; i<M.length; i++) M[i] = 0;
	            for(int i=0; i<C[1].length; i++) C[1][i] = 0;  // real index C = +1 because index array with 0.
	            for(int i=0; i<C[3].length; i++) C[3][i] = 0;
	            for(int i=0; i<Sum.length; i++) Sum[i] = 0;
	            iUtils.array2array(C2,0,C[2],0,C2.length);
	        }
	        /**
	         * преобразует массив байт в обратную запись
	         * @param bS преобразуемый массив 
	         */
	    	public void turnArray(byte[] bS) {
	    		byte b=0;
	    		int l=bS.length;
	    		for(int i=0;i<l/2;i++) {
	    			b=bS[i];
	    			bS[i]=bS[l-1-i];
	    			bS[l-1-i]=b;
	    		}
	    	}

	    	/**
	    	 * функ.ГОСТ 34.11-94
	    	 * (in:) n16||..||n1 ==> (out:) n1^n2^n3^n4^n13^n16||n16||..||n2
	    	 */
	        short[] wS = new short[16], w_S = new short[16];
	        private void ksi(byte[] in) {
	        	iUtils.cpyBytesToShort(in, wS);
	            w_S[15] = (short)(wS[0] ^ wS[1] ^ wS[2] ^ wS[3] ^ wS[12] ^ wS[15]);
	            //System.arraycopy(wS, 1, w_S, 0, 15); // вместо этой функ. используем свою:
	            iUtils.array2array(wS,1,w_S,0,15);
	            iUtils.cpyShortToBytes(w_S, in);
	        }
	        private byte[] K = new byte[32];   
	        /**
	         * функ. ГОСТ 34.11-94 перемешивание  
	         * (транспонирование матрицы размером 8*4 в размер 4*8)
	         * (i + 1 + 4(k - 1)) = 8i + k      i = 0-3, k = 1-8
	         * @param in массив для перемешивания
	         * @return рез-т перемешивания
	         */
	        private byte[] P(byte[] in)  {

	            for(int k = 0; k < 8; k++)  {
	                K[4*k] = in[k];
	                K[1 + 4*k] = in[ 8 + k];
	                K[2 + 4*k] = in[16 + k];
	                K[3 + 4*k] = in[24 + k];
	            }
	            return K;
	        }

}
