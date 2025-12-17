import java.util.ArrayList;
import java.util.List;

public class Flotte {

    public List<Satellite> satellites = new ArrayList<>();

    public Flotte() { }

    public List<Satellite> getsatellites() {
        return this.satellites;
    }

    public void setsatellites(List<Satellite> satellites) {
        this.satellites = satellites;
    }
}
