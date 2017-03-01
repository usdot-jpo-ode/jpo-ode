package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

public class J2735MessageContent extends J2735Choice {
      
      private static final long serialVersionUID = 3472684479212369295L;
      
      public class CodeOrText extends J2735Choice {

         private static final long serialVersionUID = 1686363207723189470L;

         public Integer code_chosen;
         public String text_chosen;
         
         public CodeOrText (Integer code, String text) {
            super();
            setChosenField("code_chosen", code);
            setChosenField("text_chosen", text);
         }
      }

      public List<CodeOrText> advisory_chosen;
      public List<CodeOrText> exitService_chosen;
      public List<CodeOrText> genericSign_chosen;
      public List<CodeOrText> speedLimit_chosen;
      public List<CodeOrText> workZone_chosen;
      
    //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssMessageContent class
//      public MessageContent(Content content) {
//         super();
//
//         int chosenFlag = content.getChosenFlag();
//         
//         switch (chosenFlag) {
//         case Content.advisory_chosen:
//            setChosenField("advisory_chosen", new ArrayList<CodeOrText>());
//            if (content.hasAdvisory()) {
//               ITIScodesAndText advisory = content.getAdvisory();
//               for (ITIScodesAndText.Sequence_ e : advisory.elements) {
//                  if (e.item != null) {
//                     switch (e.item.getChosenFlag()) {
//                     case ITIScodesAndText.Sequence_.Item.itis_chosen:
//                        if (e.item.hasItis())
//                           advisory_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
//                        break;
//                     case ITIScodesAndText.Sequence_.Item.text_chosen:
//                        if (e.item.hasText())
//                           advisory_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
//                        break;
//                     }
//                  }
//               }
//            }
//            break;
//         case Content.exitService_chosen:
//            setChosenField("exitService_chosen", new ArrayList<CodeOrText>());
//            ExitService exitService = content.getExitService();
//            for (ExitService.Sequence_ e : exitService.elements) {
//               if (e.item != null) {
//                  switch (e.item.getChosenFlag()) {
//                  case ExitService.Sequence_.Item.itis_chosen:
//                     if (e.item.hasItis())
//                        exitService_chosen.add(
//                           new CodeOrText(e.item.getItis().intValue(), null));
//                     break;
//                  case ExitService.Sequence_.Item.text_chosen:
//                     if (e.item.hasText())
//                        exitService_chosen.add(new CodeOrText(null,
//                           e.item.getText().stringValue()));
//                     break;
//                  }
//               }
//            }
//            break;
//         case Content.genericSign_chosen:
//            setChosenField("genericSign_chosen", new ArrayList<CodeOrText>());
//            GenericSignage genericSign = content.getGenericSign();
//            for (GenericSignage.Sequence_ e : genericSign.elements) {
//               if (e.item != null) {
//                  switch (e.item.getChosenFlag()) {
//                  case GenericSignage.Sequence_.Item.itis_chosen:
//                     if (e.item.hasItis())
//                        genericSign_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
//                     break;
//                  case GenericSignage.Sequence_.Item.text_chosen:
//                     if (e.item.hasText())
//                        genericSign_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
//                     break;
//                  }
//               }
//            }
//            break;
//         case Content.speedLimit_chosen:
//            setChosenField("speedLimit_chosen", new ArrayList<CodeOrText>());
//            SpeedLimit speedLimit = content.getSpeedLimit();
//            for (SpeedLimit.Sequence_ e : speedLimit.elements) {
//               if (e.item != null) {
//                  switch (e.item.getChosenFlag()) {
//                  case SpeedLimit.Sequence_.Item.itis_chosen:
//                     if (e.item.hasItis())
//                        speedLimit_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
//                     break;
//                  case SpeedLimit.Sequence_.Item.text_chosen:
//                     if (e.item.hasText())
//                        speedLimit_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
//                     break;
//                  }
//               }
//            }
//            break;
//         case Content.workZone_chosen:
//            setChosenField("workZone_chosen", new ArrayList<CodeOrText>());
//            WorkZone workZone = content.getWorkZone();
//            for (WorkZone.Sequence_ e : workZone.elements) {
//               if (e.item != null) {
//                  switch (e.item.getChosenFlag()) {
//                  case WorkZone.Sequence_.Item.itis_chosen:
//                     if (e.item.hasItis())
//                        workZone_chosen.add(new CodeOrText(e.item.getItis().intValue(), null));
//                     break;
//                  case WorkZone.Sequence_.Item.text_chosen:
//                     if (e.item.hasText())
//                        workZone_chosen.add(new CodeOrText(null, e.item.getText().stringValue()));
//                     break;
//                  }
//               }
//            }
//            break;
//         }
//      }
//

}
