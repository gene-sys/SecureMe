package isaForever;
import isaForever.utils;

public class isaGOST {
	//
	protected final static long longmask=0x00000000ffffffffL;
	protected final static int intmask=0xFFFFFFFF;
	utils iUtils= new utils();	
	//
	private static final byte Sbox[] = { // data for substitution
        0x4,0xA,0x9,0x2,0xD,0x8,0x0,0xE,0x6,0xB,0x1,0xC,0x7,0xF,0x5,0x3,
        0xE,0xB,0x4,0xC,0x6,0xD,0xF,0xA,0x2,0x3,0x8,0x1,0x0,0x7,0x5,0x9,
        0x5,0x8,0x1,0xD,0xA,0x3,0x4,0x2,0xE,0xF,0xC,0x7,0x6,0x0,0x9,0xB,
        0x7,0xD,0xA,0x1,0x0,0x8,0x9,0xF,0xE,0x4,0x6,0xC,0xB,0x2,0x5,0x3,
        0x6,0xC,0x7,0x1,0x5,0xF,0xD,0x8,0x4,0xA,0x9,0xE,0x0,0x3,0xB,0x2,
        0x4,0xB,0xA,0x0,0x7,0x2,0x1,0xD,0x3,0x6,0x8,0x5,0x9,0xC,0xF,0xE,
        0xD,0xB,0x4,0x1,0x3,0xF,0x5,0x9,0x0,0xA,0xE,0x7,0x6,0x8,0x2,0xC,
        0x1,0xF,0xD,0x0,0x5,0x7,0xA,0x4,0x9,0x2,0x3,0xE,0x6,0xB,0x8,0xC
    };
	private byte sinhrosend[] = {-48,-86,-75,85,-86,-43,83,-92}; //синхропосылка

  /**
   * основной шиаг шифрования по ГОСТ 28147
   * @param n1 элемент замещения
   * @param key элемент ключа
   * @return замещенный элемент
   */
	private int mainStep(int n1, int key) {
			int cm = (key + n1); // CM1
		// S-box replacing
			int om =  Sbox[cm & 0xF];
				om += Sbox[ 16 + ((cm >>  4) & 0xF)] <<  4;
				om += Sbox[ 32 + ((cm >>  8) & 0xF)] <<  8;
				om += Sbox[ 48 + ((cm >> 12) & 0xF)] << 12;
				om += Sbox[ 64 + ((cm >> 16) & 0xF)] << 16;
				om += Sbox[ 80 + ((cm >> 20) & 0xF)] << 20;
				om += Sbox[ 96 + ((cm >> 24) & 0xF)] << 24;
				om += Sbox[112 + ((cm >> 28) & 0xF)] << 28;
				return om << 11 | om >>> 21; // 11-leftshift
	}
	/**
	 * зашифрование 1го блока размером 64бит(8байт) режим простой замены 
	 * @param workingKey ключ массив 4 байт*8
	 * @param in блок незашифр.данных
	 * @param inOff с какого места начать, если длина исходго блока > 64бит
	 * @param out блок зашифрованных данных
	 * @param outOff в какое место записать рез-т, если длина исходго блока > 64бит
	 */
	public void ECB(int[] workingKey,  byte[] in, int inOff,byte[]  out, int outOff) {
	        int N1, N2, tmp, j;  //tmp -> for saving N1
	        N1 = iUtils.bytesToint(in, inOff);
	        N2 = iUtils.bytesToint(in, inOff + 4);
	          for(int k = 0; k < 3; k++)  { // 1-24 steps
	              for(j = 0; j < 8; j++) {
	                  tmp = N1;
	                  N1 = N2 ^ mainStep(N1, workingKey[j]); // CM2
	                  N2 = tmp;
	              }
	            }
	            for(j = 7; j > 0; j--) { // 25-31 steps
	                tmp = N1;
	                N1 = N2 ^  mainStep(N1, workingKey[j]); // CM2
	                N2 = tmp;
	            }
	        N2 = N2 ^ mainStep(N1, workingKey[0]);  // 32 step (N1=N1)
	        iUtils.intTobytes(N1, out, outOff);
	        iUtils.intTobytes(N2, out, outOff + 4);
	 }
	/**
	 * зашифрование режим простой замены 
	 * @param workingKey ключ массив 32 байт
	 * @param in блок незашифр.данных
	 * @param inOff  с какого места начать, если длина исходго блока > 64бит
	 * @param out блок зашифрованных данных
	 * @param outOff в какое место записать рез-т, если длина исходго блока > 64бит
	 */
	public void ECB(byte[] workingKey,  byte[] in, int inOff,byte[]  out, int outOff) {
		int[] key = new int[8];
		for(int i=0; i!=8; i++)	key[i] = iUtils.bytesToint(workingKey,i*4);
		ECB(key,in,inOff,out,outOff);
	}
	

