package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Description
{
    private Path path;

    public Path getPath ()
    {
        return path;
    }

    public void setPath (Path path)
    {
        this.path = path;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [path = "+path+"]";
    }
}
