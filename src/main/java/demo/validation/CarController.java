package demo.validation;

import springboot.annotation.mvc.PostMapping;
import springboot.annotation.mvc.RequestBody;
import springboot.annotation.mvc.RestController;
import springboot.annotation.validation.Validated;

import javax.validation.Valid;

@Validated
@RestController("/cars")
public class CarController {
    @PostMapping
    public CarDto create(@RequestBody @Valid CarDto carDto) {
        return carDto;
    }
}
