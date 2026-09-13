import javax.print.attribute.standard.ColorSupported;
import javax.smartcardio.Card;
import java.io.IOException;
import java.lang.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return managedCustomers;
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

        switch (cardType){
            case Platinum:
                mastercard = new Platinum();


        }
        account.setAccountType(accType);
        customer.addAccount(account);
        System.out.println(managedCustomers.toString());
        fileManager.saveUserData(customer);
        fileManager.saveCustomerData(customer);
        return customer;
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
