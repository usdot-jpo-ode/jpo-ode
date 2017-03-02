package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;

import us.dot.its.jpo.ode.j2735.dsrc.ExitService;
import us.dot.its.jpo.ode.j2735.dsrc.GenericSignage;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedLimit;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerDataFrame.Content;
import us.dot.its.jpo.ode.j2735.dsrc.WorkZone;
import us.dot.its.jpo.ode.j2735.itis.ITIScodesAndText;
import us.dot.its.jpo.ode.plugin.j2735.J2735CodeOrText;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageContent;

public class OssMessageContent {

   private OssMessageContent() {
   }

   public static J2735MessageContent genericMessageContent(Content content) {
      J2735MessageContent gmc = new J2735MessageContent();
      
       int chosenFlag = content.getChosenFlag();
       
       switch (chosenFlag) {
       case Content.advisory_chosen:
          gmc.setChosenField("advisory_chosen", new ArrayList<J2735CodeOrText>());
          if (content.hasAdvisory()) {
             ITIScodesAndText advisory = content.getAdvisory();
             for (ITIScodesAndText.Sequence_ e : advisory.elements) {
                if (e.item != null) {
                   switch (e.item.getChosenFlag()) {
                   case ITIScodesAndText.Sequence_.Item.itis_chosen:
                      if (e.item.hasItis())
                         gmc.advisory_chosen.add(
                               new J2735CodeOrText(e.item.getItis().intValue(), null));
                      break;
                   case ITIScodesAndText.Sequence_.Item.text_chosen:
                      if (e.item.hasText())
                         gmc.advisory_chosen.add(new J2735CodeOrText(null, e.item.getText().stringValue()));
                      break;
                   }
                }
             }
          }
          break;
       case Content.exitService_chosen:
          gmc.setChosenField("exitService_chosen", new ArrayList<J2735CodeOrText>());
          ExitService exitService = content.getExitService();
          for (ExitService.Sequence_ e : exitService.elements) {
             if (e.item != null) {
                switch (e.item.getChosenFlag()) {
                case ExitService.Sequence_.Item.itis_chosen:
                   if (e.item.hasItis())
                      gmc.exitService_chosen.add(
                         new J2735CodeOrText(e.item.getItis().intValue(), null));
                   break;
                case ExitService.Sequence_.Item.text_chosen:
                   if (e.item.hasText())
                      gmc.exitService_chosen.add(new J2735CodeOrText(null,
                         e.item.getText().stringValue()));
                   break;
                }
             }
          }
          break;
       case Content.genericSign_chosen:
          gmc.setChosenField("genericSign_chosen", new ArrayList<J2735CodeOrText>());
          GenericSignage genericSign = content.getGenericSign();
          for (GenericSignage.Sequence_ e : genericSign.elements) {
             if (e.item != null) {
                switch (e.item.getChosenFlag()) {
                case GenericSignage.Sequence_.Item.itis_chosen:
                   if (e.item.hasItis())
                      gmc.genericSign_chosen.add(new J2735CodeOrText(e.item.getItis().intValue(), null));
                   break;
                case GenericSignage.Sequence_.Item.text_chosen:
                   if (e.item.hasText())
                      gmc.genericSign_chosen.add(new J2735CodeOrText(null, e.item.getText().stringValue()));
                   break;
                }
             }
          }
          break;
       case Content.speedLimit_chosen:
          gmc.setChosenField("speedLimit_chosen", new ArrayList<J2735CodeOrText>());
          SpeedLimit speedLimit = content.getSpeedLimit();
          for (SpeedLimit.Sequence_ e : speedLimit.elements) {
             if (e.item != null) {
                switch (e.item.getChosenFlag()) {
                case SpeedLimit.Sequence_.Item.itis_chosen:
                   if (e.item.hasItis())
                      gmc.speedLimit_chosen.add(new J2735CodeOrText(e.item.getItis().intValue(), null));
                   break;
                case SpeedLimit.Sequence_.Item.text_chosen:
                   if (e.item.hasText())
                      gmc.speedLimit_chosen.add(new J2735CodeOrText(null, e.item.getText().stringValue()));
                   break;
                }
             }
          }
          break;
       case Content.workZone_chosen:
          gmc.setChosenField("workZone_chosen", new ArrayList<J2735CodeOrText>());
          WorkZone workZone = content.getWorkZone();
          for (WorkZone.Sequence_ e : workZone.elements) {
             if (e.item != null) {
                switch (e.item.getChosenFlag()) {
                case WorkZone.Sequence_.Item.itis_chosen:
                   if (e.item.hasItis())
                      gmc.workZone_chosen.add(new J2735CodeOrText(e.item.getItis().intValue(), null));
                   break;
                case WorkZone.Sequence_.Item.text_chosen:
                   if (e.item.hasText())
                      gmc.workZone_chosen.add(new J2735CodeOrText(null, e.item.getText().stringValue()));
                   break;
                }
             }
          }
          break;
       }
      return gmc;
   }

}
