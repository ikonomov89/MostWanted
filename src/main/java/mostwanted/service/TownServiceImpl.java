package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.domain.dtos.TownImportDto;
import mostwanted.domain.entities.Town;
import mostwanted.repository.RaceRepository;
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
public class TownServiceImpl implements TownService {

    private final static String TOWNS_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/towns.json";

    private final FileUtil fileUtil;
    private final TownRepository townRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public TownServiceImpl(FileUtil fileUtil, RaceRepository raceRepository, TownRepository townRepository, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.fileUtil = fileUtil;
        this.townRepository = townRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }


    @Override
    public Boolean townsAreImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsJsonFile() throws IOException {
        return this.fileUtil.readFile(TOWNS_JSON_FILE_PATH);
    }

    @Override
    public String importTowns(String townsFileContent) {
        StringBuilder sb = new StringBuilder();

        TownImportDto[] dtos = this.gson.fromJson(townsFileContent, TownImportDto[].class);
        List<String> townList = new ArrayList<>();

        for (TownImportDto dto : dtos) {
            Town town = this.modelMapper.map(dto, Town.class);

            if (!this.validationUtil.isValid(town)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            if (!townList.contains(town.getName())) {
                townList.add(town.getName());

                this.townRepository.saveAndFlush(town);

                sb.append(String.format("Successfully imported Town - %s.", town.getName()))
                        .append(System.lineSeparator());
            } else {
                sb.append("Error: Duplicate Data!").append(System.lineSeparator());
            }

        }

        return sb.toString();
    }

    @Override
    public String exportRacingTowns() {
        StringBuilder sb = new StringBuilder();

        List<Town> townList = this.townRepository.findAll();

        townList.stream().sorted((t1, t2) -> {
            if (t1.getRacers().size() == t2.getRacers().size()) {
                return t1.getName().compareTo(t2.getName());
            } else if (t2.getRacers().size() > t1.getRacers().size()) {
                return t2.getRacers().size();
            } else {
                return t1.getRacers().size();
            }
        }).forEach(t -> sb.append(String.format("Name: %s%nRacers: %d", t.getName(), t.getRacers().size()))
                .append(System.lineSeparator()));


        return sb.toString();
    }
}
