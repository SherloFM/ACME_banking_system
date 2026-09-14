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
    TransactionType type;

    public Transactions(String transactionID, LocalDateTime timeStamp, TransactionType type, double amount, double postTransactionBalance) {
        this.transactionID = transactionID;
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
                timeStamp + ","+
                type+ "," +
                amount+ "," +
                postTransactionBalance
                ;
    }


}
