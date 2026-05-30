package car_management_api.car.repository;

import car_management_api.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository
        extends JpaRepository<Car, Long> {

    List<Car> findAllByUserId(Long userId);

    Optional<Car> findByIdAndUserId(
            Long id,
            Long userId
    );

    boolean existsByLicensePlate(String licensePlate);
}