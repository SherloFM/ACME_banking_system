import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

abstract class User implements iEncryptable, iLockable{
    String userID, encryptedPassword, name;
    Enum<Banker,Customer> role;
    int failedLoginAttempts;
    LocalDateTime lockoutDate;

    public boolean authenticate(String inputPassword){
        boolean isAuthenticated = false;
        if(inputPassword == encryptedPassword){
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    public Enum<Banker> getRole() {
        return role;
    }

    @Override
    public String encryptPassowrd(String rawPassword){
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        String hashed = HexFormat.of().formatHex(hashBytes);
        return hashed;
    }

    interface
}
