package us.dot.its.jpo.ode.test.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a test case for an approval test, containing a description, input, and expected
 * output.
 */
@Getter
@Setter
public class ApprovalTestCase {

  public String description;
  public String input;
  public String expected;

  /**
   * Deserializes a list of approval test cases from a JSON file located at the specified path. The
   * JSON file is expected to have a structure where the test cases are contained in an array named
   * "cases". Each test case is represented by a JSON object that includes a "description", "input",
   * and "expected" field.
   *
   * @param path the file path to the JSON file containing the test cases
   * @return a list of ApprovalTestCase objects deserialized from the JSON file
   * @throws IOException if an I/O error occurs while reading the file
   */
  public static List<ApprovalTestCase> deserializeTestCases(String path) throws IOException {
    List<ApprovalTestCase> cases = new ArrayList<>();
    File file = new File(path);
    byte[] jsonData = Files.readAllBytes(file.toPath());
    JSONObject jsonObject = new JSONObject(new String(jsonData));

    JSONArray jsonArray = jsonObject.getJSONArray("cases");

    for (int i = 0; i < jsonArray.length(); i++) {
      ApprovalTestCase approvalTestCase = new ApprovalTestCase();
      JSONObject json = jsonArray.getJSONObject(i);

      approvalTestCase.setDescription(json.getString("description"));

      Object input = json.get("input");
      approvalTestCase.setInput(input.toString());

      approvalTestCase.setExpected(json.get("expected").toString());

      cases.add(approvalTestCase);
    }
    return cases;
  }
}
