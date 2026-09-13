import java.util.List;

public class CheckingAccount extends Account{


    public CheckingAccount(String accNumber, double balance, boolean isActive, int overdraftAccount) {
        super(accNumber, balance, isActive, overdraftAccount);
    }


    public boolean checkLimit(Enum<TransactionType> transactionTypeEnum, double requestedAmount) {
        return false;
    }


    public void recordUsage(Enum<TransactionType> transactionTypeEnum, double transactionAmount) {

    }

    @Override
    public void resetDailyLimitsIfNewDay() {

    }

    @Override
    public boolean test(Object o) {
        return false;
    }
}
