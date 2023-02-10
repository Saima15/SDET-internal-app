package com.scalesec.vulnado;

import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;

@RestController
@EnableAutoConfiguration
@ApiIgnore
public class CowController {
    @RequestMapping(value = "/cowsay")
    String cowsay(@RequestParam(defaultValue = "I love Linux!") String input) {
        return Cowsay.run(input);
    }
}
