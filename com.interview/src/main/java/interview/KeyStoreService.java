package interview;

import interview.botstrapengine.DataBaseBootService;
import interview.model.Employee;
import java.io.IOException;
import java.util.Scanner;
import org.json.simple.parser.ParseException;

public class KeyStoreService {

  public static void main(String[] args) throws IOException, ParseException {

    KeyStoreController keyStore = new KeyStoreController();
    KeyStoreController.internalMap = DataBaseBootService.bootStrapFromDisc();

    int option;
    String key, id, name;
    while (true) {
      try{
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter option 1:INSERT 2:DELETE 3:UPDATE 4:READ");
      option = sc.nextInt();
      if (option <= 0 || option > 4) {
        System.out.println("Wrong input try again");
        continue;
      } else {
        System.out.println("Enter Key , ID and name for employee");
        sc.nextLine();
      }
      key = sc.nextLine();
      id = sc.nextLine();
      name = sc.nextLine();

      keyStore.handleData(option, key, new Employee(id, name));
      }catch (Exception ex) {
        System.out.println("Please retry !!");
      }

    }
  }
}
