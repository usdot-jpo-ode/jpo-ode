package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class J2735AdvisorySituationDataTest {

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void test() {
      J2735AdvisorySituationData gasd = new J2735AdvisorySituationData();
      
      ArrayList<J2735GeographicalPath> regions = new ArrayList<J2735GeographicalPath>();
      regions.add(new J2735GeographicalPath());
      
      ArrayList<J2735TravelerDataFrame> dataFrames =
            new ArrayList<J2735TravelerDataFrame>();
      dataFrames.add(new J2735TravelerDataFrame()
            .setRegions(regions)
            .setMessageId(new J2735MessageId()
                  .setRoadSignID_chosen(new J2735RoadSignId()
                        .setPosition(new J2735Position3D())
                        )
                  )
            );
      
      ArrayList<J2735RegionalContent> regional = new ArrayList<J2735RegionalContent>();
      regional.add(new J2735RegionalContent());
      
      gasd.setAdvisoryDetails(
            new J2735AdvisoryDetails().setTravelerInfo(
                  new J2735TravelerInfo()
                  .setDataFrames(dataFrames )
                  .setRegional(regional )
                  )
            );
      System.out.println(gasd.toJson(true));
   }

}
