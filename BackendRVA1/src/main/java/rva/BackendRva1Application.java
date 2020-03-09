package rva;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendRva1Application {

	public static void main(String[] args) {
		SpringApplication.run(BackendRva1Application.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context){
		return args -> {
			System.out.println("Beans provided by Spring framework");
			String[] beanNames = context.getBeanDefinitionNames();
			for(String beanName: beanNames){
				System.out.println(beanName);
			}
		};
	}

}
