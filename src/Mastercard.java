import java.time.LocalDate;
import java.util.Map;

abstract class Mastercard implements iLimitEnforcer{
    String cardNumber;
    Map<Transactions,Double> dailyLimits;
    Map<Transactions,Double> dailyUsage;
    LocalDate lastResetDate;

    public String getCardNumber() {
        return cardNumber;
    }


}
