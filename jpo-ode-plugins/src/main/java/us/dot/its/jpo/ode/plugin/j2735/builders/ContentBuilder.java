package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.timstorage.Content;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.ITIS_CodesAndText;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Items;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Item;

public class ContentBuilder {
  private ContentBuilder() {
        throw new UnsupportedOperationException();
    }

    private static Items genericItems(JsonNode sequence) {
        Items itemsObj = new Items();

        JsonNode item = sequence.get("item");
        if (item != null) {
            Item itemObj = new Item();

            JsonNode itis = item.get("itis");
            if (itis != null) {
                itemObj.setItis(itis.asText());
            }

            JsonNode text = item.get("text");
            if (text != null) {
                itemObj.setText(text.asText());
            }

            itemsObj.setItem(itemObj);
        }

        return itemsObj;
    }

    private static Items[] genericSequence(JsonNode contentType) {
        Items[] sequenceObj = null;

        JsonNode sequence = contentType.get("SEQUENCE");
        if (sequence != null) {
            List<Items> iList = new ArrayList<>();

            if (sequence.isArray()) {
                Iterator<JsonNode> elements = sequence.elements();

                while (elements.hasNext()) {
                    iList.add(genericItems(elements.next()));
                }
            } else {
                iList.add(genericItems(sequence));
            }

            sequenceObj = iList.toArray(new Items[0]);
        }

        return sequenceObj;
    }

    public static Content genericContent(JsonNode content) {
        Content contentObj = new Content();

        JsonNode advisory = content.get("advisory");
        if (advisory != null) {
            ITIS_CodesAndText adivsoryObj = new ITIS_CodesAndText();
            adivsoryObj.setSEQUENCE(genericSequence(advisory));
            contentObj.setAdvisory(adivsoryObj);
        }

        JsonNode workZone = content.get("workZone");
        if (workZone != null) {
            ITIS_CodesAndText workZoneObj = new ITIS_CodesAndText();
            workZoneObj.setSEQUENCE(genericSequence(workZone));
            contentObj.setWorkZone(workZoneObj);
        }

        JsonNode genericSign = content.get("genericSign");
        if (genericSign != null) {
            ITIS_CodesAndText genericSignObj = new ITIS_CodesAndText();
            genericSignObj.setSEQUENCE(genericSequence(genericSign));
            contentObj.setGenericSign(genericSignObj);
        }

        JsonNode speedLimit = content.get("speedLimit");
        if (speedLimit != null) {
            ITIS_CodesAndText speedLimitObj = new ITIS_CodesAndText();
            speedLimitObj.setSEQUENCE(genericSequence(speedLimit));
            contentObj.setSpeedLimit(speedLimitObj);
        }

        JsonNode exitService = content.get("exitService");
        if (exitService != null) {
            ITIS_CodesAndText exitServiceObj = new ITIS_CodesAndText();
            exitServiceObj.setSEQUENCE(genericSequence(exitService));
            contentObj.setExitService(exitServiceObj);
        }

        return contentObj;
    }
}
