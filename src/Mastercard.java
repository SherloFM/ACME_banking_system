import java.time.LocalDate;
import java.util.Map;

enum CardType{
    Platinum,
    Titanium,
    Standard
}
abstract class Mastercard implements iLimitEnforcer{
    String cardNumber;
    Map<Transactions,Double> dailyLimits;
    Map<Transactions,Double> dailyUsage;
    LocalDate lastResetDate;
    CardType type;

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Map<Transactions, Double> getDailyLimits() {
        return dailyLimits;
    }

    public void setDailyLimits(Map<Transactions, Double> dailyLimits) {
        this.dailyLimits = dailyLimits;
    }

    public Map<Transactions, Double> getDailyUsage() {
        return dailyUsage;
    }

    public void setDailyUsage(Map<Transactions, Double> dailyUsage) {
        this.dailyUsage = dailyUsage;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(LocalDate lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Mastercard(CardType type, Map<Transactions, Double> dailyLimits, String cardNumber) {
        this.type = type;
        this.dailyLimits = dailyLimits;
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }


}
