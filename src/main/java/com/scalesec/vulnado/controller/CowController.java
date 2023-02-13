package com.scalesec.vulnado.controller;

import com.scalesec.vulnado.service.Cowsay;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@EnableAutoConfiguration
@ApiIgnore
public class CowController {
    @RequestMapping(value = "/cowsay")
    String cowsay(@RequestParam(defaultValue = "I love Linux!") String input) {
        return Cowsay.run(input);
    }
}
