package com.scalesec.vulnado;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

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
  @RequestMapping(value = "/comments", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  Comment createComment(@RequestHeader(value="x-auth-token") String token, @RequestBody @Valid CommentRequest input) {
    return Comment.createComment(input.username, input.body);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE, produces = "application/json")
  String deleteComment(@RequestHeader(value="x-auth-token") String token, @PathVariable("id") String id) {
    return Comment.deleteComment(id);
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/user-comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> getUserComments(@RequestHeader(value = "x-auth-token") String token, @RequestParam("user") String user) {
    if (ObjectUtils.isEmpty(Comment.getCommentsByUserName(user))) {
      throw new NotFound("Comment not found for user");
    } else {
      return Comment.getCommentsByUserName(user);
    }
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT, produces = "application/json")
  String updateComment(@RequestHeader(value = "x-auth-token") String token, @PathVariable("id") String id,
                       @RequestBody CommentRequest input) throws SQLException {
    return Comment.updateComment(input.body, id);
  }

}


class CommentRequest implements Serializable {
  @NotEmpty
  @NotNull
  public String username;
  @Max(20)
  @Min(5)
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
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFound extends RuntimeException {
  public NotFound(String exception) {
    super(exception);
  }
}