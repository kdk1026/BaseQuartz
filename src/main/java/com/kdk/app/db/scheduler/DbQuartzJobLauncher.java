package com.kdk.app.db.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kdk.app.db.service.CityService;

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
public class DbQuartzJobLauncher implements Job {

	private final CityService cityService;

	public DbQuartzJobLauncher(CityService cityService) {
		this.cityService = cityService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
            cityService.getAndRegisterCities();
		} catch (Exception e) {
			log.error("", e);
		}
	}

}