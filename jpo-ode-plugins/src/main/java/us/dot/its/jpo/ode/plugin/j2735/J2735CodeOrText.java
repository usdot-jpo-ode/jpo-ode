package us.dot.its.jpo.ode.plugin.j2735;

public class J2735CodeOrText extends J2735Choice {

   private static final long serialVersionUID = 1686363207723189470L;

   public Integer code_chosen;
   public String text_chosen;
   
   public J2735CodeOrText (Integer code, String text) {
      super();
      setChosenField("code_chosen", code);
      setChosenField("text_chosen", text);
   }
}

