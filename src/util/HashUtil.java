package util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

	public static String md5(){
		long unixTime = System.currentTimeMillis();
		String random = Double.toString(Math.random());
		String texto = unixTime+random;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(texto.getBytes(),0,texto.length());
			return new BigInteger(1,md5.digest()).toString(8);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Falha no MD5");
		}
		
		return null;
	}
	
	public static String md5(String nome){
		String random = Double.toString(Math.random());
		String texto = nome+random;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(texto.getBytes(),0,texto.length());
			return new BigInteger(1,md5.digest()).toString(8);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Falha no MD5");
		}
		
		return null;
	}
}
