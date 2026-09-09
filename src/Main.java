import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    Optional<User> currentUser = Optional.empty();
    Scanner kbd = new Scanner(System.in);
    FileManager fileManager = new FileManager();

    public void login(){
        String userID,rawPassword;
        System.out.println("Enter userID and Password");
        userID = kbd.next();
        rawPassword = kbd.next();
        Optional<User> rawUser = fileManager.loadUserData(userID);

        if (rawUser.isEmpty()){
            System.out.println("User Not Found");
            return;
        }
        if(rawUser.get().isCurrentlyLocked() == true){
            System.out.println("Account is locked. It will unlock at " + rawUser.get().getLockoutDatetime());
        }

        boolean isValid = rawUser.get().authenticate(rawPassword);
        if(isValid == false){
            System.out.println("Invalid Password");
            rawUser.get().incrementFailedAttempts();
            if(rawUser.get().getFailedLoginAttempts() == 3){
                System.out.println("3 failed attempts. Come after " + rawUser.get().getLockoutDatetime());
            }
        }else{
            rawUser.get().resetFailedAttempts();
            currentUser = rawUser;
            System.out.println("Login Successful");
        }

        if (currentUser.get().getRole() == Role.Banker){
            System.out.println("Banker");
        }else{
            System.out.println("Customer");
        }
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.login();
    }
}