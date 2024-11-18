package us.dot.its.jpo.ode.testUtilities;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApprovalTestCase {

    public String description;
    public String input;
    public String expected;

    public static List<ApprovalTestCase> deserializeTestCases(String path) throws IOException {
        return deserializeTestCases(path, "");
    }

    public static List<ApprovalTestCase> deserializeTestCases(String path, String startFlag) throws IOException {
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
            approvalTestCase.setInput(startFlag + input.toString()); // Add the 2-byte length prefix to the input

            approvalTestCase.setExpected(json.get("expected").toString());

            cases.add(approvalTestCase);
        }
        return cases;
    }
}
