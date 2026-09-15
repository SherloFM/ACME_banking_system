import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

enum TransactionType{
    Withdraw,
    Deposit,
    Transfer,
}

public class Transactions implements Comparable<Transactions>, Serializable {
    String transactionID;
    LocalDateTime timeStamp;
    String accountNumber;
    TransactionType type;

    public String getAccountNumber() {
        return accountNumber;
    }

    public Transactions(String transactionID, String accountNumber, LocalDateTime timeStamp, TransactionType type, double amount, double postTransactionBalance) {
        this.transactionID = transactionID;
        this.accountNumber = accountNumber;
        this.timeStamp = timeStamp;
        this.type = type;
        this.amount = amount;
        this.postTransactionBalance = postTransactionBalance;
    }

    double amount;
    double postTransactionBalance;

    public int compareTo(Transactions otherTransaction){

        return this.timeStamp.compareTo(otherTransaction.timeStamp);
    }

    public String getTransactionID() {
        return transactionID;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getPostTransactionBalance() {
        return postTransactionBalance;
    }

    public String toFileString(){

        return transactionID + ","+
                accountNumber+","+
                timeStamp + ","+
                type+ "," +
                amount+ "," +
                postTransactionBalance
                ;
    }


}
