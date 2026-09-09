import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    Optional<User> currentUser = null;
    Scanner kbd = new Scanner(System.in);
    FileManager fileManager = new FileManager();
    SecurityManager securityManager = new SecurityManager();

    public void login(){
        String userID,rawPassword;
        System.out.println("Enter userID and Password");
        userID = kbd.next();
        rawPassword = kbd.next();
        Optional<User> rawUser = fileManager.loadUserData(userID);

        if (rawUser.isEmpty()){
            System.out.println("User Not Found");
        }
        if(rawUser.get().isCurrentlyLocked() == true){
            System.out.println("Account is locked. It will unlock at " + rawUser.get().lockoutDatetime);
        }

        boolean isValid = securityManager.verifyPassword(rawPassword, rawUser.get().getEncryptedPassword());
        if(isValid == false){
            System.out.println("Invalid Password");
            rawUser.get().incrementFailedAttempts();
            if(rawUser.get().failedLoginAttempts == 3){
                System.out.println("3 failed attempts. Come after " + rawUser.get().lockoutDatetime);
            }
        }else{
            rawUser.get().resetFailedAttempts();
            currentUser = rawUser;
            System.out.println("Login Successful");
        }

        if (currentUser.get().getRole().equals(Role.Banker)){

        }
    }
    public static void main(String[] args) {
    }
}