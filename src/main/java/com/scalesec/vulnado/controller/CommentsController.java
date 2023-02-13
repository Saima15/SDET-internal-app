package com.scalesec.vulnado.controller;

import com.scalesec.vulnado.service.Comment;
import com.scalesec.vulnado.service.User;
import com.scalesec.vulnado.dto.CommentRequest;
import com.scalesec.vulnado.exceptions.BadRequest;
import com.scalesec.vulnado.exceptions.Found;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@EnableAutoConfiguration
@Api(tags = {"Comments"})
public class CommentsController {
  @Value("${app.secret}")
  private String secret;

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation("This API is to get list of all comments")
  @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> comments(@RequestHeader(value="x-auth-token") String token, @RequestParam("orderByUserName") String orderByUserName) throws SQLException {
    User.assertAuth(secret, token);
    return Comment.fetchAllComments(orderByUserName);
  }

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation("This API is to add a comment")
  @RequestMapping(value = "/comments", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
  Comment createComment(@RequestHeader(value = "x-auth-token") String token, @RequestBody @Valid CommentRequest input) {
    if (input.body.length() >= 5 && input.body.length() <= 10) {
      return Comment.createComment(input.username, input.body);
    } else {
      throw new BadRequest("Error");
    }
  }

  @CrossOrigin(origins = "*")
  @ApiOperation("This API is to delete a comment")
  @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE, produces = "application/json")
  String deleteComment(@RequestHeader(value="x-auth-token") String token, @PathVariable("id") String id) {
    return Comment.deleteComment(id);
  }

  @CrossOrigin(origins = "*")
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ApiOperation("This API is to get comments of a particular user")
  @RequestMapping(value = "/user-comments", method = RequestMethod.GET, produces = "application/json")
  List<Comment> getUserComments(@RequestHeader(value = "x-auth-token") String token, @RequestParam(value = "user",required = false) String user) {
    if (ObjectUtils.isEmpty(Comment.getCommentsByUserName(user))) {
      throw new Found("Comment found for user");
    } else {
      return Comment.getCommentsByUserName(user);
    }
  }

  @CrossOrigin(origins = "*")
  @ApiOperation("This API is to update a comment")
  @RequestMapping(value = "/comments/{username}", method = RequestMethod.POST, produces = "application/json")
  String updateComment(@RequestHeader(value = "x-auth-token") String token, @PathVariable("username") String username,
                       @RequestBody CommentRequest input) throws SQLException {
    return Comment.updateComment(input.body, username);
  }

}
