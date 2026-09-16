import javax.swing.text.html.Option;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    Optional<User> currentUser = Optional.empty();
    Banker banker;
    Customer customer;
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
                    customer = (Customer) currentUser.get();
                    fileManager.loadCustomerAccounts(customer);
                    startCustomerMenu();
                }
                System.out.println("Login Successful");
                return;
            }

        }
    }

    public void withdrawBanker(Account customerAcc, double amount){
        customerAcc.withdraw(amount);
    }
    public void logout(){
        login();
    }
    public void startCustomerMenu(){
        while (loggedin &&currentUser.get().getRole() == Role.Customer) {
            System.out.println("What would you like to do: \n 1)Withdraw \n 2)deposit \n 3)transfer \n 4)Logout");
            int choice = kbd.nextInt();
            if(choice == 1){
                System.out.println("Enter Amount");
                double amount = kbd.nextDouble();
                System.out.println("Enter Acc Number");
                String accNumber = kbd.next();
                customer.withdraw(amount,accNumber);
            }
            if (choice == 2){
                System.out.println("1) deposit into own account/s \n 2) deposit into other account");
                int depositchoice = kbd.nextInt();
                if (depositchoice == 1) {
                    System.out.println("Enter Amount");
                    double amount = kbd.nextDouble();
                    System.out.println("Enter Acc Number");
                    String accNumber = kbd.next();
                    customer.depositToPersonal(amount, accNumber);
                }
                if (depositchoice == 2){
                    System.out.println("Enter Customer ID");
                    String targetCustomerID = kbd.next();
                    System.out.println("Enter Target Acc Number");
                    String accNumber = kbd.next();
                    System.out.println("Enter Amount");
                    double amount = kbd.nextDouble();
                    customer.deposittoOtherUser(targetCustomerID,accNumber, amount);
                }
            }

            if(choice == 4){
                logout();
            }
        }
    }
    public void startBankMenu(){
        while (loggedin &&currentUser.get().getRole() == Role.Banker){
            System.out.println("What would you like to do: \n 1)add new customer \n 2)view customer \n 3)Perform Transaction \n 4)Logout");
            int choice = kbd.nextInt();
            if(choice == 1){
                System.out.println("Add new customer portal");
                String customerName, customerPassword;
                AccountType accountType;
                CardType cardType;


                System.out.println("Enter CPR number");
                String cpr = kbd.next();
                if(!customer.isValidCPR(cpr)){
                    System.out.println("Invalid CPR");
                    continue;
                }

                Optional<Customer> existingCustomer = fileManager.findUserByCPR(cpr);
                if(existingCustomer.isPresent()){
                    customerName = existingCustomer.get().getName();

                    System.out.println("Existing customer found.");
                    System.out.println("Customer Name: " + customerName);
                    System.out.println(
                            "UserID: " + existingCustomer.get().getUserID());
                    customerPassword = null;
                }else{
                System.out.println("Enter customer name");
                customerName = kbd.next();
                System.out.println("Enter Password");
                customerPassword = kbd.next();

                }

                System.out.println("Enter Account Type");
                accountType = AccountType.valueOf(kbd.next());
                System.out.println("Enter Card Type");
                cardType = CardType.valueOf(kbd.next());
                banker.createCustomer(customerName, customerPassword,accountType,cardType, cpr);
            }
            if(choice ==2){
                System.out.println(banker.getManagedCustomers());
            }
            if(choice == 3){
                System.out.println("Which transaction would you like to perform? \n 1)Withdraw from customer \n 2)Deposit into customer \n 3)Transfer between accounts");
                int transactionChoice = kbd.nextInt();
                if(transactionChoice == 1){
                    System.out.println("Enter amount to withdraw");
                    double amount = kbd.nextDouble();
                    System.out.println("Enter Customer ID");
                    String customerID = kbd.next();
                    System.out.println("Enter Customer Account number");
                    String accNumber = kbd.next();
                    banker.withdrawFromUser(customerID, accNumber,amount);
                }
                if(transactionChoice == 2){
                    System.out.println("1) deposit into own account \n 2) deposit into other accounts");
                    int depositChoice = kbd.nextInt();
                    if(depositChoice == 1){
                        System.out.println("Enter amount to deposit");
                        double amount = kbd.nextDouble();
                        System.out.println("Enter Customer ID");
                        String customerID = kbd.next();
                        System.out.println("Enter Customer Account number");
                        String accNumber = kbd.next();
                        banker.deposittoCurrentUser(customerID, accNumber,amount);
                    }
                    if (depositChoice == 2){
                        System.out.println("Enter amount to deposit");
                        double amount = kbd.nextDouble();
                        System.out.println("Enter Customer ID");
                        String customerID = kbd.next();
                        System.out.println("Enter Customer Account number");
                        String accNumber = kbd.next();
                        banker.deposittoOtherCustmer(customerID, accNumber,amount);

                    }
                }
                if(transactionChoice == 3){

                }
                if(transactionChoice == 4){

                }
            }if(choice == 4){
                logout();
            }
        }
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.login();
    }
}