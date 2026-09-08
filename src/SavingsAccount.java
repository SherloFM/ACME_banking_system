import java.time.LocalDate;
import java.util.List;

public class SavingsAccount extends Account{

    double interestRate;

    public  double calculateMonthlyInterest(){

    }


    @Override
    boolean applyOverdraftRules(double requestedAmount) {
        boolean isAllowed = false;

        return false;
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
