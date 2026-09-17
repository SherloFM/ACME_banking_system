import javax.swing.text.html.Option;
import java.io.File;
import java.security.spec.RSAOtherPrimeInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
            System.out.println("What would you like to do: \n 1)Withdraw \n 2)deposit \n 3)transfer\n 4)view Overdraft fees \n 5)view transaction \n 6)Logout");
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
            if (choice == 3){
                System.out.println("1) transfer to other people \n2) transfer between accounts");
                int transferChoice = kbd.nextInt();
                if(transferChoice == 1){
                    System.out.println("Enter Recipient Customer ID");
                    String targetCustomerID = kbd.next();
                    System.out.println("Enter Recipient Account Number");
                    String targetAccNumber = kbd.next();
                    System.out.println("Enter Customer Customer ID");
                    String customerID = kbd.next();
                    System.out.println("Enter Customer Account Number");
                    String customerAcc = kbd.next();
                    System.out.println("Enter Amount");
                    double amount = kbd.nextDouble();
                    customer.transferToOtherAcc(targetCustomerID, targetAccNumber, customerID, customerAcc, amount);
                }

                if (transferChoice == 2){
                    System.out.println("Enter Recipient Account Number");
                    String account2 = kbd.next();

                    System.out.println("Enter Customer Account Number");
                    String account1 = kbd.next();
                    System.out.println("Enter Amount");
                    double amount = kbd.nextDouble();
                    customer.transferBetweenOwnAccounts(account1, account2, amount);
                }
            }
            if (choice==4){
                    List<Account> customeraccounts = customer.getAccounts();
                    int overdraftfees = 0;
                    for (Account account: customeraccounts){
                        overdraftfees += account.getOverdraftAccount();
                    }
                customer.viewOverdraftFees();
                if(customer.getTotalOverdraftFeesOwed()<=0){
                    System.out.println("no fees owed");

                }else {

                System.out.println("would u like to pay?? \n 1) yes \n 2)no");
                int pay = kbd.nextInt();
                if (pay == 1){

                    System.out.println("enter amount");
                    double amount= kbd.nextDouble();
                }
                }
            }
            if (choice == 5){
                boolean transactionmenu = true;
                while (transactionmenu){
                    System.out.println("1) View all transactions");
                    System.out.println("2) Filter transactions");
                    System.out.println("3) Sort transactions");
                    System.out.println("4) Back");

                    int transactionChoice = kbd.nextInt();

                    if (transactionChoice == 1) {

                        displayTransactions(
                                customer.getTransactionHistory()
                        );

                    }

                    else if (transactionChoice == 2) {

                        boolean filterMenu = true;

                        while (filterMenu) {

                            System.out.println(
                                    "\n========== FILTER TRANSACTIONS =========="
                            );

                            System.out.println("1) Transaction type");
                            System.out.println("2) Account");
                            System.out.println("3) Date");
                            System.out.println("4) Minimum amount");
                            System.out.println("5) Back");

                            int filterChoice = kbd.nextInt();

                            if (filterChoice == 1) {

                                System.out.println(
                                        "\n===== TRANSACTION TYPE ====="
                                );

                                System.out.println("1) Withdraw");
                                System.out.println("2) Deposit");
                                System.out.println("3) Transfer");
                                System.out.println("4) Own Transfer");

                                int typeChoice = kbd.nextInt();

                                TransactionType type = null;

                                if (typeChoice == 1) {
                                    type = TransactionType.Withdraw;
                                }
                                else if (typeChoice == 2) {
                                    type = TransactionType.Deposit;
                                }
                                else if (typeChoice == 3) {
                                    type = TransactionType.Transfer;
                                }
                                else if (typeChoice == 4) {
                                    type = TransactionType.OwnTransfer;
                                }
                                else {
                                    System.out.println("Invalid choice.");
                                    continue;
                                }

                                displayTransactions(
                                        customer.filterTransactionsByType(type)
                                );
                            }

                            else if (filterChoice == 2) {

                                System.out.print(
                                        "Enter account number: "
                                );

                                String accountNumber = kbd.next();

                                displayTransactions(
                                        customer.filterTransactionsByAccount(
                                                accountNumber
                                        )
                                );
                            }

                            else if (filterChoice == 3) {

                                System.out.print(
                                        "Enter date (YYYY-MM-DD): "
                                );

                                String dateInput = kbd.next();

                                try {

                                    LocalDate date =
                                            LocalDate.parse(dateInput);

                                    displayTransactions(
                                            customer.filterTransactionsByDate(
                                                    date
                                            )
                                    );

                                } catch (Exception e) {

                                    System.out.println(
                                            "Invalid date."
                                    );
                                }
                            }

                            else if (filterChoice == 4) {

                                System.out.print(
                                        "Enter minimum amount: "
                                );

                                double amount = kbd.nextDouble();

                                displayTransactions(
                                        customer.filterTransactionsByAmount(
                                                amount
                                        )
                                );
                            }

                            else if (filterChoice == 5) {

                                filterMenu = false;
                            }

                            else {

                                System.out.println(
                                        "Invalid choice."
                                );
                            }
                        }
                    }

                    else if (transactionChoice == 3) {

                        boolean sortMenu = true;

                        while (sortMenu) {

                            System.out.println(
                                    "\n========== SORT TRANSACTIONS =========="
                            );

                            System.out.println("1) Newest first");
                            System.out.println("2) Oldest first");
                            System.out.println("3) Back");

                            int sortChoice = kbd.nextInt();

                            if (sortChoice == 1) {

                                displayTransactions(
                                        customer.getTransactionsNewestFirst()
                                );

                            }

                            else if (sortChoice == 2) {

                                displayTransactions(
                                        customer.getTransactionsOldestFirst()
                                );

                            }

                            else if (sortChoice == 3) {

                                sortMenu = false;

                            }

                            else {

                                System.out.println(
                                        "Invalid choice."
                                );
                            }
                        }
                    }

                    else if (transactionChoice == 4) {

                        transactionmenu = false;

                    }

                    else {

                        System.out.println(
                                "Invalid choice."
                        );
                    }
                }
            }
            if(choice == 6){
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
                String cpr;


                System.out.println("Enter CPR number");
                cpr = kbd.next();
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
                List<Customer> managedCustomers = banker.getManagedCustomers();

                for (Customer customer: managedCustomers){
                    System.out.println(
                            "{"+customer.userID + ","+
                                    customer.getName()+","+
                                    customer.getCPR()+"}"
                    );
                }
//                    System.out.println("Enter chosen customer");

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
                    System.out.println("1) transfer to other people \n2) transfer between accounts");
                    int transferChoice = kbd.nextInt();
                    if(transferChoice == 1){
                        System.out.println("Enter Recipient Customer ID");
                        String targetCustomerID = kbd.next();
                        System.out.println("Enter Recipient Account Number");
                        String targetAccNumber = kbd.next();
                        System.out.println("Enter Customer Customer ID");
                        String customerID = kbd.next();
                        System.out.println("Enter Customer Account Number");
                        String customerAcc = kbd.next();
                        System.out.println("Enter Amount");
                        double amount = kbd.nextDouble();
                        banker.transferToOtherAcc(targetCustomerID, targetAccNumber, customerID, customerAcc, amount);
                    }

                    if (transferChoice == 2){
                        System.out.println("Enter Recipient Customer ID");
                        String customerID = kbd.next();
                        System.out.println("Enter Recipient Account Number");
                        String account2 = kbd.next();

                        System.out.println("Enter Customer Account Number");
                        String account1 = kbd.next();
                        System.out.println("Enter Amount");
                        double amount = kbd.nextDouble();
                        banker.transferBetweenCustomerOwnAccounts(customerID, account1, account2, amount);
                    }
                }
            }if(choice == 4){
                logout();
            }
        }
    }

    public void displayTransactions(
            List<Transactions> transactions) {

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

            return;
        }

        System.out.println(
                "\n========== TRANSACTION HISTORY =========="
        );

        for (Transactions transaction : transactions) {

            System.out.println(
                    "ID: " +
                            transaction.getTransactionID() +

                            " | Account: " +
                            transaction.getAccountNumber() +

                            " | Date: " +
                            transaction.getTimeStamp() +

                            " | Type: " +
                            transaction.getType() +

                            " | Amount: " +
                            transaction.getAmount() +

                            " | Balance After: " +
                            transaction.getPostTransactionBalance()
            );
        }
    }


    public static void main(String[] args) {
        Main main = new Main();
        main.login();
    }
}