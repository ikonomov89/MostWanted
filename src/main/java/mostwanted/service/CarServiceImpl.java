package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.domain.dtos.CarImportDto;
import mostwanted.domain.entities.Car;
import mostwanted.repository.CarRepository;
import mostwanted.repository.RacerRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class CarServiceImpl implements CarService {

    private final static String CARS_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/cars.json";

    private final FileUtil fileUtil;
    private final CarRepository carRepository;
    private final RacerRepository racerRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public CarServiceImpl(FileUtil fileUtil, CarRepository carRepository, RacerRepository racerRepository, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.fileUtil = fileUtil;
        this.carRepository = carRepository;
        this.racerRepository = racerRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }


    @Override
    public Boolean carsAreImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsJsonFile() throws IOException {
        return fileUtil.readFile(CARS_JSON_FILE_PATH);
    }

    @Override
    public String importCars(String carsFileContent) {
        StringBuilder sb = new StringBuilder();

        CarImportDto[] dtos = this.gson.fromJson(carsFileContent, CarImportDto[].class);

        for (CarImportDto dto : dtos) {
            Car car = this.modelMapper.map(dto, Car.class);
            if (!this.validationUtil.isValid(car)) {
                sb.append("Error: Incorrect Data!").append(System.lineSeparator());
                continue;
            }

            car.setRacer(this.racerRepository.findByName(dto.getRacerName()));

            this.carRepository.saveAndFlush(car);

            sb.append(String.format("Successfully imported Car - %s %s @ %s.", car.getBrand(), car.getModel(), car.getYearOfProduction())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
