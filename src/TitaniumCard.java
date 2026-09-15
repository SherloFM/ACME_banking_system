import java.util.Map;

public class TitaniumCard extends Mastercard{

    private final double withdrawLimit = 10000;
    private final double transferLimit = 20000;
    private final double ownTransferLimit = 40000;
    private final double depositLimit = 100000;
    private final double ownDepositLimit = 200000;

    public TitaniumCard(String cardNumber) {
        super(cardNumber);
        this.type = CardType.Titanium;
    }

    @Override
    public void resetDailyLimitsIfNewDay() {

    }

    @Override
    public double getWithdrawLimit() {
        return withdrawLimit;
    }
}
