package com.zcx.cloud.system.scheduler.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuartzTriggerListener implements TriggerListener{
	public static final String ALL_TRIGGER_LISTENER = "all_trigger_listener";
	
	/**
	 * 获取Trigger Name
	 */
	@Override
	public String getName() {
		return QuartzTriggerListener.ALL_TRIGGER_LISTENER;
	}

	
	/**
	 * Trigger被执行，JobDetail将会被执行
	 */
	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
	}

	/**
	 * 是否终止Trigger执行，终止后JobDetail将不会被执行
	 * true：终止
	 * false：不终止
	 */
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		
		return false;
	}

	/**
	 * Trigger由于系统的某些原因错过触发
	 */
	@Override
	public void triggerMisfired(Trigger trigger) {
		
	}

	/**
	 * Trigger执行完成，JobDetail执行完毕
	 */
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		
	}

}
