package org.keumann.batch.base.hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.keumann.batch.TestConfiguration;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@RunWith(SpringRunner.class) // 스프링 JUnit 기능을 사용하겠다는 표시
@ContextConfiguration(classes = {HelloConfiguration.class, TestConfiguration.class})
public class HelloConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void hello() throws Exception {

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    }

}