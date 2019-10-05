package mostwanted.domain.dtos.races;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlRootElement(name = "race")
@XmlAccessorType(XmlAccessType.FIELD)
public class RaceImportDto {

    @XmlElement(name = "laps")
    private Integer laps;

    @XmlElement(name = "district-name")
    private String districtName;

    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry")
    private List<EntryImportDto> entryImportDtos;

    public List<EntryImportDto> getEntryImportDtos() {
        return entryImportDtos;
    }

    public void setEntryImportDtos(List<EntryImportDto> entryImportDtos) {
        this.entryImportDtos = entryImportDtos;
    }

    public RaceImportDto() {
    }

    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

}
