package socialnetwork.domain.utils;

import socialnetwork.domain.validators.ValidationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {

    public static String hash(String password){
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return s.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void validate(String password, String rePassword) {
        if(password.length() < 4)
            throw new ValidationException("Password length must be grater than 4!");
        if(!password.equals(rePassword))
            throw new ValidationException("Passwords must be the same!");
    }
}
