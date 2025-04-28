package com.kdk.app.db.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdk.app.db.mapper.CityMapper;
import com.kdk.app.db.service.CityService;
import com.kdk.app.db.vo.CityVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 30. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Slf4j
@Service
public class CityServiceImpl implements CityService {

	@Autowired
	private CityMapper cityMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

	@Override
	public void getAndRegisterCities() {
		cityMapper.deleteCityBackAll();

		int pagePerRow = 1000;

		int totalCnt = cityMapper.selectCityTotalCnt();
		int totalPages = (int) Math.ceil((double) totalCnt / pagePerRow);

		Map<String, Object> parameterValues;

		for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
			int offset = (currentPage - 1) * pagePerRow;

			parameterValues = new HashMap<>();
			parameterValues.put("offset", offset);
            parameterValues.put("limit", pagePerRow);

            List<CityVo> cities = cityMapper.selectCityAll(parameterValues);

			try ( SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false) ) {
				for ( CityVo item : cities ) {
					CityMapper getCityMapper = sqlSession.getMapper(CityMapper.class);
					getCityMapper.insertCityBack(item);
				}

				sqlSession.commit();
			} catch (Exception e) {
				log.error("Error during batch operation", e);
                throw e;
			}
		}
	}

}
