package org.keumann;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing  //이 애플리케이션은 배치 프로세싱을 하겠다는 의미
@SpringBootApplication
public class BatchTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchTemplateApplication.class, args);
    }

}
