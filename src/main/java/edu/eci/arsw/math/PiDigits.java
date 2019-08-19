package edu.eci.arsw.math;

///  <summary>

import java.util.ArrayList;

///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {

    private static int DigitsPerSum = 8;
    private static double Epsilon = 1e-17;
    private static byte[] digits;

    
    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    
    public static byte[] getDigits(int start, int count,int n) throws InterruptedException {
        digits = new byte[count];
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }
        ArrayList<PiDigitsThread> pool = new ArrayList<>();
        for (int i=0; i<n; i++ ){
            int ini=start + ((count / n) * i);;
            int fi=count/n;
            pool.add(new PiDigitsThread(start,ini,fi));
        }
        pool.forEach((hilo) -> {
            hilo.start();
        });
        for (PiDigitsThread hilo :pool){
            try {
		hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
			}
		} 
        return digits;
        
        
    }

    /// <summary>
    /// Returns the sum of 16^(n - k)/(8 * k + m) from 0 to k.
    /// </summary>
    /// <param name="m"></param>
    /// <param name="n"></param>
    /// <returns></returns>
    private static double sum(int m, int n) {
        double sum = 0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < Epsilon) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /// <summary>
    /// Return 16^p mod m.
    /// </summary>
    /// <param name="p"></param>
    /// <param name="m"></param>
    /// <returns></returns>
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }


public static class PiDigitsThread extends Thread{
    int inicio, indice,fin;
    public PiDigitsThread(int inicio,int indice,  int fin){
        this.inicio=inicio;
        this.indice=indice;
        this.fin=fin;
    }
    @Override
    public void run(){
        double sum = 0;

        for (int i = indice; i < indice+fin; i++) {
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, inicio)
                        - 2 * sum(4, inicio)
                        - sum(5, inicio)
                        - sum(6, inicio);

                inicio += DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            PiDigits.digits[i] = (byte) sum;
        }
        
    }
}
}
