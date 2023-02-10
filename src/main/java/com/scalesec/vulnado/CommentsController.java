package com.scalesec.vulnado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;

import javax.validation.Valid;
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
  List<Comment> comments(@RequestHeader(value="x-auth-token") String token, @RequestParam("orderByUserName") String orderByUserName) throws SQLException {
    User.assertAuth(secret, token);
    return Comment.fetchAllComments(orderByUserName);
  }

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/comments", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
  Comment createComment(@RequestHeader(value = "x-auth-token") String token, @RequestBody @Valid CommentRequest input) {
    if (input.body.length() >= 5 && input.body.length() <= 10) {
      return Comment.createComment(input.username, input.body);
    } else {
      throw new BadRequest("Error");
    }
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE, produces = "application/json")
  String deleteComment(@RequestHeader(value="x-auth-token") String token, @PathVariable("id") String id) {
    return Comment.deleteComment(id);
  }

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @RequestMapping(value = "/user-comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> getUserComments(@RequestHeader(value = "x-auth-token") String token, @RequestParam("user") String user) {
    if (ObjectUtils.isEmpty(Comment.getCommentsByUserName(user))) {
      throw new Found("Comment found for user");
    } else {
      return Comment.getCommentsByUserName(user);
    }
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.POST, produces = "application/json")
  String updateComment(@RequestHeader(value = "x-auth-token") String token, @PathVariable("id") String id,
                       @RequestBody CommentRequest input) throws SQLException {
    return Comment.updateComment(input.body, id);
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
@ResponseStatus(HttpStatus.FOUND)
class Found extends RuntimeException {
  public Found(String exception) {
    super(exception);
  }
}