import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    Optional<User> currentUser = Optional.empty();
    Banker banker;
    Scanner kbd = new Scanner(System.in);
    FileManager fileManager = new FileManager();
    boolean loggedin = false;

    public void login() {
        String userID, rawPassword;
        System.out.println("Enter userID and Password");
        userID = kbd.next();
        Optional<User> rawUser = fileManager.loadUserData(userID);
        while (true) {
        rawPassword = kbd.next();

        if (rawUser.isEmpty()) {
            System.out.println("User Not Found");
            return;
        }
        if (rawUser.get().isCurrentlyLocked() == true) {
            System.out.println("Account is locked. It will unlock at " + rawUser.get().getLockoutDatetime());
            return;
        }
            if (!rawUser.get().authenticate(rawPassword)) {
                System.out.println(rawUser.get().failedLoginAttempts);
                rawUser.get().incrementFailedAttempts();
                System.out.println(rawUser.get().failedLoginAttempts);
                if (rawUser.get().failedLoginAttempts == 3) {
                    System.out.println("3 failed attempts. Come after " + rawUser.get().getLockoutDatetime());
                    return;
                }
                System.out.println("Invalid Password try again");
            } else {
                rawUser.get().resetFailedAttempts();
                currentUser = rawUser;
                loggedin = true;
                if (currentUser.get().getRole() == Role.Banker) {
                    System.out.println("Banker");
                    banker = (Banker) currentUser.get();
                    startBankMenu();
                } else {
                    System.out.println("Customer");
                }
                System.out.println("Login Successful");
                return;
            }

        }
    }

    public void logout(){
        login();
    }

    public void startBankMenu(){
        while (loggedin &&currentUser.get().getRole() == Role.Banker){
            System.out.println("What would you like to do: \n 1)add new customer \n 2)view customer \n 3)logout");
            int choice = kbd.nextInt();
            if(choice == 1){
                System.out.println("Add new customer portal");
                String customerName, customerPassword;
                AccountType accountType;
                CardType cardType;
                System.out.println("Enter customer name");
                customerName = kbd.next();
                System.out.println("Enter Password");
                customerPassword = kbd.next();
                System.out.println("Enter Account Type");
                accountType = AccountType.valueOf(kbd.next());
                System.out.println("Enter Card Type");
                cardType = CardType.valueOf(kbd.next());

                banker.createCustomer(customerName, customerPassword,accountType,cardType);
            }
            if(choice ==2){
                System.out.println(banker.getManagedCustomers());
            }
            if(choice == 3){
                logout();
            }
        }
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.login();
    }
}