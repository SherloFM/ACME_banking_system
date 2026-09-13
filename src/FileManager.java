import javax.swing.text.html.Option;
import java.io.*;
import java.nio.file.NoSuchFileException;
import java.security.MessageDigest;
import java.util.*;

public class FileManager {
    String bankDirectory, customer_Directory;
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
    public void saveCustomerData(Customer customer){
        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\Customers\\Customer-"
                + customer.getUserID() + ".txt";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            writer.write("Customer ID=" + customer.getUserID());
            writer.newLine();
            writer.write("Customer name="+ customer.getName());
            writer.newLine();
            for(Account account: customer.getAccounts()){
                writer.write("Account");
                writer.newLine();

                writer.write("accNumber=" + account.accNumber);
                writer.newLine();

                writer.write("balance=" + account.Balance);
                writer.newLine();

                writer.write("isActive=" + account.isActive);
                writer.newLine();

                writer.write("overdraftAccount=" + account.overdraftAccount);
                writer.newLine();

                writer.write("assignedCard=" +(account.assignedCard == null ? "null" : account.assignedCard.getCardNumber()));
                writer.newLine();
                for (Transactions transactions: account.getTransactions()){
                    writer.write(transactions.toFileString());
                    writer.newLine();
                }
            }
        }catch (IOException e){
            System.out.println(e);;
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
                writer.newLine();
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
                long currentNumber = Long.parseLong(userData[0]);
                if (currentNumber > highestNumber){
                    highestNumber = currentNumber;
                }
            }
        } catch (FileNotFoundException e) {
            return "0000000000000001";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.format("%016d", highestNumber);
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

    public void saveCustomerAccount(String customerID, String accountNumber, String cardNumber) {

        String filepath = "C:\\Users\\ahmed\\IdeaProjects\\ACME_banking_system\\src\\CustomerAccounts.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) {

            writer.write(
                    customerID + "," +
                            accountNumber + "," +
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
    private User getRoleFromLine(String line){
        String[] userData = line.split(",");
        Role role = userData[3].equals("Banker")? Role.Banker:Role.Customer;
        if(role == Role.Banker){
            return new Banker(userData[0],userData[1],userData[2],role,0,null);
        }else {
            return new Customer(userData[0],userData[1],userData[2],role,0,null);
        }
    }


    public void saveTransactionLog(String customerID, List<Transactions> transactions){

    }

//
//    public List<Transactions> loadTransactionLog(String customerID){
//
//    }
}
