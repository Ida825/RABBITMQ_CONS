package cn.et;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Mail_Cons {

	

	public static void main(String[] args) throws IOException, TimeoutException {
		SpringApplication.run(Mail_Cons.class, args);
		 
		
	}

}
