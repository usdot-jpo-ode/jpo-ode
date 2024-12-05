package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class TravelerDataFrameTest {
  ObjectMapper mapper = new ObjectMapper();

  @Test
  void deserializePreJ2735_2016() throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/timstorage/travelerDataFrame_pre-J2735-2016.json")));
    TravelerDataFrame expectedFrame = getExpectedTravelerDataFrame();

    TravelerDataFrame deserializedFrame = mapper.readValue(json, TravelerDataFrame.class);

    Assertions.assertEquals(expectedFrame, deserializedFrame);
  }

  @Test
  void deserializeJ2735_2016() throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/timstorage/travelerDataFrame_J2735-2016.json")));
    TravelerDataFrame expectedFrame = getExpectedTravelerDataFrame();

    TravelerDataFrame deserializedFrame = mapper.readValue(json, TravelerDataFrame.class);

    Assertions.assertEquals(expectedFrame, deserializedFrame);
  }

  @Test
  void deserializeJ2735_2020() throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(
        "src/test/resources/us/dot/its/jpo/ode/plugin/j2735/timstorage/travelerDataFrame_J2735-2020.json")));
    TravelerDataFrame expectedFrame = getExpectedTravelerDataFrame();

    TravelerDataFrame deserializedFrame = mapper.readValue(json, TravelerDataFrame.class);

    Assertions.assertEquals(expectedFrame, deserializedFrame);
  }

  private TravelerDataFrame getExpectedTravelerDataFrame() {
    TravelerDataFrame frame = new TravelerDataFrame();
    frame.setDoNotUse1("value1");
    frame.setFrameType(new FrameType());
    frame.setMsgId(new MsgId());
    frame.setStartYear("2024");
    frame.setStartTime("12:00");
    frame.setDurationTime("30");
    frame.setPriority("high");
    frame.setDoNotUse2("value2");
    frame.setRegions(new Regions());
    frame.setDoNotUse3("value3");
    frame.setDoNotUse4("value4");
    frame.setTcontent(new Content());
    frame.setUrl("http://example.com");
    return frame;
  }
}