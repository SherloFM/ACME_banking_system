import javax.smartcardio.Card;
import java.util.Map;

public class PlatinumCard extends Mastercard{


    private final double withdrawLimit = 20000;
    private final double transferLimit = 40000;
    private final double ownTransferLimit = 80000;
    private final double depositLimit = 100000;
    private final double ownDepositLimit = 200000;

    public PlatinumCard(String cardNumber) {
        super(cardNumber);
        this.type = CardType.Platinum;

    }


    @Override
    public void resetDailyLimitsIfNewDay() {

    }
}
