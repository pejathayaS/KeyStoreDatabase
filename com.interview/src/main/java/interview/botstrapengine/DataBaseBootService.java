package interview.botstrapengine;

import static interview.botstrapengine.Constants.keyValueStorageMaster;
import static interview.botstrapengine.Constants.readOnlyFilePath;
import static java.nio.charset.StandardCharsets.UTF_8;

import interview.model.Employee;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class DataBaseBootService {

  /**
   * Called everytime application starts . Makes sure
   * Append only data is read and propertly written to the main DB.
   * Also populates the hashmap with the updated data
   * @return
   * @throws IOException
   * @throws ParseException
   */
  public static Map<String,Employee> bootStrapFromDisc() throws IOException, ParseException {
    HashMap<String, Employee> map = readDataFromMainStorage();

    // Read data from temp storage and update the hashmap entries.
    // This can be further optimised based on bottom up appraach
    File file = new File(readOnlyFilePath);
    if (file.exists()) {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      String dataToPrcess, operation, key, empID, name;

      while ((dataToPrcess = br.readLine()) != null) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(dataToPrcess);
        if (jsonObject == null) continue;
        operation = jsonObject.get("Operation").toString();
        key = jsonObject.get("Key").toString();
        empID = jsonObject.get("empId") == null ? null : jsonObject.get("empId").toString();
        name = jsonObject.get("name") == null ? null : jsonObject.get("name").toString();

        if (operation.equals("INSERT") || operation.equals("CREATE")) {
          map.put(key, new Employee(empID, name));
        } else if (operation.equals("DELETE")) {
          map.remove(key);
        }
      }
    }
    writeToMainDataStorage(map);
    return map;
  }

  /**
   * This method takes care of writing the data into the storage. This is one time activity during boostrap
   * @param map
   * @throws IOException
   */
  private static void writeToMainDataStorage(HashMap<String, Employee> map) throws IOException {
    JSONObject employeeObject = new JSONObject();
    Iterator<Entry<String, Employee>> iterator = map.entrySet().iterator();
    Employee employee;
    boolean check = Files.deleteIfExists(Paths.get(keyValueStorageMaster));
    File file=new File(keyValueStorageMaster);
    while (iterator.hasNext()) {
      Map.Entry mapElement = iterator.next();
      employeeObject.put("Key", mapElement.getKey());
      employee = (Employee) mapElement.getValue();
      if (employee == null) continue;
      employeeObject.put("empId", employee.getId());
      employeeObject.put("name", employee.getName());

      Files.write(
          Paths.get(keyValueStorageMaster),
          (employeeObject.toJSONString() + System.lineSeparator()).getBytes(UTF_8),
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
      }
    }

  /**
   * Reads the data from storage and updates the internal hashmap
   * @return
   * @throws IOException
   */
  private static HashMap<String, Employee> readDataFromMainStorage() throws IOException {
    HashMap<String, Employee> map = new HashMap<>();
    File file = new File(keyValueStorageMaster); // creates a new file instance
    if(!file.exists()) return map;
    FileReader fr = new FileReader(file); // reads the file
    BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
    String dataToPrcess, key, empID, name;

    while ((dataToPrcess = br.readLine()) != null) {
      JSONObject jsonObject = (JSONObject) JSONValue.parse(dataToPrcess);
      key = jsonObject.get("Key").toString();
      empID = jsonObject.get("empId").toString();
      name = jsonObject.get("name").toString();

      map.put(key, new Employee(empID, name));
    }
    fr.close();
    return map;
  }


}
