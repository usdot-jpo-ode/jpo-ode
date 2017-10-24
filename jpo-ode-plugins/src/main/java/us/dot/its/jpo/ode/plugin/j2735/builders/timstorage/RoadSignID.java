package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class RoadSignID
{
    private Position position;

    private String crc;

    private MutcdCode mutcdCode;

    private String viewAngle;

    public Position getPosition ()
    {
        return position;
    }

    public void setPosition (Position position)
    {
        this.position = position;
    }

    public String getCrc ()
    {
        return crc;
    }

    public void setCrc (String crc)
    {
        this.crc = crc;
    }

    public MutcdCode getMutcdCode ()
    {
        return mutcdCode;
    }

    public void setMutcdCode (MutcdCode mutcdCode)
    {
        this.mutcdCode = mutcdCode;
    }

    public String getViewAngle ()
    {
        return viewAngle;
    }

    public void setViewAngle (String viewAngle)
    {
        this.viewAngle = viewAngle;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [position = "+position+", crc = "+crc+", mutcdCode = "+mutcdCode+", viewAngle = "+viewAngle+"]";
    }
}
