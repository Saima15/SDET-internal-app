package com.scalesec.vulnado;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class Comment {
  public String id, username, body;
  public Timestamp created_on;

  public Comment(String id, String username, String body, Timestamp created_on) {
    this.id = id;
    this.username = username;
    this.body = body;
    this.created_on = created_on;
  }

  public static Comment createComment(String username, String body){
    long time = new Date().getTime();
    Timestamp timestamp = new Timestamp(time);
    Comment comment = new Comment(UUID.randomUUID().toString(), username, body, timestamp);
    try {
      if (comment.commit()) {
        return comment;
      } else {
        throw new BadRequest("Unable to save comment");
      }
    } catch (Exception e) {
      throw new ServerError(e.getMessage());
    }
  }

  public static List<Comment> fetchAllComments(String orderBy) throws SQLException {
    Statement stmt = null;
    List<Comment> comments = new ArrayList();
    try {
      Connection cxn = Postgres.connection();
      stmt = cxn.createStatement();
      String query = null;
      if(orderBy.equals("ASC")||orderBy.equals("asc")) {
         query = "select * from comments order by username DESC;";
      }
      else if(orderBy.equals("DESC")||orderBy.equals("desc")) {
         query = "select * from comments order by username ASC;";
      }
      if(Objects.isNull(query)){
        throw new ServerError("Order is not present");
      }
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String body = rs.getString("body");
        Timestamp created_on = rs.getTimestamp("created_on");
        Comment c = new Comment(id, username, body, created_on);
        comments.add(c);
      }
//      cxn.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      throw e;
    }
//    finally {
      return comments;
//    }
  }

  public static String deleteComment(String id) {
    try {
      String sql = "DELETE FROM comments where id = ?";
      Connection con = Postgres.connection();
      PreparedStatement pStatement = con.prepareStatement(sql);
      pStatement.setString(1, id);

      if (1 == pStatement.executeUpdate()) {
        return "Deleted successfully";
      } else
        return "Delete unsuccessful";

    } catch (Exception e) {
      e.printStackTrace();
    }

    return "Deleted";
  }

  private Boolean commit() throws SQLException {
    String sql = "INSERT INTO comments (id, username, body, created_on) VALUES (?,?,?,?)";
    Connection con = Postgres.connection();
    PreparedStatement pStatement = con.prepareStatement(sql);
    pStatement.setString(1, this.id);
    pStatement.setString(2, this.username);
    pStatement.setString(3, this.body);
    pStatement.setTimestamp(4, this.created_on);
    return 1 == pStatement.executeUpdate();
  }

  public static List<Comment> getCommentsByUserName(String user) {
    List<Comment> comments = new ArrayList<>();
    try {
      Connection con;
      PreparedStatement pstmt;
      ResultSet rs;
      String sql = "SELECT * FROM comments WHERE username=?" ;

      con = Postgres.connection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1,user);

      rs = pstmt.executeQuery();
      while (rs.next()) {
        String id = rs.getString("id");
        String username = rs.getString("username");
        String body = rs.getString("body");
        Timestamp createdOn = rs.getTimestamp("created_on");
        Comment c = new Comment(id, username, body, createdOn);
        comments.add(c);
      }
//      con.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
    } finally {
      return comments;
    }
  }
}
