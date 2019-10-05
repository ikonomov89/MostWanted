package mostwanted.domain.entities;


import javax.persistence.*;

@Entity
@Table(name = "race_entries")
public class RaceEntry extends BaseEntity {

    @Column(name = "has_finished")
    private Boolean hasFinished;

    @Column(name = "finish_time")
    private Double finishTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "racer_id", referencedColumnName = "id")
    private Racer racer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "race_id", referencedColumnName = "id")
    private Race race;

    public RaceEntry() {
    }

    public Boolean getHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(Boolean hasFinished) {
        this.hasFinished = hasFinished;
    }

    public Double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Double finishTime) {
        this.finishTime = finishTime;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Racer getRacer() {
        return racer;
    }

    public void setRacer(Racer racer) {
        this.racer = racer;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
