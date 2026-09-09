import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.OptionalInt;

public class FileManager {
    String bankDirectory, customer_Directory;


    public void saveCustomerData(Customer customer){

    }

    public void saveBankerData(Banker banker){

    }

    public Optional<User> loadUserData(String userID){
        Optional<User> theUser;
        String filePath = "Users.txt";
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            while (bufferedReader.readLine() != null){
                String[] user = bufferedReader.toString().split(",");
                if(user[0].equals(userID)){
                    if(user[3].equals("Banker")){
                        System.out.println("Banker");
                        Banker bankerUser = new Banker();
                        return Optional.of(bankerUser);
                    }else{
                        System.out.println("Customer");
                        Customer customerUser = new Customer();
                        customerUser.toString();
                        return Optional.of(customerUser);
                    }
                }

            }
        }catch (IOException e){
            System.out.println(e);
        }
        return Optional.empty();
    }

    public void saveTransactionLog(String customerID, List<Transactions> transactions){

    }


    public List<Transactions> loadTransactionLog(String customerID){

    }
}
