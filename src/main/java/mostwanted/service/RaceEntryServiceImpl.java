package mostwanted.service;

import mostwanted.domain.dtos.raceentries.RaceEntryImportDto;
import mostwanted.domain.dtos.raceentries.RaceEntryImportRootDto;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.repository.CarRepository;
import mostwanted.repository.RaceEntryRepository;
import mostwanted.repository.RacerRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RaceEntryServiceImpl implements RaceEntryService {

    private final static String RACE_ENTRIES_XML_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/race-entries.xml";

    private final RaceEntryRepository raceEntryRepository;
    private final CarRepository carRepository;
    private final RacerRepository racerRepository;
    private final FileUtil fileUtil;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    @Autowired
    public RaceEntryServiceImpl(RaceEntryRepository raceEntryRepository, CarRepository carRepository, RacerRepository racerRepository, FileUtil fileUtil, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.raceEntryRepository = raceEntryRepository;
        this.carRepository = carRepository;
        this.racerRepository = racerRepository;
        this.fileUtil = fileUtil;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public Boolean raceEntriesAreImported() {
        return this.raceEntryRepository.count() > 0;
    }

    @Override
    public String readRaceEntriesXmlFile() throws IOException {
        return this.fileUtil.readFile(RACE_ENTRIES_XML_FILE_PATH);
    }

    @Override
    public String importRaceEntries() throws JAXBException {
        StringBuilder sb = new StringBuilder();

        RaceEntryImportRootDto rootDto = this.xmlParser.parseXml(RaceEntryImportRootDto.class, RACE_ENTRIES_XML_FILE_PATH);

        List<RaceEntryImportDto> dtos = rootDto.getRaceEntryImportDtoList().stream().
                map(d -> this.modelMapper.map(d, RaceEntryImportDto.class)).collect(Collectors.toList());


        for (RaceEntryImportDto dto : dtos) {
            RaceEntry raceEntry = this.modelMapper.map(dto, RaceEntry.class);
            if (!this.validationUtil.isValid(raceEntry)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            Integer idCar = Integer.parseInt(dto.getCarId());

            raceEntry.setCar(this.carRepository.findById(idCar).orElse(null)); //FIXME think not to be null;
            raceEntry.setRacer(this.racerRepository.findByName(dto.getRacerName()));
            raceEntry.setRace(null);

            this.raceEntryRepository.saveAndFlush(raceEntry);


            sb.append(String.format("Successfully imported RaceEntry - %d.", raceEntry.getId())).append(System.lineSeparator());

        }

        return sb.toString();
    }
}
