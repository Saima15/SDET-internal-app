package com.scalesec.vulnado.controller;


import com.scalesec.vulnado.Postgres;
import com.scalesec.vulnado.service.User;
import com.scalesec.vulnado.dto.LoginRequest;
import com.scalesec.vulnado.dto.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.beans.factory.annotation.*;
import java.sql.SQLException;

@RestController
@EnableAutoConfiguration
@Api(tags = {"Login"})
public class LoginController {
  @Value("${app.secret}")
  private String secret;

  @CrossOrigin(origins = "*")
  @ApiOperation("This API is to login and get token")
  @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  ResponseEntity<LoginResponse> login(@RequestBody LoginRequest input) {
    User user = User.fetch(input.username);
    if (Postgres.md5(input.password).equals(user.hashedPassword)) {
      LoginResponse loginResponse= new LoginResponse(user.token(secret));
      return ResponseEntity.ok(loginResponse);
    } else {
      LoginResponse loginResponse= new LoginResponse("Unauthorised");
      return ResponseEntity.ok(loginResponse);
    }
  }

  @CrossOrigin(origins = "*")
  @ApiOperation("This API is to register a user")
  @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  ResponseEntity<String> register(@RequestBody LoginRequest input) throws SQLException {
    String result = User.insertUser(input.username, input.password);
    return ResponseEntity.ok(result);

  }
}

