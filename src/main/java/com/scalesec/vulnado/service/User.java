package com.scalesec.vulnado.service;

import com.scalesec.vulnado.Postgres;
import com.scalesec.vulnado.exceptions.Unauthorized;

import java.sql.*;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.UUID;


public class User {
    public String id, username, hashedPassword;

    public User(String id, String username, String hashedPassword) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String token(String secret) {
//    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
//        return Jwts.builder().setSubject(this.username).signWith(key).compact();
        return getToken(secret);
    }

    public static void assertAuth(String secret, String token) {
        try {

            String newToken = getToken(secret);
            if (!newToken.equals(token)) {
                throw new Unauthorized("Unauthorized");
            }
//      SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
//      Jwts.parser()
//        .setSigningKey(key)
//        .parseClaimsJws(token);
        } catch (Exception e) {
            throw new Unauthorized(e.getMessage());
        }
    }

    private static String getToken(String secret) {
        LocalDate date = LocalDate.now();
        String[] dayNames = new DateFormatSymbols().getWeekdays();
        Calendar date1 = Calendar.getInstance();
        String dayName = dayNames[date1.get(Calendar.DAY_OF_WEEK)];
        String d = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String newToken = dayName + d + secret;
        return newToken;
    }

    public static User fetch(String un) {
        Statement stmt = null;
        User user = null;
        try {
            Connection cxn = Postgres.connection();
            stmt = cxn.createStatement();
            System.out.println("Opened database successfully");

            String query = "select * from users where username = '" + un + "' limit 1";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                String user_id = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                user = new User(user_id, username, password);
            }
            cxn.close();
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return user;
        }
    }

    public static String insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (user_id, username, password, created_on) VALUES (?, ?, ?, current_timestamp)";
        PreparedStatement pStatement = null;
        try {
            Connection cxn = Postgres.connection();

            pStatement = cxn.prepareStatement(sql);
            pStatement.setString(1, UUID.randomUUID().toString());
            pStatement.setString(2, username);
            pStatement.setString(3, Postgres.md5(password));
            if (1 == pStatement.executeUpdate()) {
                return "Registered";
            } else {
                return "Error in registration";
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
