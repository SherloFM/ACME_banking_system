import java.util.List;
import java.util.function.Predicate;

public interface iFilterable {
    List<Transaction> filterTransactions(Predicate<Transaction>);
}
