            import javax.print.attribute.standard.ColorSupported;
            import javax.smartcardio.Card;
            import javax.swing.text.html.Option;
            import java.io.IOException;
            import java.lang.*;
            import java.time.LocalDateTime;
            import java.util.ArrayList;
            import java.util.List;
            import java.util.Optional;

            public class Banker extends User{
                List<Customer> managedCustomers = new ArrayList<>();


                public Banker(String userID, String encryptedPassword, String name, Role role, int failedLoginAttempts, LocalDateTime lockoutDatetime, String CPR) {
                    super(userID, encryptedPassword, name, role, failedLoginAttempts, lockoutDatetime,CPR);
                }

                @Override
                public Role getRole() {
                    return super.getRole();
                }

                public List<Customer> getManagedCustomers() {
                    loadManagedCustomers();
                    return managedCustomers.stream().toList();
                }

                public Customer createCustomer(String customerName, String rawPassword, AccountType accType, CardType cardType, String cpr){
                    FileManager fileManager = new FileManager();
                    Account account;
                    Mastercard mastercard;
                    Optional<Customer> existingCustomer= fileManager.findUserByCPR(cpr);
                    String customerID;
                    Customer customer;

                    if(existingCustomer.isPresent()){
                        customer= existingCustomer.get();

                        if (!customer.getName().equalsIgnoreCase(customerName)) {

                            System.out.println(
                                    "CPR already exists, but the name does not match."
                            );

                            System.out.println(
                                    "Stored name: " + customer.getName()
                            );

                            System.out.println(
                                    "Entered name: " + customerName
                            );

                            return null;
                        }

                        customerID = customer.getUserID();

                        System.out.println(
                                "Existing customer found."
                        );

                    }else {
                        customerID = fileManager.generateCustomerID();
                        String hashedPassword = encryptPassowrd(rawPassword);

                        customer = new Customer(
                                customerID,
                                hashedPassword,
                                customerName,
                                Role.Customer,
                                0,
                                null,
                                cpr
                        );

                        fileManager.saveUserData(customer);
                        fileManager.addCustomerToBanker(this.userID, customerID);
                        managedCustomers.add(customer);

                        System.out.println(
                                "New customer created."
                        );

                        System.out.println(
                                "New UserID: " + customerID
                        );
                    }

                    String accNumber = fileManager.generateAccNumber();
                    switch (accType){
                        case Checking:
                            account = new CheckingAccount(accNumber, 0,true,0);
                            break;

                        case Saving:
                            account = new SavingsAccount(accNumber, 0,true,0);
                            break;

                        default:
                            throw new IllegalArgumentException("Invalid Account Type");
                    }

                    String cardNumber = fileManager.generateCardNumber();
                    switch (cardType){
                        case Platinum:
                            mastercard = new PlatinumCard(cardNumber);
                            break;
                        case Titanium:
                            mastercard = new TitaniumCard(cardNumber);
                            break;
                        case Standard:
                            mastercard = new StandardCard(cardNumber);
                            break;
                        default:
                            throw new IllegalArgumentException("Wrong Card Type");
                    }
                    account.setAccountType(accType);
                    customer.addAccount(account);
                    fileManager.saveUserData(customer);
                    fileManager.saveCustomerAccount(customerID, accNumber, accType, 0, true,0,cardNumber,cardType, cpr);
                    return customer;
                }

                public void loadManagedCustomers(){
                    FileManager fileManager = new FileManager();
                    managedCustomers.clear();

                    managedCustomers.addAll(fileManager.loadManagedCustomer(this.userID));
                }

                public void withdrawFromUser(String customerID, String accNumber, double amount) {

                    FileManager fileManager = new FileManager();

                    Optional<Account> accountOptional =
                            fileManager.loadAccount(customerID, accNumber);

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

                    double withdrawLimit = card.getWithdrawLimit();

                    double currentWithdrawals =
                            fileManager.getTodayWithdrawals(customerID, accNumber);

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

                    // Update CustomerAccounts.txt
                    fileManager.updateAccBalance(
                            customerID,
                            accNumber,
                            newBalance
                    );

                    // Generate transaction
                    Transactions transaction = new Transactions(
                            fileManager.generateTransactionID(customerID),
                            accNumber,
                            LocalDateTime.now(),
                            TransactionType.Withdraw,
                            amount,
                            newBalance
                    );

                    // Save transaction
                    fileManager.saveTransaction(
                            customerID,
                            transaction
                    );

                    System.out.println("withdraw done");
                    System.out.println("new balance " + newBalance);
                }
                public void deposittoCurrentUser(String customerID, String accNumber, double amount) {

                    FileManager fileManager = new FileManager();

                    Optional<Account> accountOptional =
                            fileManager.loadAccount(customerID, accNumber);

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

                    double depositLimit = card.getOwnDepositLimit();

                    double currentDepositLimit =
                            fileManager.getTodayDeposits(customerID, accNumber);

                    if (currentDepositLimit + amount > depositLimit) {

                        System.out.println("Deposit exceeds your daily limit");
                        System.out.println("Card Type: " + card.getType());
                        System.out.println("Daily Limit: " + depositLimit);
                        System.out.println("Already deposited today: " + currentDepositLimit);
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
                            customerID,
                            accNumber,
                            newBalance
                    );

                    // Generate transaction
                    Transactions transaction = new Transactions(
                            fileManager.generateTransactionID(customerID),
                            accNumber,
                            LocalDateTime.now(),
                            TransactionType.Deposit,
                            amount,
                            newBalance
                    );

                    // Save transaction
                    fileManager.saveTransaction(
                            customerID,
                            transaction
                    );

                    System.out.println("Deposit done");
                    System.out.println("Card Type: " + card.getType());
                    System.out.println("Amount deposited: " + amount);
                    System.out.println("Remaining daily limit: "
                            + (depositLimit - currentDepositLimit - amount));
                    System.out.println("New balance: " + newBalance);
                }

                public void deposittoOtherCustmer(String targetCustomerID, String targerAccNumber, double amount) {

                    FileManager fileManager = new FileManager();

                    Optional<Account> accountOptional =
                            fileManager.loadAccount(targetCustomerID, targerAccNumber);

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
                            fileManager.getTodayDeposits(targetCustomerID, targerAccNumber);

                    if (currentDepositLimit + amount > depositLimit) {

                        System.out.println("Deposit exceeds your daily limit");
                        System.out.println("Card Type: " + card.getType());
                        System.out.println("Daily Limit: " + depositLimit);
                        System.out.println("Already deposit today: " + currentDepositLimit);
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
                            targetCustomerID,
                            targerAccNumber,
                            newBalance
                    );

                    // Generate transaction
                    Transactions transaction = new Transactions(
                            fileManager.generateTransactionID(targetCustomerID),
                            targerAccNumber,
                            LocalDateTime.now(),
                            TransactionType.Deposit,
                            amount,
                            newBalance
                    );

                    // Save transaction
                    fileManager.saveTransaction(
                            targetCustomerID,
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
                        String tartgetCustomerID,
                        String targetAccNumber,
                        String customerID,
                        String custmerAcc,
                        double amount) {

                    FileManager fileManager = new FileManager();

                    if(amount<=0){
                        System.out.println("transfer must be greater than 0");
                        return;
                    }
                    if(customerID.equals(tartgetCustomerID)){
                        System.out.println("this is your other account");
                        System.out.println("use own deposit");
                        return;
                    }
                    Optional<Account> targetAccountOptional =
                            fileManager.loadAccount(tartgetCustomerID, targetAccNumber);

                    Optional<Account> senderAccountOptional = fileManager.loadAccount(customerID,custmerAcc);

                    if (targetAccountOptional.isEmpty()) {
                        System.out.println("Invalid Account");
                        return;
                    }
                    if (senderAccountOptional.isEmpty()) {
                        System.out.println("Invalid Account");
                        return;
                    }

                    Account senderAccount = senderAccountOptional.get();
                    Account targetAccount = targetAccountOptional.get();

                    Mastercard targetcard = targetAccount.getAssignedCard();
                    Mastercard sendercard = senderAccount.getAssignedCard();

                    if (sendercard == null) {
                        System.out.println("Sender account has no card");
                        return;
                    }
                    if (targetAccount == null) {
                        System.out.println("Sender account has no card");
                        return;
                    }


                    double transferLimit = sendercard.getTransferLimit();

                    double currentTransferLimit =
                            fileManager.getTodayTransfers(tartgetCustomerID, targetAccNumber);

                    if (currentTransferLimit + amount > transferLimit) {

                        System.out.println("Transfer exceeds your daily limit");
                        System.out.println("Card Type: " + sendercard.getType());
                        System.out.println("Daily Limit: " + transferLimit);
                        System.out.println("Already transfered today: " + currentTransferLimit);
                        System.out.println("Remaining limit: "
                                + (transferLimit - currentTransferLimit));

                        return;
                    }

                    if (senderAccount.getBalance()< amount) {
                        System.out.println("Insufficient Balance");
                        return;
                    }

                    if(!senderAccount.transfer(senderAccount,targetAccount,amount)){
                        System.out.println("Transfer Failed");
                        return;
                    }

                    double senderNewBalance = senderAccount.getBalance();
                    double targetNewBalance = targetAccount.getBalance();



                    // Update CustomerAccounts.txt
                    fileManager.updateAccBalance(
                            tartgetCustomerID,
                            targetAccNumber,
                            targetNewBalance
                    );

                    fileManager.updateAccBalance(
                            customerID,
                            custmerAcc,
                            senderNewBalance
                    );

                    // Generate transaction
                    Transactions senderTransaction = new Transactions(
                            fileManager.generateTransactionID(tartgetCustomerID),
                            custmerAcc,
                            LocalDateTime.now(),
                            TransactionType.Transfer,
                            amount,
                            senderNewBalance
                    );
                    Transactions targetTransaction = new Transactions(
                            fileManager.generateTransactionID(tartgetCustomerID),
                            targetAccNumber,
                            LocalDateTime.now(),
                            TransactionType.Deposit,
                            amount,
                            targetNewBalance
                    );

                    // Save transaction
                    fileManager.saveTransaction(
                            tartgetCustomerID,
                            targetTransaction
                    );

                    System.out.println("Transfer successful");
                    System.out.println("From Customer: " + customerID);
                    System.out.println("From Account: " + custmerAcc);
                    System.out.println("To Customer: " + tartgetCustomerID);
                    System.out.println("To Account: " + targetAccNumber);
                    System.out.println("Amount transferred: " + amount);
                    System.out.println("Transfer limit: " + transferLimit);
                    System.out.println("Transferred today: "
                            + (currentTransferLimit + amount));
                    System.out.println("Remaining transfer limit: "
                            + (transferLimit - currentTransferLimit - amount));
                    System.out.println("Sender new balance: "
                            + senderNewBalance);
                }

                public void transferBetweenCustomerOwnAccounts(
                        String customerID,
                        String sourceAccNumber,
                        String targetAccNumber,
                        double amount) {

                    FileManager fileManager = new FileManager();

                    // 1. Validate amount
                    if (amount <= 0) {
                        System.out.println("Transfer amount must be greater than 0.");
                        return;
                    }

                    // 2. Make sure the source and target accounts are different
                    if (sourceAccNumber.equals(targetAccNumber)) {
                        System.out.println("Source and target accounts cannot be the same.");
                        return;
                    }

                    // 3. Load source account
                    Optional<Account> sourceAccountOptional =
                            fileManager.loadAccount(customerID, sourceAccNumber);

                    if (sourceAccountOptional.isEmpty()) {
                        System.out.println("Source account not found.");
                        return;
                    }

                    // 4. Load target account
                    Optional<Account> targetAccountOptional =
                            fileManager.loadAccount(customerID, targetAccNumber);

                    if (targetAccountOptional.isEmpty()) {
                        System.out.println("Target account not found.");
                        return;
                    }

                    Account sourceAccount = sourceAccountOptional.get();
                    Account targetAccount = targetAccountOptional.get();

                    // 5. Make sure both accounts are active
                    if (!sourceAccount.isActive()) {
                        System.out.println("Source account is inactive.");
                        return;
                    }

                    if (!targetAccount.isActive()) {
                        System.out.println("Target account is inactive.");
                        return;
                    }

                    // 6. Get the source account's card
                    Mastercard sourceCard = sourceAccount.getAssignedCard();

                    if (sourceCard == null) {
                        System.out.println("Source account has no card.");
                        return;
                    }

                    // 7. Get OWN transfer limit
                    double ownTransferLimit =
                            sourceCard.getOwnTransferLimit();

                    // 8. Get today's own-account transfers
                    double currentOwnTransfers =
                            fileManager.getTodayOwnTransfers(
                                    customerID,
                                    sourceAccNumber
                            );

                    // 9. Check daily own-transfer limit
                    if (currentOwnTransfers + amount > ownTransferLimit) {

                        System.out.println("Transfer exceeds your daily own-account transfer limit.");
                        System.out.println("Card Type: " + sourceCard.getType());
                        System.out.println("Daily Own Transfer Limit: "
                                + ownTransferLimit);
                        System.out.println("Transferred between own accounts today: "
                                + currentOwnTransfers);
                        System.out.println("Remaining limit: "
                                + (ownTransferLimit - currentOwnTransfers));

                        return;
                    }

                    // 10. Check source balance
                    if (sourceAccount.getBalance() < amount) {
                        System.out.println("Insufficient Balance.");
                        return;
                    }

                    // 11. Actually transfer the money
                    if (!sourceAccount.transfer(
                            sourceAccount,
                            targetAccount,
                            amount)) {

                        System.out.println("Transfer Failed.");
                        return;
                    }

                    // 12. Get new balances
                    double sourceNewBalance =
                            sourceAccount.getBalance();

                    double targetNewBalance =
                            targetAccount.getBalance();

                    // 13. Update source account balance
                    fileManager.updateAccBalance(
                            customerID,
                            sourceAccNumber,
                            sourceNewBalance
                    );

                    // 14. Update target account balance
                    fileManager.updateAccBalance(
                            customerID,
                            targetAccNumber,
                            targetNewBalance
                    );

                    // 15. Create transaction for SOURCE account
                    Transactions sourceTransaction =
                            new Transactions(
                                    fileManager.generateTransactionID(customerID),
                                    sourceAccNumber,
                                    LocalDateTime.now(),
                                    TransactionType.OwnTransfer,
                                    amount,
                                    sourceNewBalance
                            );

                    // 16. Create transaction for TARGET account
                    Transactions targetTransaction =
                            new Transactions(
                                    fileManager.generateTransactionID(customerID),
                                    targetAccNumber,
                                    LocalDateTime.now(),
                                    TransactionType.Deposit,
                                    amount,
                                    targetNewBalance
                            );

                    // 17. Save source transaction
                    fileManager.saveTransaction(
                            customerID,
                            sourceTransaction
                    );

                    // 18. Save target transaction
                    fileManager.saveTransaction(
                            customerID,
                            targetTransaction
                    );

                    // 19. Display result
                    System.out.println("Transfer successful.");
                    System.out.println("Customer ID: " + customerID);
                    System.out.println("From Account: " + sourceAccNumber);
                    System.out.println("To Account: " + targetAccNumber);
                    System.out.println("Amount transferred: " + amount);
                    System.out.println("Card Type: " + sourceCard.getType());
                    System.out.println("Own Transfer Limit: "
                            + ownTransferLimit);
                    System.out.println("Transferred between own accounts today: "
                            + (currentOwnTransfers + amount));
                    System.out.println("Remaining Own Transfer Limit: "
                            + (ownTransferLimit - currentOwnTransfers - amount));
                    System.out.println("Source New Balance: "
                            + sourceNewBalance);
                    System.out.println("Target New Balance: "
                            + targetNewBalance);
                }

                public String viewCustomerDetails(String customerID){
                    String customerDetails = "";

                    return customerDetails;
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

                public void activateCustomerAccount(
                        String customerID,
                        String accNumber) {

                    FileManager fileManager = new FileManager();

                    Optional<Account> accountOptional =
                            fileManager.loadAccount(customerID, accNumber);

                    if (accountOptional.isEmpty()) {
                        System.out.println("Invalid account.");
                        return;
                    }

                    Account account = accountOptional.get();

                    if (account.isActive()) {
                        System.out.println("Account is already active.");
                        return;
                    }

                    account.setActive(true);

                    fileManager.updateAccountActiveStatus(
                            customerID,
                            accNumber,
                            true
                    );

                    System.out.println("Account activated successfully.");
                    System.out.println("Account: " + accNumber);
                }

                public void payCustomerOverdraftFees(
                        Customer customer,
                        double amount) {

                    if (customer == null) {
                        System.out.println("Customer not found.");
                        return;
                    }

                    customer.payOverdraftFees(amount);
                }

            }
