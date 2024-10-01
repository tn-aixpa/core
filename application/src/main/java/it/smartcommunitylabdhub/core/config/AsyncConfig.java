package it.smartcommunitylabdhub.core.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@Order(5)
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "taskScheduler")
    @Primary
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
        return threadPoolTaskScheduler;
    }
    // @Bean
    // PollingService pollingService(@Qualifier("taskExecutor") TaskExecutor executor) {
    //     // Create new Polling service instance
    //     PollingService pollingService = new PollingService(executor);

    //     // CREATE POLLERS EXAMPLE
    //     //
    //     // List<Workflow> test = new ArrayList<>();
    //     // Function<Integer, Integer> doubleFunction = num -> {
    //     // Random randomno = new Random();
    //     // long randomDelay = (long) (randomno.nextDouble() * 40 + 20); // Random delay
    //     // between 3 and 10
    //     // // seconds
    //     // System.out.println("RANDOM DELAY TEST WORKFLOW " + randomDelay);
    //     // try {
    //     // Thread.sleep(randomDelay * 1000);
    //     // } catch (InterruptedException e) {
    //     // // Handle interrupted exception if necessary
    //     // }

    //     // return 9;
    //     // };
    //     // test.add(WorkflowFactory
    //     // .builder().step(doubleFunction, 5).build());
    //     // pollingService.createPoller("TEST-POLLER", test, 3, true);

    //     // Start polling
    //     pollingService.startPolling();

    //     return pollingService;
    // }
}
