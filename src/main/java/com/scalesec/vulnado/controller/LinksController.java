package com.scalesec.vulnado.controller;

import com.scalesec.vulnado.exceptions.BadRequest;
import com.scalesec.vulnado.service.LinkLister;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.io.IOException;


@RestController
@EnableAutoConfiguration
@ApiIgnore
public class LinksController {
  @RequestMapping(value = "/links", produces = "application/json")
  List<String> links(@RequestParam String url) throws IOException{
    return LinkLister.getLinks(url);
  }
  @RequestMapping(value = "/links-v2", produces = "application/json")
  List<String> linksV2(@RequestParam String url) throws BadRequest {
    return LinkLister.getLinksV2(url);
  }
}
