import java.util.List;
import java.util.function.Predicate;

public interface iFilterable implements Predicate{
    List<Transaction> filterTransactions(Predicate<Transaction>);
}
