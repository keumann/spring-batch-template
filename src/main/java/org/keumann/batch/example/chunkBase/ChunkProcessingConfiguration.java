package org.keumann.batch.example.chunkBase;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ChunkProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkProcessingJob(){
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.chunkBaseStep(null))
                .build();
    }

    @Bean
    @JobScope   //Job 실행/종료에 따라서 빈이 생성 소멸됨.
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String chunkSize){  //spel을 이용해서 파라미터 설정
        return stepBuilderFactory.get("taskBaseStep")
                .<String, String>chunk(StringUtils.isNotEmpty(chunkSize) ? Integer.parseInt(chunkSize) : 10)      //첫번째 제네릭 타입 : input 타입, 두번째 제네릭 타입 : itemProcessor output 타입, 10개씩 처리하겠다.
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())   //아웃풋 타입의 리스트를 받아서 처리
                .build();
    }

    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());        //생성자로 리스트를 받음
    }

    private ItemProcessor<String, String> itemProcessor() { //reader에서 생성한 데이터를 가공하거나 writer로 넘길지 말지를 결정
        return item -> item + ", Spring Batch";             //ItemProcessor에서 null을 리턴하게되면 itemProcessor로 넘어갈 수 없음.
    }


    private ItemWriter<String> itemWriter() {
        return items -> log.info("chunk item size : {}", items.size());
        //return items -> items.forEach(log::info);
    }

    private List<String> getItems() {

        List<String> items = new ArrayList<>();

        for(int i=0;i<100;i++){
            items.add(i + " Hello");
        }

        return items;
    }

}