	/**
	 * расшифрование режим простой замены 
	 * @param workingKey ключ массив 4 байт*8
	 * @param in блок зашифрованн.данных
	 * @param inOff откуда начать расшифровку для блоков длиной > 64бит
	 * @param out блок расшифрованных данных
	 * @param outOff куда писать рез-т для блоков длиной > 64бит
	 */
	public void dECB(int[] workingKey,  byte[] in, int inOff,byte[]  out, int outOff) {
	        int N1, N2, tmp, j;  //tmp -> for saving N1
	        N1 = iUtils.bytesToint(in, inOff);
	        N2 = iUtils.bytesToint(in, inOff + 4);
	          for(j = 0; j < 8; j++) { // 1-8 steps
	              tmp = N1;
	              N1 = N2 ^ mainStep(N1, workingKey[j]); // CM2
	              N2 = tmp;
	           }
	           for(int k = 0; k < 3; k++) { //9-31 steps
	             for(j = 7; j >= 0; j--) {
	                 if ((k == 2) && (j==0))  break; // break 32 step 
	                 tmp = N1;
	                 N1 = N2 ^ mainStep(N1, workingKey[j]); // CM2
	                 N2 = tmp;
	             }
	           }
	        N2 = N2 ^ mainStep(N1, workingKey[0]);  // 32 step (N1=N1)
	        iUtils.intTobytes(N1, out, outOff);
	        iUtils.intTobytes(N2, out, outOff + 4);
	}
	/**
	 * режим гаммирования - потковое [за/рас]шифрование произвольной длины
	 * @param workingKey ключ 8*4байт
	 * @param in исходный блок (массив байт в т.ч.1Байт)
	 * @param out рез-щий блок (массив байт в т.ч.1Байт)
	 */
	public void  OFB(int[] workingKey,  byte[] in, byte[]  out) {
			int C1 = 0x1010104;
			int C2 = 0x1010101;
			int N3,N4;
			int inOff=0;
			//long cypher, summ;
			byte[] tmp= new byte[8];
			byte[] gamma= new byte[8];
			//byte sinhrosend[] = {-48,-86,-75,85,-86,-43,83,-92}; //синхропосылка
			// зашифрование N1 и N2  по 32-З
			 ECB(workingKey, sinhrosend, 0,gamma,0); 
			 N3 = iUtils.bytesToint(gamma, 0);
			 N4 = iUtils.bytesToint(gamma, 4);
			 while (inOff<in.length) {
				 N3+=C2; // СМ3
				 N4= CM4(N4,C1); // СМ4
				 iUtils.intTobytes(N3, tmp, 0);
				 iUtils.intTobytes(N4, tmp, 4);
				 ECB(workingKey, tmp, 0,gamma,0);
			     for(int i=0;i<8;i++) {
			    	 if ((inOff+i)!=in.length)			    		 
			    		 out[inOff+i] = (byte)(gamma[i] ^ in[inOff+i]);//сумматор СМ5
			    	 else break;
			     }
			     inOff+=8;
			 }
		}
	/**
	 * Генератор ключей 256 бит, используя режим OFB (гаммирования) ГОСТ 
	 * @param workingKey - исходный ключ
	 * @return новый сгенеренный ключ
	 */
	public byte[]  KeyGenGOST(int[] workingKey) {
		int C1 = 0x1010104;
		int C2 = 0x1010101;
		int N3,N4;
		//long cypher, summ;
		byte[] tmp= new byte[8];
		byte[] gamma= new byte[8];
		byte[] out= new byte[32];
		// зашифрование N1 и N2  по 32-З
		 ECB(workingKey, sinhrosend, 0,gamma,0); 
		 N3 = iUtils.bytesToint(gamma, 0);
		 N4 = iUtils.bytesToint(gamma, 4);
		 for(int i=0;i<4;i++) {
			 N3+=C2; // СМ3
			 N4= CM4(N4,C1); // СМ4
			 iUtils.intTobytes(N3, tmp, 0);
			 iUtils.intTobytes(N4, tmp, 4);
			 ECB(workingKey, tmp, 0,gamma,0);
			 for(int j=0;j<8;j++)
				 out[j+i*8] = (byte)(gamma[j]);
		 }
		 return out;
	}
	/**
	 * ГОСТ 28147
	 * сумматоры СМ1 и СМ3 = просто сложение 2 int в выходной int
	 * @param N1 первый парам-р сложения
	 * @param N2 второй парам-р сложения
	 * @return рез-т сложения
	 */
	private int CM13(int N1, int N2) {
		return (int)((N1+N2)&intmask);
	}
	/**
	 * ГОСТ 28147
	 * сумматор СМ2 = xor 2 int в выходной int
	 */
	/**
	 * ГОСТ 28147
	 * сумматор СМ4 = сумма по модулю (2^32)-1
	 * @param N2 первый параметр сумматора
	 * @param C1 второй параметр сумматора
	 * @return рез-т выполения сумматора
	 */
	private static int CM4(int N2, int C1) {
		long x = (((long)N2)&longmask)+(((long)C1)&longmask);
		return (int)(x+(x>>>32));
	}	
	/**
	 * ГОСТ 28147
	 * сумматор СМ5 по мод.2 для любого кол-ва бит до 64
	 * @param a первый параметр сумматора
	 * @param b второй параметр сумматора
	 * @return рез-т выполения сумматора
	 */
	private static long CM5(long a, long b) {
		long c = a ^ b;
		return (long)c;
	}
}
