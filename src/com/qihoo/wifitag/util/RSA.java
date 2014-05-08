package com.qihoo.wifitag.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import android.util.Base64;

public class RSA {
	private static RSAPublicKey publicKey = null;
	private static RSAPrivateKey privateKey = null;
	private static int keyLength;
	
	private String defaultModulus = "89518651557495779765289733526022230028713903696945335765735193417128401827053";
	private String defaultPublicExponent = "65537";
	private String defaultPrivateExponent = "21748263882973706538641426021962035895869755725540448042959332505935842315641";
    private int defaultKeyLength = 32;
	
	public RSA(){
		if (RSA.publicKey == null && RSA.privateKey == null){
			setDefaultKey();
		}
	}
	
    public String encrypt(String plainStr){
    	String str = "";
    	try {
            Cipher cipher = Cipher.getInstance("RSA");
            byte[] plainText = plainStr.getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, RSA.publicKey);
            byte[] enBytes = cipher.doFinal(plainText);
            str = encryptBASE64(enBytes );
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
        return str;
    }
    
    public String decrypt(String encryptedStr) {
    	String strdecoded = "";
    	
    	try {
        	Cipher cipher = Cipher.getInstance("RSA");
        	cipher.init(Cipher.DECRYPT_MODE, RSA.privateKey);
        	byte[] encryptedBytes = decryptBASE64(encryptedStr);
        	byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        	strdecoded = new String(decryptedBytes);
        	
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	return strdecoded;
    }
	
	private void setDefaultKey(){
		try {
			BigInteger bintModulus = new BigInteger(this.defaultModulus);
			BigInteger bintPublicExponent = new BigInteger(this.defaultPublicExponent);
			BigInteger bintPrivateExponent = new BigInteger(this.defaultPrivateExponent);
			
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(bintModulus, bintPublicExponent);
			RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(bintModulus, bintPrivateExponent);
			
			RSA.publicKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
			RSA.privateKey = (RSAPrivateKey)KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
			RSA.keyLength = this.defaultKeyLength;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	/**
	 * Don't call it!!!!
	 * @param keyLength
	 */
	public void updateKey(int keyLength){
		try {
			RSA.keyLength = keyLength;
		    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		    keyPairGen.initialize(RSA.keyLength * 8);
		    KeyPair keyPair = keyPairGen.generateKeyPair();
		    RSA.publicKey = (RSAPublicKey)keyPair.getPublic();
		    RSA.privateKey = (RSAPrivateKey)keyPair.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			
		}
	}

	private String encryptBASE64(byte[] bytestr) {
		try {
			return new String(Base64.encode(bytestr, 0, bytestr.length, Base64.DEFAULT), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public byte[] decryptBASE64(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			byte[] encode = str.getBytes("UTF-8");
			
			return Base64.decode(encode, 0, encode.length, Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
