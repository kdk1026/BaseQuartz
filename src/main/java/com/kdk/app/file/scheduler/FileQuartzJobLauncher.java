package com.kdk.app.file.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kdk.app.file.service.VirtualAccountService;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 29. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Slf4j
public class FileQuartzJobLauncher implements Job {

	private final VirtualAccountService virtualAccountService;

	public FileQuartzJobLauncher(VirtualAccountService virtualAccountService) {
		this.virtualAccountService = virtualAccountService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			virtualAccountService.getAndRegisterVirtualAccounts();
		} catch (Exception e) {
			log.error("", e);
		}
	}

}