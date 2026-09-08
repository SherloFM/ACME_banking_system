import java.io.Serializable;
import java.util.List;

abstract class Account implements iFilterable, iLimitEnforcer, Serializable {
    String accNumber;
    double Balance;
    boolean isActive;
    int overdraftAccount;
    Mastercard assignedCard;
    List<Transactions> transactions;

    public boolean deposit(double amount){
        boolean isSuccess = false;
        if(amount>0){
            Balance += amount;
            isSuccess = true;
        }
        return isSuccess;
    }

    public boolean withdraw(double amount){

        boolean isSuccess = false;

        if(amount>0 && amount<Balance){
            Balance -= amount;
            isSuccess = true;
        }

        return isSuccess;
    }

    public boolean transfer(Account target, double amount){
        boolean isSuccess = false;

    }

    public String generalStatement(){
        return Balance + transactions.toString();
    }

    abstract boolean applyOverdraftRules(double requestedAmount){
        return true;
    }

    public void addTransaction(transactionType type, double amount){

    }


}
