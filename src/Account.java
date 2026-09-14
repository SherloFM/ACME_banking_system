import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

enum AccountType{
    Checking,
    Saving
}

abstract class Account implements iFilterable, iLimitEnforcer, Serializable {
    String accNumber;
    double Balance;
    boolean isActive;
    int overdraftAccount;
    Mastercard assignedCard;
    AccountType accountType;

    public Account(String accNumber, double balance, boolean isActive, int overdraftAccount) {
        this.accNumber = accNumber;
        Balance = balance;
        this.isActive = isActive;
        this.overdraftAccount = overdraftAccount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public double getBalance() {
        return Balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getOverdraftAccount() {
        return overdraftAccount;
    }

    public Mastercard getAssignedCard() {
        return assignedCard;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public void setBalance(double balance) {
        Balance = balance;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setOverdraftAccount(int overdraftAccount) {
        this.overdraftAccount = overdraftAccount;
    }

    public void setAssignedCard(Mastercard assignedCard) {
        this.assignedCard = assignedCard;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    List<Transactions> transactions = new ArrayList<>();

    public boolean deposit(double amount){
        boolean isSuccess = false;
        if(amount>0){
            Balance += amount;
            isSuccess = true;
        }
        return isSuccess;
    }

    public boolean withdraw(double amount){
        double balance = getBalance();
        if(amount>balance || amount<0){
            return false;
        }
        balance -= amount;
        setBalance(balance);
        return true;
    }

    public boolean transfer(Account target, double amount){
        boolean isSuccess = false;

        return isSuccess;
    }

    public String generalStatement(){
        return Balance + transactions.toString();
    }

    boolean applyOverdraftRules(double requestedAmount){
        return true;
    }
//
    public void addTransaction(Transactions transaction){
        transactions.add(transaction);
    }


}
