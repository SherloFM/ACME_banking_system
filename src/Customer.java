import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customer extends User{


    double totalOverdraftFeesOwed;
    List<Account> accounts = new ArrayList<>();

    public Customer(String userID, String encryptedPassword, String name, Role role, int failedLoginAttempts, LocalDateTime lockoutDatetime) {
        super(userID, encryptedPassword, name, role, failedLoginAttempts, lockoutDatetime);
    }



    public void addAccount(Account newAcc){
        accounts.add(newAcc);

    }

    public Optional<Account> getAccNumber(String accNumber){
        return getAccNumber(accNumber);
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
}
