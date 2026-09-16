import java.time.LocalDate;
import java.util.Map;

enum CardType{
    Platinum,
    Titanium,
    Standard
}
abstract class Mastercard implements iLimitEnforcer{
    String cardNumber;
    Map<Transactions,Double> dailyUsage;
    LocalDate lastResetDate;
    CardType type;

    public Mastercard(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public abstract double getWithdrawLimit();

    public abstract double getDepositLimit();

    public abstract double getOwnDepositLimit();

    public abstract double getTransferLimit();

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


    public String getCardNumber() {
        return cardNumber;
    }


}
