package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class SEQUENCE
{
    private Item item;

    public Item getItem ()
    {
        return item;
    }

    public void setItem (Item item)
    {
        this.item = item;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [item = "+item+"]";
    }
}
