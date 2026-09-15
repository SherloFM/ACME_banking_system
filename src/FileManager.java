import javax.smartcardio.Card;
import javax.swing.text.html.Option;
import java.io.*;
import java.nio.file.NoSuchFileException;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {
    HashMap<String, List<String>> bankerCustomer = new HashMap<>();

    public void saveUserData(Customer customer){
        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\Users.txt";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath,true))){

            writer.write(
                    customer.getUserID() +","+
                            customer.getEncryptedPassword() +","+
                            customer.getName()+","+
                            customer.getRole()

            );

            writer.newLine();
        }catch (IOException e){
            System.out.println(e);;
        }
    }
    public void createTransactionFile(String customerID) {

        String filepath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\Customers\\Customer-"
                        + customerID + ".txt";

        try {
            File file = new File(filepath);

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public void addCustomerToBanker(String bankerID,String customerID){
        loadBankerCustomer();
        if(!bankerCustomer.containsKey(bankerID)){
            bankerCustomer.put(bankerID,new ArrayList<>());
        }
        bankerCustomer.get(bankerID).add(customerID);

        saveBankerCustomer();
    }
    public void saveBankerCustomer(){
        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\BankerCustomer.txt";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            for(String bankerID: bankerCustomer.keySet()){
                writer.write(bankerID);
                for (String customerID: bankerCustomer.get(bankerID)){
                    writer.write(","+customerID);
                }

            writer.newLine();
            }
        }catch (IOException e){
            System.out.println(e);;
        }
    }

    public void loadBankerCustomer(){
        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\BankerCustomer.txt";

        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;

            while ((line = reader.readLine())!= null){
                String[] data = line.split(",");
                String banker = data[0];
                List<String> customers = new ArrayList<>();
                for (int i = 1; i < data.length; i++) {
                    customers.add(data[i]);
                }

                bankerCustomer.put(banker,customers);
            }

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public List<Customer> loadManagedCustomer(String bankerID){
            loadBankerCustomer();
            List<Customer> customers = new ArrayList<>();

            if(!bankerCustomer.containsKey(bankerID)){
                return customers;
            }

            for(String customerID: bankerCustomer.get(bankerID)){
                Optional<User> user =  loadUserData(customerID);
                if(user.isPresent()&&user.get() instanceof Customer){
                    customers.add((Customer) user.get());
                }
            }

            return customers;
    }

    public void saveBankerData(Banker banker){

    }

    public String generateCustomerID(){
        String fielpath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\Users.txt";

        int highestID = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(fielpath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split(",");
                int currentID = Integer.parseInt(userData[0]);
                if (currentID > highestID){
                    highestID = currentID;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int nextID = highestID+1;

        return String.format("%05d", nextID);
    }

    public String generateCardNumber() {
        String fielpath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        long highestNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fielpath))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split(",");
                long currentNumber = Long.parseLong(userData[7]);
                if (currentNumber > highestNumber){
                    highestNumber = currentNumber;
                }
            }
        } catch (FileNotFoundException e) {
            return "0000000000000001";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("%016d", highestNumber+1);
    }

    public String generateAccNumber(){
        String fielpath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        int highestAccNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fielpath))){
            String line;
            while ((line = reader.readLine()) != null){

                String[] userData = line.split(",");

                int currentAccNumber = Integer.parseInt(userData[1]);

                if (currentAccNumber > highestAccNumber){
                    highestAccNumber = currentAccNumber;
                }
            }
        } catch (FileNotFoundException e) {
            return "00001";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("%05d", highestAccNumber+1);
    }

    public String generateTransactionID(String customerID) {

        String filepath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\Customers\\Customer-"
                        + customerID + ".txt";

        int highestTransactionID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] transactionData = line.split(",");

                int currentTransactionID = Integer.parseInt(transactionData[0]);

                if (currentTransactionID > highestTransactionID) {
                    highestTransactionID = currentTransactionID;
                }
            }

        } catch (FileNotFoundException e) {

            // Customer has no transaction file yet
            return "00001";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("%05d", highestTransactionID + 1);
    }

    public void saveCustomerAccount(
            String customerID,
            String accountNumber,
            AccountType accountType,
            double balance,
            boolean isActive,
            int overdraftAccount,
            String cardNumber,
            CardType cardType
            ) {

        String filepath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filepath, true))) {
            writer.write(
                    customerID + "," +
                            accountNumber + "," +
                            accountType + "," +
                            balance + "," +
                            isActive + "," +
                            overdraftAccount + "," +
                            cardType+","+
                            cardNumber
            );

            writer.newLine();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Optional<User> loadUserData(String userID){
        String filePath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\Users.txt";
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (line.startsWith(userID + ",")){
                    return Optional.of(getRoleFromLine(line));
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }
        return Optional.empty();
    }

    public void loadCustomerAccounts(Customer customer){
        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath))){
            String line;

            while ((line = bufferedReader.readLine()) != null){

            String[] customerAccounts = line.split(",");
            String customerID = customerAccounts[0];
            if (!customerID.equals(customer.getUserID())){
                System.out.println("Invalid Customer ID");
                return;
            }

                String accountNumber = customerAccounts[1];
                AccountType accountType = AccountType.valueOf(customerAccounts[2]);
                double balance = Double.parseDouble(customerAccounts[3]);
                boolean isActive = Boolean.parseBoolean(customerAccounts[4]);
                int overdraftAccount = Integer.parseInt(customerAccounts[5]);

                CardType cardType = CardType.valueOf(customerAccounts[6]);
                String cardNumber = customerAccounts[7];

                Account account;

                if(accountType == AccountType.Checking){
                    account = new CheckingAccount(
                            accountNumber,
                            balance,
                            isActive,
                            overdraftAccount
                    );
                }else{
                    account = new SavingsAccount(
                            accountNumber,
                            balance,
                            isActive,
                            overdraftAccount
                    );
                }

                Mastercard mastercard;

                switch (cardType){
                    case Platinum -> mastercard = new PlatinumCard(cardNumber);
                    case Titanium -> mastercard = new TitaniumCard(cardNumber);
                    case Standard -> mastercard = new StandardCard(cardNumber);
                    default -> throw new IllegalArgumentException("Invalid Input");
                }

                account.setAssignedCard(mastercard);
                customer.addAccount(account);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private User getRoleFromLine(String line){
        String[] userData = line.split(",");
        Role role = userData[3].equals("Banker")? Role.Banker:Role.Customer;
        if(role == Role.Banker){
            return new Banker(userData[0],userData[1],userData[2],role,0,null);
        }else {
            return new Customer(userData[0],userData[1],userData[2],role,0,null);
        }
    }

    public Optional<Account> loadAccount(String customerID, String accNumber) {

        String filePath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 8) {
                    continue;
                }

                String storedCustomerID = data[0].trim();
                String accountNumber = data[1].trim();
                AccountType accountType = AccountType.valueOf(data[2].trim());
                double balance = Double.parseDouble(data[3].trim());
                boolean isActive = Boolean.parseBoolean(data[4].trim());
                int overdraftAccount = Integer.parseInt(data[5].trim());
                CardType cardType = CardType.valueOf(data[6].trim());
                String cardNumber = data[7].trim();

                if (storedCustomerID.equals(customerID)
                        && accountNumber.equals(accNumber)) {

                    Account account;

                    if (accountType == AccountType.Checking) {

                        account = new CheckingAccount(
                                accountNumber,
                                balance,
                                isActive,
                                overdraftAccount
                        );

                    } else if (accountType == AccountType.Saving) {

                        account = new SavingsAccount(
                                accountNumber,
                                balance,
                                isActive,
                                overdraftAccount
                        );

                    } else {
                        System.out.println("Invalid account type");
                        return Optional.empty();
                    }

                    Mastercard card;

                    if (cardType == CardType.Platinum) {

                        card = new PlatinumCard(cardNumber);

                    } else if (cardType == CardType.Titanium) {

                        card = new TitaniumCard(cardNumber);

                    } else if (cardType == CardType.Standard) {

                        card = new StandardCard(cardNumber);

                    } else {
                        System.out.println("Invalid card type");
                        return Optional.empty();
                    }

                    account.setAssignedCard(card);

                    return Optional.of(account);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data in CustomerAccounts.txt: " + e.getMessage());
        }

        return Optional.empty();
    }

    public void updateAccBalance(
            String customerID,
            String accNumber,
            double newBalance) {

        String filePath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        for (int i = 0; i < lines.size(); i++) {

            String[] data = lines.get(i).split(",");

            String storedCustomerID = data[0];
            String storedAccountNumber = data[1];

            if (storedCustomerID.equals(customerID)
                    && storedAccountNumber.equals(accNumber)) {

                data[3] = String.valueOf(newBalance);

                lines.set(i, String.join(",", data));

                break;
            }
        }

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath))) {

            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void saveTransaction(String customerID, Transactions transaction) {

        String filePath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\Customers\\Customer-"
                        + customerID + ".txt";

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath, true))) {

            writer.write(transaction.toFileString());
            writer.newLine();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public double getTodayWithdrawals(String customerID, String accNumber) {

        String filePath =
                "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\Customers\\Customer-"
                        + customerID + ".txt";



        double totalWithdrawn = 0;

        LocalDate today = LocalDate.now();

        File file = new File(filePath);
        if(!file.exists()){
            createTransactionFile(customerID);
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                String transactionAccount = data[1];
                LocalDateTime timestamp = LocalDateTime.parse(data[2]);
                String transactionType = data[3];
                double amount = Double.parseDouble(data[4]);

                if (transactionAccount.equals(accNumber)
                        && transactionType.equals("Withdraw")
                        && timestamp.toLocalDate().equals(today)) {

                    totalWithdrawn += amount;
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return totalWithdrawn;
    }
}
