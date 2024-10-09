package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.printf("Чтобы перейти на страницу сайта открывай: %n%s%n",
				"http://localhost:8080");
		System.out.println("""
                Пользователи:
                reader1 : password1
                reader2 : password2
                reader_and_publisher : password3
                publisher : password4
                admin : password5
                Описание ролей см. в readme""");
	}

}
