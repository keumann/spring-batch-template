package org.keumann.batch.base.scope;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Job Parameter를 받으려면 Scope 어노테이션을 선언 해야함.
 * Spring Batch 컴포넌트(Tasklet, ItemReader, ItemWriter, ItemProcessor 등)에 @StepScope를 사용하게 되면
 * Spring Batch가 Spring Container를 통해 지정된 Step의 실행 시점에 해당 컴포넌트를 Spring Bean으로 생성.
 * 즉 Bean의 생성 시점을 지정된 Scope가 실행되는 시점으로 지연.
 *
 * 장점 : Job Parameter의 Late Binding이 가능.Job Parameter의 Late Binding이 가능.
 *       Job Parameter의 Late Binding이 가능. @StepScope가 있다면 각각의 Step에서 별도의 Tasklet을 생성하고 관리하기 때문에
 *          서로의 상태를 침범할 일이 없음.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobScopeConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SimpleJobTasklet simpleJobTasklet;

    @Bean
    public Job scopeJob() {
        return jobBuilderFactory.get("scopeJob")
                .start(scopeStep1(null))
                .next(scopeStep2())
                .build()
                ;
    }

    @Bean
    @JobScope  //@JobScope는 Step 선언문에서 사용
    public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("scopeStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is scopeStep1");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build()
                ;
    }

    @Bean
    public Step scopeStep2() {
        return stepBuilderFactory.get("scoreStep2")
                //.tasklet(scopeStep2Tasklet(null))
                .tasklet(simpleJobTasklet)
                .build()
                ;
    }

    @Bean
    @StepScope  //@JobScope는 Step 선언문에서 사용
    public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return (contribution, chunkContext) -> {
            log.info(">>>>> This is scopeStep2");
            log.info(">>>>> requestDate = {}", requestDate);
            return RepeatStatus.FINISHED;
        };
    }

}