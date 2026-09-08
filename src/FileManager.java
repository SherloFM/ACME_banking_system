import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class FileManager {
    String bankDirectory, customer_Directory;

    public void saveCustomerData(Customer customer){

    }

    public void saveBankerData(Banker banker){

    }

    public void saveTransactionLog(String customerID, List<Transactions> transactions){

    }

    public Optional<Customer> loadCustomer(String customerID){

    }

    public Optional<Customer> loadBanker(String customerID){

    }

    public List<Transactions> loadTransactionLog(String customerID){

    }
}
