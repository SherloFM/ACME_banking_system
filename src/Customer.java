import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Customer extends User{

    List<Transactions> transactions;

    double totalOverdraftFeesOwed = 0;
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

    public void transferToOtherAcc(
            String targetCustomerID,
            String targetAccNumber,
            String customerID,
            String customerAcc,
            double amount) {

        FileManager fileManager = new FileManager();

        // 1. Make sure this is actually another customer
        if (userID.equals(targetCustomerID)) {
            System.out.println("This is your own account.");
            System.out.println("Use transferBetweenOwnAccounts() instead.");
            return;
        }

        // 2. Validate amount
        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than 0.");
            return;
        }

        // 3. Load sender account
        Optional<Account> senderAccount =
                fileManager.loadAccount(customerID, customerAcc);

        if (senderAccount.isEmpty()) {
            System.out.println("Sender account not found.");
            return;
        }

        // 4. Load recipient account
        Optional<Account> targetAccount =
                fileManager.loadAccount(targetCustomerID, targetAccNumber);

        if (targetAccount.isEmpty()) {
            System.out.println("Recipient account not found.");
            return;
        }

        Account sender = senderAccount.get();
        Account target = targetAccount.get();

        // 5. Make sure both accounts are active
        if (!sender.isActive()) {
            System.out.println("Sender account is inactive.");
            return;
        }

        if (!target.isActive()) {
            System.out.println("Recipient account is inactive.");
            return;
        }

        // 6. Get the SENDER'S card
        Mastercard senderCard = sender.getAssignedCard();

        if (senderCard == null) {
            System.out.println("Sender account does not have an assigned card.");
            return;
        }

        // 7. Get the external transfer limit
        double transferLimit = senderCard.getTransferLimit();

        // 8. Get how much the sender has already transferred today
        double todayTransfers =
                fileManager.getTodayTransfers(customerID, customerAcc);

        // 9. Check daily limit
        if (todayTransfers + amount > transferLimit) {
            System.out.println("Transfer exceeds your daily transfer limit.");
            System.out.println("Daily limit: " + transferLimit);
            System.out.println("Transferred today: " + todayTransfers);
            System.out.println("Remaining: " + (transferLimit - todayTransfers));
            return;
        }

        // 10. Check sender balance
        if (sender.getBalance() < amount) {
            System.out.println("Insufficient balance.");
            return;
        }

        // 11. Actually move the money
        boolean successful =
                sender.transfer(sender, target, amount);

        if (!successful) {
            System.out.println("Transfer failed.");
            return;
        }

        // 12. Update sender balance in file
        fileManager.updateAccBalance(
                customerID,
                customerAcc,
                sender.getBalance()
        );

        // 13. Update recipient balance in file
        fileManager.updateAccBalance(
                targetCustomerID,
                targetAccNumber,
                target.getBalance()
        );

        // 14. Create sender transaction
        Transactions senderTransaction = new Transactions(
                fileManager.generateTransactionID(customerID),
                customerAcc,
                LocalDateTime.now(),
                TransactionType.Transfer,
                amount,
                sender.getBalance()
        );

        // 15. Save sender transaction
        fileManager.saveTransaction(
                customerID,
                senderTransaction
        );

        // 16. Create recipient transaction
        Transactions targetTransaction = new Transactions(
                fileManager.generateTransactionID(targetCustomerID),
                targetAccNumber,
                LocalDateTime.now(),
                TransactionType.Deposit,
                amount,
                target.getBalance()
        );

        // 17. Save recipient transaction
        fileManager.saveTransaction(
                targetCustomerID,
                targetTransaction
        );

        System.out.println("Transfer successful.");
        System.out.println("Transferred: " + amount);
        System.out.println("From account: " + customerAcc);
        System.out.println("To account: " + targetAccNumber);
    }


    public void transferBetweenOwnAccounts(
            String sourceAccNumber,
            String targetAccNumber,
            double amount) {

        FileManager fileManager = new FileManager();

        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than 0");
            return;
        }

        if (sourceAccNumber.equals(targetAccNumber)) {
            System.out.println("Source and target accounts cannot be the same");
            return;
        }

        Optional<Account> sourceAccountOptional =
                fileManager.loadAccount(
                        userID,
                        sourceAccNumber
                );

        Optional<Account> targetAccountOptional =
                fileManager.loadAccount(
                        userID,
                        targetAccNumber
                );

        if (sourceAccountOptional.isEmpty()) {
            System.out.println("Invalid source account");
            return;
        }

        if (targetAccountOptional.isEmpty()) {
            System.out.println("Invalid target account");
            return;
        }

        Account sourceAccount =
                sourceAccountOptional.get();

        Account targetAccount =
                targetAccountOptional.get();

        Mastercard sourceCard =
                sourceAccount.getAssignedCard();

        if (sourceCard == null) {
            System.out.println("Source account has no card");
            return;
        }

        // OWN ACCOUNT transfer limit
        double transferLimit =
                sourceCard.getOwnTransferLimit();

        // Only count OWN transfers
        double currentTransfers =
                fileManager.getTodayOwnTransfers(
                        userID,
                        sourceAccNumber
                );

        if (currentTransfers + amount > transferLimit) {

            System.out.println(
                    "Own-account transfer exceeds your daily limit"
            );

            System.out.println(
                    "Card Type: " + sourceCard.getType()
            );

            System.out.println(
                    "Daily Own-Transfer Limit: "
                            + transferLimit
            );

            System.out.println(
                    "Already transferred today: "
                            + currentTransfers
            );

            System.out.println(
                    "Remaining limit: "
                            + (transferLimit - currentTransfers)
            );

            return;
        }

        if (sourceAccount.getBalance() < amount) {
            System.out.println("Insufficient balance");
            return;
        }

        if (!sourceAccount.transfer(
                sourceAccount,
                targetAccount,
                amount)) {

            System.out.println("Transfer failed");
            return;
        }

        double sourceNewBalance =
                sourceAccount.getBalance();

        double targetNewBalance =
                targetAccount.getBalance();

        fileManager.updateAccBalance(
                userID,
                sourceAccNumber,
                sourceNewBalance
        );

        fileManager.updateAccBalance(
                userID,
                targetAccNumber,
                targetNewBalance
        );

        Transactions sourceTransaction =
                new Transactions(
                        fileManager.generateTransactionID(userID),
                        sourceAccNumber,
                        LocalDateTime.now(),
                        TransactionType.OwnTransfer,
                        amount,
                        sourceNewBalance
                );

        fileManager.saveTransaction(
                userID,
                sourceTransaction
        );

        Transactions targetTransaction =
                new Transactions(
                        fileManager.generateTransactionID(userID),
                        targetAccNumber,
                        LocalDateTime.now(),
                        TransactionType.Deposit,
                        amount,
                        targetNewBalance
                );

        fileManager.saveTransaction(
                userID,
                targetTransaction
        );

        System.out.println("Own-account transfer successful");
        System.out.println("From Account: " + sourceAccNumber);
        System.out.println("To Account: " + targetAccNumber);
        System.out.println("Amount transferred: " + amount);
        System.out.println("Daily Own-Transfer Limit: "
                + transferLimit);
        System.out.println("Transferred today: "
                + (currentTransfers + amount));
        System.out.println("Remaining limit: "
                + (transferLimit - currentTransfers - amount));
        System.out.println("New source balance: "
                + sourceNewBalance);
    }


    public double getTotalOverdraftFeesOwed() {

        double total = 0;

        for (Account account : accounts) {
            total += account.getOverdraftAccount();
        }

        return total;
    }

    public void payOverdraftFees(double amount) {

        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        double totalOwed = getTotalOverdraftFeesOwed();

        if (totalOwed <= 0) {
            System.out.println("You have no overdraft owed.");
            return;
        }

        if (amount > totalOwed) {
            System.out.println("Payment exceeds the amount owed.");
            System.out.println("Amount owed: " + totalOwed);
            return;
        }

        double remainingPayment = amount;

        for (Account account : accounts) {

            if (account.getOverdraftAccount() <= 0) {
                continue;
            }

            if (remainingPayment >= account.getOverdraftAccount()) {

                remainingPayment -= account.getOverdraftAccount();

                account.setOverdraftAccount(0);

            } else {

                account.setOverdraftAccount(
                        account.getOverdraftAccount()
                                - (int) remainingPayment
                );

                remainingPayment = 0;
            }

            if (remainingPayment == 0) {
                break;
            }
        }

        System.out.println("Overdraft payment successful.");
        System.out.println("Amount paid: " + amount);
        System.out.println(
                "Remaining overdraft owed: "
                        + getTotalOverdraftFeesOwed()
        );
    }

    public void viewOverdraftFees() {
        System.out.println("Fees owed: " + totalOverdraftFeesOwed);
    }

    public List<Transactions> getTransactionHistory() {

        FileManager fileManager = new FileManager();

        return fileManager.loadTransactions(getUserID());
    }

    public List<Transactions> filterTransactionsByType(
            TransactionType type) {

        return getTransactionHistory()
                .stream()
                .filter(t -> t.getType() == type)
                .toList();
    }

    public List<Transactions> filterTransactionsByAccount(
            String accountNumber) {

        return getTransactionHistory()
                .stream()
                .filter(t ->
                        t.getAccountNumber().equals(accountNumber))
                .toList();
    }

    public List<Transactions> filterTransactionsByDate(
            LocalDate date) {

        return getTransactionHistory()
                .stream()
                .filter(t ->
                        t.getTimeStamp()
                                .toLocalDate()
                                .equals(date))
                .toList();
    }

    public List<Transactions> filterTransactionsByAmount(
            double minimumAmount) {

        return getTransactionHistory()
                .stream()
                .filter(t ->
                        t.getAmount() >= minimumAmount)
                .toList();
    }

    public List<Transactions> getTransactionsNewestFirst() {

        return getTransactionHistory()
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public List<Transactions> getTransactionsOldestFirst() {

        return getTransactionHistory()
                .stream()
                .sorted()
                .toList();
    }

}
