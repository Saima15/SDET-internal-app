package com.scalesec.vulnado;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;

import java.sql.SQLException;
import java.util.List;
import java.io.Serializable;

@RestController
@EnableAutoConfiguration
public class CommentsController {
  @Value("${app.secret}")
  private String secret;

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> comments(@RequestHeader(value="x-auth-token") String token, @RequestParam("order") String orderBy) throws SQLException {
    User.assertAuth(secret, token);
    return Comment.fetchAllComments(orderBy);
  }

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/comments", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  Comment createComment(@RequestHeader(value="x-auth-token") String token, @RequestBody CommentRequest input) {
    return Comment.createComment(input.username, input.body);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE, produces = "application/json")
  String deleteComment(@RequestHeader(value="x-auth-token") String token, @PathVariable("id") String id) {
    return Comment.deleteComment(id);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/user-comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> getUserDetails(@RequestHeader(value = "x-auth-token") String token, @RequestParam("user") String user) {
    return Comment.getCommentsByUserName(user);
  }
}

class CommentRequest implements Serializable {
  public String username;
  public String body;
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequest extends RuntimeException {
  public BadRequest(String exception) {
    super(exception);
  }
}

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class ServerError extends RuntimeException {
  public ServerError(String exception) {
    super(exception);
  }
}
