package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.domain.dtos.DistrictImportDto;
import mostwanted.domain.entities.District;
import mostwanted.repository.DistrictRepository;
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
public class DistrictServiceImpl implements DistrictService {

    private final static String DISTRICT_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/districts.json";

    private final FileUtil fileUtil;
    private final DistrictRepository districtRepository;
    private final TownRepository townRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public DistrictServiceImpl(FileUtil fileUtil, DistrictRepository districtRepository, TownRepository townRepository, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.fileUtil = fileUtil;
        this.districtRepository = districtRepository;
        this.townRepository = townRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public Boolean districtsAreImported() {
        return this.districtRepository.count() > 0;
    }

    @Override
    public String readDistrictsJsonFile() throws IOException {
        return fileUtil.readFile(DISTRICT_JSON_FILE_PATH);
    }

    @Override
    public String importDistricts(String districtsFileContent) {
        StringBuilder sb = new StringBuilder();

        DistrictImportDto[] dtos = this.gson.fromJson(districtsFileContent, DistrictImportDto[].class);
        List<String> districtList = new ArrayList<>();

        for (DistrictImportDto dto : dtos) {
            District district = this.modelMapper.map(dto, District.class);

            if (!this.validationUtil.isValid(district)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            if (!districtList.contains(district.getName())) {
                districtList.add(district.getName());

                district.setTown(this.townRepository.findByName(dto.getTownName()));

                this.districtRepository.saveAndFlush(district);

                sb.append(String.format("Successfully imported District - %s.", district.getName()))
                        .append(System.lineSeparator());

            } else {
                sb.append("Error: Duplicate Data!").append(System.lineSeparator());
            }

        }

        return sb.toString();
    }
}
