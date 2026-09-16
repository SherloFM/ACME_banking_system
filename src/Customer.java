import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customer extends User{


    double totalOverdraftFeesOwed;
    List<Account> accounts = new ArrayList<>();

    public Customer(String userID, String encryptedPassword, String name, Role role, int failedLoginAttempts, LocalDateTime lockoutDatetime, String CPR) {
        super(userID, encryptedPassword, name, role, failedLoginAttempts, lockoutDatetime, CPR);
    }



    public void addAccount(Account newAcc){
        accounts.add(newAcc);

    }

    public Optional<Account> getAccNumber(String accNumber){


        return accounts.stream()
                .filter(account -> account.getAccNumber().equals(accNumber))
                .findFirst();
    }

    public void payOverdraftFees(double paymentAmount){

    }

    public boolean reactivateAcc(String accNumber){
        return true;
    }

    public List<Account> getAccounts(){
        return accounts;
    }

    @Override
    public boolean verifyPassword(String rawPassword, String storedHashedPassword) {
        return false;
    }

    @Override
    public boolean checkLockedStatus() {
        return false;
    }


    @Override
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        System.out.println(this.failedLoginAttempts);
        if(this.failedLoginAttempts >= 3){
            this.lockoutDatetime = LocalDateTime.now().plusMinutes(1);
        }
    }

    @Override
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockoutDatetime = null;
    }

    public void withdraw(double amount, String accNumber) {

        FileManager fileManager = new FileManager();

        Optional<Account> accountOptional =
                fileManager.loadAccount(userID, accNumber);

        if (accountOptional.isEmpty()) {
            System.out.println("no acc");
            return;
        }

        Account account = accountOptional.get();

        Mastercard card = account.getAssignedCard();

        if (card == null) {
            System.out.println("no card");
            return;
        }

        double withdrawLimit = card.getWithdrawLimit();

        double currentWithdrawals =
                fileManager.getTodayWithdrawals(userID, accNumber);

        if (currentWithdrawals + amount > withdrawLimit) {

            System.out.println("Withdrawal exceeds your daily limit");
            System.out.println("Card Type: " + card.getType());
            System.out.println("Daily Limit: " + withdrawLimit);
            System.out.println("Already withdrawn today: " + currentWithdrawals);
            System.out.println("Remaining limit: "
                    + (withdrawLimit - currentWithdrawals));

            return;
        }

        if (!account.withdraw(amount)) {
            System.out.println("withdraw failed");
            return;
        }

        double newBalance = account.getBalance();

        fileManager.updateAccBalance(
                userID,
                accNumber,
                newBalance
        );

        Transactions transaction = new Transactions(
                fileManager.generateTransactionID(userID),
                accNumber,
                LocalDateTime.now(),
                TransactionType.Withdraw,
                amount,
                newBalance
        );

        fileManager.saveTransaction(
                userID,
                transaction
        );

        System.out.println("Withdrawal successful");
        System.out.println("Card Type: " + card.getType());
        System.out.println("Amount withdrawn: " + amount);
        System.out.println("Remaining daily limit: "
                + (withdrawLimit - currentWithdrawals - amount));
        System.out.println("New balance: " + newBalance);
    }

    public void depositToPersonal(double amount, String accNumber) {

        FileManager fileManager = new FileManager();

        Optional<Account> accountOptional =
                fileManager.loadAccount(userID, accNumber);

        if (accountOptional.isEmpty()) {
            System.out.println("no acc");
            return;
        }

        Account account = accountOptional.get();

        Mastercard card = account.getAssignedCard();

        if (card == null) {
            System.out.println("no card");
            return;
        }

        double depositLimit = card.getOwnDepositLimit();


        double currentDeposit =
                fileManager.getTodayDeposits(userID, accNumber);

        if (currentDeposit + amount > depositLimit) {

            System.out.println("Deposit exceeds your daily limit");
            System.out.println("Card Type: " + card.getType());
            System.out.println("Daily Limit: " + depositLimit);
            System.out.println("Already deposited today: " + currentDeposit);
            System.out.println("Remaining limit: "
                    + (depositLimit - currentDeposit));

            return;
        }

        if (!account.deposit(amount)) {
            System.out.println("Deposit failed");
            return;
        }

        double newBalance = account.getBalance();

        fileManager.updateAccBalance(
                userID,
                accNumber,
                newBalance
        );

        Transactions transaction = new Transactions(
                fileManager.generateTransactionID(userID),
                accNumber,
                LocalDateTime.now(),
                TransactionType.Deposit,
                amount,
                newBalance
        );

        fileManager.saveTransaction(
                userID,
                transaction
        );

        System.out.println("Deposit successful");
        System.out.println("Card Type: " + card.getType());
        System.out.println("Amount withdrawn: " + amount);
        System.out.println("Remaining daily limit: "
                + (depositLimit - currentDeposit - amount));
        System.out.println("New balance: " + newBalance);
    }

    public void deposittoOtherUser(String tartgetCustomerID, String targetAccNumber, double amount) {

        FileManager fileManager = new FileManager();

        if(userID.equals(tartgetCustomerID)){
            System.out.println("This is your customer's other account");
            System.out.println("use own deposit");
            return;
        }
        Optional<Account> accountOptional =
                fileManager.loadAccount(tartgetCustomerID, targetAccNumber);

        if (accountOptional.isEmpty()) {
            System.out.println("Invalid Account");
            return;
        }

        Account account = accountOptional.get();

        Mastercard card = account.getAssignedCard();

        if (card == null) {
            System.out.println("Account has no card");
            return;
        }

        double depositLimit = card.getDepositLimit();

        double currentDepositLimit =
                fileManager.getTodayDeposits(tartgetCustomerID, targetAccNumber);

        if (currentDepositLimit + amount > depositLimit) {

            System.out.println("Withdrawal exceeds your daily limit");
            System.out.println("Card Type: " + card.getType());
            System.out.println("Daily Limit: " + depositLimit);
            System.out.println("Already withdrawn today: " + currentDepositLimit);
            System.out.println("Remaining limit: "
                    + (depositLimit - currentDepositLimit));

            return;
        }

        if (!account.deposit(amount)) {
            System.out.println("deposit failed");
            return;
        }

        double newBalance = account.getBalance();

        // Update CustomerAccounts.txt
        fileManager.updateAccBalance(
                tartgetCustomerID,
                targetAccNumber,
                newBalance
        );

        // Generate transaction
        Transactions transaction = new Transactions(
                fileManager.generateTransactionID(tartgetCustomerID),
                targetAccNumber,
                LocalDateTime.now(),
                TransactionType.Deposit,
                amount,
                newBalance
        );

        // Save transaction
        fileManager.saveTransaction(
                tartgetCustomerID,
                transaction
        );

        System.out.println("Deposit done");
        System.out.println("Card Type: " + card.getType());
        System.out.println("Amount deposited: " + amount);
        System.out.println("Remaining daily limit: "
                + (depositLimit - currentDepositLimit - amount));
        System.out.println("New balance: " + newBalance);
    }
}
