/*
 * Copyright (C) Hanwha S&C Ltd., 2018. All rights reserved.
 *
 * This software is covered by the license agreement between
 * the end user and Hanwha S&C Ltd., and may be
 * used and copied only in accordance with the terms of the
 * said agreement.
 *
 * Hanwha S&C Ltd., assumes no responsibility or
 * liability for any errors or inaccuracies in this software,
 * or any consequential, incidental or indirect damage arising
 * out of the use of the software.
 */

package transaction.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import transaction.domain.HotBatchJobInfo;


/**
 *
 * @since 5.0
 */
@Component
public class HotBatchJobDao implements InitializingBean {
	private Logger logger = LoggerFactory.getLogger(HotBatchJobDao.class);

	@Autowired
	private DataSource dataSource;

	protected NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(jdbcTemplate == null){
			jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		}
	}
	/**
	 * HotBatchJobInfo 정보 조회
	 *
	 * @param jobName
	 * @return
	 */
	public HotBatchJobInfo getBatchJobInfo(String jobName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("jobId", jobName);

		String sql = "SELECT * FROM HONE_hot_BATCH_JOB WHERE JOB_ID = :jobId";

		try {
			return jdbcTemplate.queryForObject(sql, paramMap,  new BeanPropertyRowMapper<HotBatchJobInfo>(HotBatchJobInfo.class));
		}catch(EmptyResultDataAccessException e) {
			throw new RuntimeException("Failed to find Job Info["+jobName+"] from Job Repository", e);
		}
	}


}
