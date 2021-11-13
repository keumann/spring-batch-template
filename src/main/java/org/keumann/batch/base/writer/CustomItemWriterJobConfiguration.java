package org.keumann.batch.base.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keumann.domain.pay.Pay;
import org.keumann.domain.pay.Pay2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

/**
 * Spring Batch에서 공식적으로 지원하지 않는 Writer를 사용하고 싶을때 ItemWriter인터페이스를 구현하여 사용
 * (ex) Reader에서 읽어온 데이터를 RestTemplate으로 외부 API로 전달, 여러 Entity를 동시에 save 등등
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomItemWriterJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private static final int chunkSize = 10;

    @Bean
    public Job customItemWriterJob() {
        return jobBuilderFactory.get("customItemWriterJob")
                .start(customItemWriterStep())
                .build();
    }

    @Bean
    public Step customItemWriterStep() {
        return stepBuilderFactory.get("customItemWriterStep")
                .<Pay, Pay2>chunk(chunkSize)
                .reader(customItemWriterReader())
                .processor(customItemWriterProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> customItemWriterReader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("customItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Pay p")
                .build();
    }

    @Bean
    public ItemProcessor<Pay, Pay2> customItemWriterProcessor() {
        return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
    }

    /**
     * 람다식으로 깔끔하게 ItemWriter 반환 가능
     * @return
     */
    @Bean
    public ItemWriter<Pay2> customItemWriter() {
        return items -> {
            for (Pay2 item : items) {
                System.out.println(item);
            }
        };
    }
}