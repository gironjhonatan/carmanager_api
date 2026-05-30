package car_management_api.car.controller;

import car_management_api.car.entity.Car;
import car_management_api.car.repository.CarRepository;
import car_management_api.user.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cars")
@AllArgsConstructor
public class CarController {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/cars")
    public List<Car> getCarsByUserId(@PathVariable Long userId) {
        return carRepository.findAllByUserId(userId);
    }

    @PostMapping("/users/{userId}/cars")
    public ResponseEntity<?> createCar(
            @PathVariable Long userId,
            @RequestBody Car car
    ) {

        if (carRepository.existsByLicensePlate(
                car.getLicensePlate()
        )) {

            return ResponseEntity.badRequest()
                    .body("La placa ya existe");
        }

        return userRepository.findById(userId)
                .map(user -> {

                    car.setUser(user);

                    if (
                        car.getPhotoUrl() == null ||
                        car.getPhotoUrl().isBlank()
                    ) {

                        car.setPhotoUrl(
                                "POR_CARGAR"
                        );
                    }

                    Car savedCar =
                            carRepository.save(car);

                    return ResponseEntity.ok(
                            savedCar
                    );
                })
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(
            @PathVariable Long id,
            @RequestBody Car carDetails
    ) {

        return carRepository.findById(id)
                .map(car -> {

                    boolean exists =
                            carRepository
                            .existsByLicensePlate(
                                    carDetails.getLicensePlate()
                            );

                    if (
                        exists &&
                        !car.getLicensePlate()
                                .equals(
                                        carDetails.getLicensePlate()
                                )
                    ) {

                        return ResponseEntity
                                .badRequest()
                                .body(
                                        "La placa ya existe"
                                );
                    }

                    car.setBrand(
                            carDetails.getBrand()
                    );

                    car.setModel(
                            carDetails.getModel()
                    );

                    car.setYear(
                            carDetails.getYear()
                    );

                    car.setLicensePlate(
                            carDetails.getLicensePlate()
                    );

                    car.setColor(
                            carDetails.getColor()
                    );

                    car.setPhotoUrl(
                            carDetails.getPhotoUrl()
                    );

                    Car updatedCar =
                            carRepository.save(car);

                    return ResponseEntity.ok(
                            updatedCar
                    );

                })
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        return carRepository.findById(id)
                .map(car -> {
                    carRepository.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}