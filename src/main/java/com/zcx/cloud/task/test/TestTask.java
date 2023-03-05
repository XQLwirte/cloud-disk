package com.zcx.cloud.task.test;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试任务
 * @author ZCX
 *
 */
@Slf4j
@Component
public class TestTask {
	
	public void test1(String param) {
		log.info("测试任务执行test1-"+param);
	}
	
	public void test2() {
		log.info("测试任务执行test2-无参数");
	}
}
