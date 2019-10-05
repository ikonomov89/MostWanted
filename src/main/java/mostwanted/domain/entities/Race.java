package mostwanted.domain.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "races")
public class Race extends BaseEntity {

    @NotNull
    @Column(name = "laps", nullable = false)
    private Integer laps;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    private List<RaceEntry> entries;

    public Race() {
    }

    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public List<RaceEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<RaceEntry> entries) {
        this.entries = entries;
    }

}
