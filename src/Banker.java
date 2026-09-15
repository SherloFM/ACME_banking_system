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


        public Banker(String userID, String encryptedPassword, String name, Role role, int failedLoginAttempts, LocalDateTime lockoutDatetime) {
            super(userID, encryptedPassword, name, role, failedLoginAttempts, lockoutDatetime);
        }


        @Override
        public Role getRole() {
            return super.getRole();
        }

        public List<Customer> getManagedCustomers() {
            loadManagedCustomers();
            return managedCustomers.stream().toList();
        }

        public Customer createCustomer(String customerName, String rawPassword, AccountType accType, CardType cardType){
            FileManager fileManager = new FileManager();
            Account account;
            Mastercard mastercard;
            String customerID = fileManager.generateCustomerID();
            String hashedPassword = encryptPassowrd(rawPassword);

            Customer customer = new Customer(customerID,hashedPassword,customerName,Role.Customer,0,null) ;
            fileManager.addCustomerToBanker(this.userID, customerID);
            managedCustomers.add(customer);
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
            fileManager.saveCustomerAccount(customerID, accNumber, accType, 0, true,0,cardNumber,cardType );
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


    //    public boolean assignCardtoCustomerAcc(String customerID, String accNumber, CardType cardType){
    //        boolean isSuccess = false;
    //
    //        return isSuccess;
    //    }

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
    }
