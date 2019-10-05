package mostwanted.domain.entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "towns")
public class Town extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "town", cascade = CascadeType.ALL)
    private List<District> districts;

    @OneToMany(mappedBy = "homeTown", cascade = CascadeType.ALL)
    private List<Racer> racers;

    public Town() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public List<Racer> getRacers() {
        return racers;
    }

    public void setRacers(List<Racer> racers) {
        this.racers = racers;
    }

}
