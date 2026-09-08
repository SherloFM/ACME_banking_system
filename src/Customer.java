import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class Customer extends User{
    List<Account> accounts;
    double totalOverdraftFeesOwed;

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
