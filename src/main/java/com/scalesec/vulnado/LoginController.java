package com.scalesec.vulnado;

import org.springframework.boot.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import java.io.Serializable;
import java.sql.SQLException;

@RestController
@EnableAutoConfiguration
public class LoginController {
  @Value("${app.secret}")
  private String secret;

  @CrossOrigin(origins = "*")
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
  @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  ResponseEntity<String> register(@RequestBody LoginRequest input) throws SQLException {
    String result = User.insertUser(input.username, input.password);
    return ResponseEntity.ok(result);

  }
}

class LoginRequest implements Serializable {
  public String username;
  public String password;
}

class LoginResponse implements Serializable {
  public String token;
  public LoginResponse(String msg) { this.token = msg; }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class Unauthorized extends RuntimeException {
  public Unauthorized(String exception) {
    super(exception);
  }
}
