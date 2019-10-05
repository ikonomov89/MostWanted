package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.domain.dtos.RacerImportDto;
import mostwanted.domain.entities.Racer;
import mostwanted.repository.RaceRepository;
import mostwanted.repository.RacerRepository;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RacerServiceImpl implements RacerService {

    private final static String RACERS_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/racers.json";

    private final FileUtil fileUtil;
    private final RacerRepository racerRepository;
    private final TownRepository townRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public RacerServiceImpl(FileUtil fileUtil, RaceRepository raceRepository, RacerRepository racerRepository, TownRepository townRepository, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.fileUtil = fileUtil;
        this.racerRepository = racerRepository;
        this.townRepository = townRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public Boolean racersAreImported() {
        return this.racerRepository.count() > 0;
    }

    @Override
    public String readRacersJsonFile() throws IOException {
        return this.fileUtil.readFile(RACERS_JSON_FILE_PATH);
    }

    @Override
    public String importRacers(String racersFileContent) {
        StringBuilder sb = new StringBuilder();
        RacerImportDto[] dtos = this.gson.fromJson(racersFileContent, RacerImportDto[].class);

        List<String> racersList = new ArrayList<>();

        for (RacerImportDto dto : dtos) {
            Racer racer = this.modelMapper.map(dto, Racer.class);
            if (!this.validationUtil.isValid(racer)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            if (!racersList.contains(racer.getName())) {
                racersList.add(racer.getName());

                racer.setHomeTown(this.townRepository.findByName(dto.getHomeTown()));

                this.racerRepository.saveAndFlush(racer);
            } else {
                sb.append("Error: Duplicate Data!").append(System.lineSeparator());
            }

        }
        return sb.toString();
    }

    @Override
    public String exportRacingCars() {
        //TODO: Implement me
        return null;
    }
}
