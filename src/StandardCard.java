import java.util.Map;

public class StandardCard extends Mastercard{

    private final double withdrawLimit = 5000;
    private final double transferLimit = 10000;
    private final double ownTransferLimit = 20000;
    private final double depositLimit = 100000;
    private final double ownDepositLimit = 200000;

    public StandardCard(String cardNumber) {
        super(cardNumber);
        this.type = CardType.Standard;
    }

    @Override
    public void resetDailyLimitsIfNewDay() {

    }

    @Override
    public double getWithdrawLimit() {
        return withdrawLimit;
    }

    @Override
    public double getDepositLimit() {
        return depositLimit;
    }

    @Override
    public double getTransferLimit() {
        return transferLimit;
    }

    @Override
    public double getOwnDepositLimit() {
        return ownDepositLimit;
    }

}
