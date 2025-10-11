package com.InSpace.Api.config.Executor;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguraton {
  @Bean("asyncTaskExecutor")
  public Executor asyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(7);
    taskExecutor.setQueueCapacity(150);
    taskExecutor.setMaxPoolSize(42);
    taskExecutor.setThreadNamePrefix("AsyncTaskThread-");
    taskExecutor.initialize();
    return taskExecutor;
  }
}
