package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Extent extends Asn1Object {
  private static final long serialVersionUID = 1L;

  public enum ExtentEnum {
    useInstantlyOnly, // (0),
    useFor3meters, // (1),
    useFor10meters, // (2),
    useFor50meters, // (3),
    useFor100meters, // (4),
    useFor500meters, // (5),
    useFor1000meters, // (6),
    useFor5000meters, // (7),
    useFor10000meters, // (8),
    useFor50000meters, // (9),
    useFor100000meters, // (10),
    useFor500000meters, // (11),
    useFor1000000meters, // (12),
    useFor5000000meters, // (13),
    useFor10000000meters, // (14),
    forever // (15) -- very wide area
  }

  private String useInstantlyOnly; // (0),
  private String useFor3meters; // (1),
  private String useFor10meters; // (2),
  private String useFor50meters; // (3),
  private String useFor100meters; // (4),
  private String useFor500meters; // (5),
  private String useFor1000meters; // (6),
  private String useFor5000meters; // (7),
  private String useFor10000meters; // (8),
  private String useFor50000meters; // (9),
  private String useFor100000meters; // (10),
  private String useFor500000meters; // (11),
  private String useFor1000000meters; // (12),
  private String useFor5000000meters; // (13),
  private String useFor10000000meters; // (14),
  private String forever; // (15) -- very wide area
  
  public String getUseInstantlyOnly() {
    return useInstantlyOnly;
  }
  public void setUseInstantlyOnly(String useInstantlyOnly) {
    this.useInstantlyOnly = useInstantlyOnly;
  }
  public String getUseFor3meters() {
    return useFor3meters;
  }
  public void setUseFor3meters(String useFor3meters) {
    this.useFor3meters = useFor3meters;
  }
  public String getUseFor10meters() {
    return useFor10meters;
  }
  public void setUseFor10meters(String useFor10meters) {
    this.useFor10meters = useFor10meters;
  }
  public String getUseFor50meters() {
    return useFor50meters;
  }
  public void setUseFor50meters(String useFor50meters) {
    this.useFor50meters = useFor50meters;
  }
  public String getUseFor100meters() {
    return useFor100meters;
  }
  public void setUseFor100meters(String useFor100meters) {
    this.useFor100meters = useFor100meters;
  }
  public String getUseFor500meters() {
    return useFor500meters;
  }
  public void setUseFor500meters(String useFor500meters) {
    this.useFor500meters = useFor500meters;
  }
  public String getUseFor1000meters() {
    return useFor1000meters;
  }
  public void setUseFor1000meters(String useFor1000meters) {
    this.useFor1000meters = useFor1000meters;
  }
  public String getUseFor5000meters() {
    return useFor5000meters;
  }
  public void setUseFor5000meters(String useFor5000meters) {
    this.useFor5000meters = useFor5000meters;
  }
  public String getUseFor10000meters() {
    return useFor10000meters;
  }
  public void setUseFor10000meters(String useFor10000meters) {
    this.useFor10000meters = useFor10000meters;
  }
  public String getUseFor50000meters() {
    return useFor50000meters;
  }
  public void setUseFor50000meters(String useFor50000meters) {
    this.useFor50000meters = useFor50000meters;
  }
  public String getUseFor100000meters() {
    return useFor100000meters;
  }
  public void setUseFor100000meters(String useFor100000meters) {
    this.useFor100000meters = useFor100000meters;
  }
  public String getUseFor500000meters() {
    return useFor500000meters;
  }
  public void setUseFor500000meters(String useFor500000meters) {
    this.useFor500000meters = useFor500000meters;
  }
  public String getUseFor1000000meters() {
    return useFor1000000meters;
  }
  public void setUseFor1000000meters(String useFor1000000meters) {
    this.useFor1000000meters = useFor1000000meters;
  }
  public String getUseFor5000000meters() {
    return useFor5000000meters;
  }
  public void setUseFor5000000meters(String useFor5000000meters) {
    this.useFor5000000meters = useFor5000000meters;
  }
  public String getUseFor10000000meters() {
    return useFor10000000meters;
  }
  public void setUseFor10000000meters(String useFor10000000meters) {
    this.useFor10000000meters = useFor10000000meters;
  }
  public String getForever() {
    return forever;
  }
  public void setForever(String forever) {
    this.forever = forever;
  }
}
