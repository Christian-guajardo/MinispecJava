package gen;

import gen.Satellite;
import java.util.ArrayList;
import java.util.List;

public class Flotte {

    public List<Satellite> satellites = new ArrayList<>();
    public Poke leTeste;

    public Flotte() { }

    public List<Satellite> getSatellites() {
        return this.satellites;
    }

    public void setSatellites(List<Satellite> satellites) {
        this.satellites = satellites;
    }

    public void addSatellitesElement(Satellite element) {
        this.satellites.add(element);
    }

    public void removeSatellitesElement(Satellite element) {
        this.satellites.remove(element);
    }

    public Poke getLeTeste() {
        return this.leTeste;
    }

    public void setLeTeste(Poke leTeste) {
        this.leTeste = leTeste;
    }
}
