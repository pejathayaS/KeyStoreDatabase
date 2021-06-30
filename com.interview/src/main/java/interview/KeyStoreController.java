package interview;

import static interview.botstrapengine.Constants.readOnlyFilePath;
import static java.nio.charset.StandardCharsets.UTF_8;

import interview.model.Employee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public class KeyStoreController {

  public static Map<String, Employee> internalMap = new LinkedHashMap<>();

  /**
   * Insert the data into the DB
   *
   * @param key
   * @param employee
   * @throws IOException
   */
  public void insertData(String key, Employee employee) throws IOException {
    boolean errorFlag = false;
    try {
      if (internalMap.containsKey(employee)) {
        appendTheReadOnlyFile("UPDATE", key, employee);
        // perform UPDATE write to the file
      }
      appendTheReadOnlyFile("CREATE", key, employee);
      // perform ADD write to the read only file
    } catch (IOException ex) {
      errorFlag = true;
      // internalMap.remove(key);
      System.out.println("Insertion Failed . Please try again");
    } finally {
      if (!errorFlag) {
        internalMap.put(key, employee);
      }
    }
  }

  /**
   * Retrieves the data from DB
   *
   * @param key
   * @return
   */
  public Employee getData(String key) {
    if (internalMap.containsKey(key)) {
      return internalMap.get(key);
    }
    System.out.println("Employee with Key is not present in the database");
    return null;
  }

  /**
   * DELETE the data from DB
   *
   * @param key
   * @throws IOException
   */
  public void deleteData(String key) throws IOException {
    boolean errorFlag = false;
    try {
      if (internalMap.containsKey(key)) {
        appendTheReadOnlyFile("DELETE", key, null);
      } else {
        System.out.println("Employee with Key is not present in the database");
      }
    } catch (IOException ex) {
      errorFlag = true;
      System.out.println("Delete operation failed . Please try again");
    } finally {
      if (!errorFlag) {
        internalMap.remove(key);
        System.out.println("Data deleted with ID :" + key);
      }
    }
  }

  /***
   * This method updates the "append only" file with the operations.
   * @param operation
   * @param key
   * @param value
   * @throws IOException
   */
  public static void appendTheReadOnlyFile(String operation, String key, Employee value)
      throws IOException {
    JSONObject employeeObject = new JSONObject();
    employeeObject.put("Operation", operation);
    employeeObject.put("Key", key);

    if (value != null) {
      employeeObject.put("empId", value.getId());
      employeeObject.put("name", value.getName());
    }
    Files.write(
        Paths.get(readOnlyFilePath),
        (employeeObject.toJSONString() + System.lineSeparator()).getBytes(UTF_8),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  void handleData(int operation, String key, Employee emp) throws IOException {
    if (operation == 1 || operation == 3) {
      insertData(key, emp);
    } else if (operation == 2) {
      deleteData(key);
    } else if (operation == 4) {
      System.out.println(getData(key));
    } else {
      System.out.println("Wrong operatin : Please try again!");
    }
  }
}
