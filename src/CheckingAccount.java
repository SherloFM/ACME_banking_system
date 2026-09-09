import java.util.List;

public class CheckingAccount extends Account{


//    @Override
//    public boolean checkLimit(Enum<transactionType> transactionTypeEnum, double requestedAmount) {
//        return false;
//    }
//
//    @Override
//    public void recordUsage(Enum<transactionType> transactionTypeEnum, double transactionAmount) {
//
//    }

    @Override
    public void resetDailyLimitsIfNewDay() {

    }

    @Override
    public boolean test(Object o) {
        return false;
    }
}
