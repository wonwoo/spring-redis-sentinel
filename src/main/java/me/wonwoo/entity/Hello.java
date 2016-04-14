package me.wonwoo.entity;

/**
 * Created by wonwoo on 2016. 4. 14..
 */
public class Hello {
  private Long id;
  private String name;
  private String password;
  private String email;

  public Hello() { //why redis
  }

  public Hello(Long id, String name, String password, String email) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
