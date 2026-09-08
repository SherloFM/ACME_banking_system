import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transactions implements Comparable<Transactions>, Serializable {
    String transactionID;
    LocalDateTime timeStamp;
    TransactionType type;
    double amount;
    double postTransactionBalance;

    public int compareTo(Transactions otherTransaction){
        return 0;
    }

    public String toFileString(){
        return "";
    }

}
