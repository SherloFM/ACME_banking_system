import java.util.List;

public class CheckingAccount extends Account{

    @Override
    boolean applyOverdraftRules(double requestedAmount) {
        boolean isAllowed = false;
        return isAllowed;
    }

    @Override
    public List<Transaction> filterTransactions() {
        return List.of();
    }

    @Override
    public boolean checkLimit(Enum<transactionType> transactionTypeEnum, double requestedAmount) {
        return false;
    }

    @Override
    public void recordUsage(Enum<transactionType> transactionTypeEnum, double transactionAmount) {

    }

    @Override
    public void resetDailyLimitsIfNewDay() {

    }

    @Override
    public boolean test(Object o) {
        return false;
    }

}
