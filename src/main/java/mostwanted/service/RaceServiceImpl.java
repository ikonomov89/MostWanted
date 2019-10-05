package mostwanted.service;

import mostwanted.domain.dtos.races.EntryImportDto;
import mostwanted.domain.dtos.races.RaceImportDto;
import mostwanted.domain.dtos.races.RaceImportRootDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Race;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.repository.DistrictRepository;
import mostwanted.repository.RaceEntryRepository;
import mostwanted.repository.RaceRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RaceServiceImpl implements RaceService {

    private final static String RACES_XML_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/races.xml";

    private final RaceRepository raceRepository;
    private final DistrictRepository districtRepository;
    private final RaceEntryRepository raceEntryRepository;
    private final FileUtil fileUtil;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository, DistrictRepository districtRepository, RaceEntryRepository raceEntryRepository, FileUtil fileUtil, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.raceRepository = raceRepository;
        this.districtRepository = districtRepository;
        this.raceEntryRepository = raceEntryRepository;
        this.fileUtil = fileUtil;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }


    @Override
    public Boolean racesAreImported() {
        return this.raceRepository.count() > 0;
    }

    @Override
    public String readRacesXmlFile() throws IOException {
        return this.fileUtil.readFile(RACES_XML_FILE_PATH);
    }

    @Override
    public String importRaces() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        RaceImportRootDto rootDtos = this.xmlParser.parseXml(RaceImportRootDto.class, RACES_XML_FILE_PATH);

        for (RaceImportDto dto : rootDtos.getRaceImportDtos()) {
            Race race = this.modelMapper.map(dto, Race.class);
            if (!this.validationUtil.isValid(race)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            District district = this.districtRepository.findByName(dto.getDistrictName());
            race.setDistrict(district);

            List<RaceEntry> raceEntries = new ArrayList<>();

            for (EntryImportDto entryDto : dto.getEntryImportDtos()) {
                this.modelMapper.map(dto.getEntryImportDtos(), RaceEntry.class);
                RaceEntry raceEntry = this.raceEntryRepository.findById(entryDto.getId()).orElse(null);

                if (raceEntry != null) {
                    raceEntries.add(raceEntry);
                    raceEntry.setRace(race);
                }
            }

            race.setEntries(raceEntries);
            this.raceRepository.saveAndFlush(race);

            sb.append(String.format("Successfully imported Race - %d.", race.getId())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}