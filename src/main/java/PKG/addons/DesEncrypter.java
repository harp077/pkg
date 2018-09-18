package PKG.addons;
import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
//Класс реализующий работу с алгоритмом шифрования DES
public class DesEncrypter {
    
    Cipher ecipher;
    Cipher dcipher;
    
    public DesEncrypter(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        ecipher = Cipher.getInstance("DES");
        dcipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }
    
    public String encrypt(String str) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = ecipher.doFinal(utf8);
        return new sun.misc.BASE64Encoder().encode(enc);
    }
    
    public String decrypt(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
        byte[] utf8 = dcipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }
    
    public String KeyToString (SecretKey secretKey) {
        // create new key
        //SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        // get base64 encoded version of the key
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return encodedKey;
    }
    
    public SecretKey StringToKey (byte[] decodedKey) {
        // decode the base64 encoded string
        //byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }
    
    public static void main(String[] s) throws IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
        SecretKey key=null;
        key = KeyGenerator.getInstance("DES").generateKey();
        /*
        OR: 
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            factory.generateSecret(keySpec);
        */
        DesEncrypter encrypter = new DesEncrypter(key);
        String OStr1="simple string";
        String SStr = encrypter.encrypt(OStr1);
        String OStr2 = encrypter.decrypt(SStr);
        System.out.println("Open String:"+OStr1+"\nAfter encripting: "+SStr+"\nAfter decripting: "+OStr2);
    }
}