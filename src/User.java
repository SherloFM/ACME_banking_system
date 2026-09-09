import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.security.NoSuchAlgorithmException;

enum Role{
    Banker,
    Customer
}

abstract class User implements iEncryptable, iLockable, Serializable {
    protected String userID, encryptedPassword, name;
    protected Role role;
    protected int failedLoginAttempts;
    protected LocalDateTime lockoutDatetime;


    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}
    public String getEncryptedPassword() {return encryptedPassword;}
    public String getUserID() {return userID;}


    @Override
    public boolean authenticate(String inputPassword){
        boolean isAuthenticated = false;
        String hashedInput = encryptPassowrd(inputPassword);
        if(hashedInput.equals(encryptedPassword)){
            isAuthenticated = true;
        }
        return isAuthenticated;
    }


    @Override
    public String encryptPassowrd(String rawPassword){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            String hashed = HexFormat.of().formatHex(hashBytes);
            return hashed;
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCurrentlyLocked(){
        boolean isLocked = false;
        if (lockoutDatetime.isAfter(LocalDateTime.now())){
            isLocked = true;
        }

        return isLocked;
    }

    @Override
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if(failedLoginAttempts >= 3){
            this.lockoutDatetime = LocalDateTime.now().plusMinutes(1);
        }
    }

    @Override
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockoutDatetime = null;
    }
}
