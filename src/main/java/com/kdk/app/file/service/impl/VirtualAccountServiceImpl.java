package com.kdk.app.file.service.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.file.mapper.VirtualAccountMaper;
import com.kdk.app.file.service.VirtualAccountService;
import com.kdk.app.file.vo.VirtualAccountVo;

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
public class VirtualAccountServiceImpl implements VirtualAccountService {

	private final VirtualAccountMaper virtualAccountMaper;
    private final SqlSessionFactory sqlSessionFactory;
    private final SpringBootProperty springBootProperty;

	public VirtualAccountServiceImpl(VirtualAccountMaper virtualAccountMaper, SqlSessionFactory sqlSessionFactory, SpringBootProperty springBootProperty) {
		this.virtualAccountMaper = virtualAccountMaper;
		this.sqlSessionFactory = sqlSessionFactory;
		this.springBootProperty = springBootProperty;
	}

	@Override
	public void getAndRegisterVirtualAccounts() {
		virtualAccountMaper.deleteVirtualAccount();

		String filePath = springBootProperty.getProperty("file.virtual-account");
		int rowsToRead = 10;

		try ( BufferedReader br = new BufferedReader(new FileReader(filePath)) ) {
			String line;
			int lineNumber = 0;
			List<String> dataList = new ArrayList<>();

			// 첫 줄 건너뜀
//			if ( (line = br.readLine()) != null ) {
//
//			}

			while ( (line = br.readLine()) != null ) {
				lineNumber++;

				dataList.add(line);

				if ( lineNumber % rowsToRead == 0 ) {
					this.registerVirtualAccounts(dataList);
					dataList.clear();
				}
			}

			if ( lineNumber % rowsToRead != 0 ) {
				this.registerVirtualAccounts(dataList);
			}

		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void registerVirtualAccounts(List<String> dataList) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)  ) {
			for ( String item : dataList ) {
				VirtualAccountVo vo = new VirtualAccountVo();
				vo.setAcount(item);
				sqlSession.insert("com.kdk.app.file.mapper.VirtualAccountMaper.insertVirtualAccount", vo);
			}

			sqlSession.commit();
		} catch (Exception e) {
			log.error("Error during batch operation", e);
            throw e;
		}
	}

}
