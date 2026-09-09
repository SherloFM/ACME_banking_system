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
        String filePath = "/Users.txt";
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (line.startsWith(userID + ",")){
                    return Optional.of(getRoleFromLine(userID));
                }
            }
        }catch (IOException e){
            System.out.println(e);
        }
        return Optional.empty();
    }
    private User getRoleFromLine(String line){
        String[] userData = line.split(",");
        Role role = userData[3].equals("Banker")? Role.Banker:Role.Customer;
        if(role == Role.Banker){
            return new Banker(userData[0],userData[1],userData[2],role,0,null);
        }else {
            return new Banker(userData[0],userData[1],userData[2],role,0,null);
        }
    }


    public void saveTransactionLog(String customerID, List<Transactions> transactions){

    }

//
//    public List<Transactions> loadTransactionLog(String customerID){
//
//    }
}
