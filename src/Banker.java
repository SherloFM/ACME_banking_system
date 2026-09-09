import java.lang.*;
import java.time.LocalDateTime;
import java.util.List;

public class Banker extends User{
    List<Customer> managedCustomers;

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

//    public Customer createCustomer(String customerName, String rawPassword){
//
//    }

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

    }

    @Override
    public void resetFailedAttempts() {

    }
}
