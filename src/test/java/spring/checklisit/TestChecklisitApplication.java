package spring.checklisit;

import org.springframework.boot.SpringApplication;

public class TestChecklisitApplication {

  public static void main(String[] args) {
    SpringApplication.from(ChecklisitApplication::main)
                     .with(TestcontainersConfiguration.class)
                     .run(args);
  }

}
