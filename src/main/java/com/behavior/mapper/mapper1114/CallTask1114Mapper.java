package com.behavior.mapper.mapper1114;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author  Cobin
 * @date    2020/3/28 9:43
 * @version 1.0
*/
public interface CallTask1114Mapper {
	int updateSmallOrderWap(Map<Object,Object> update);
	List<Map<String,Integer>> getSmallOrderWapActInfo();
	List<Map<Object,Object>> getSmallOrderUserInfo(@Param("sDate") String sDate,@Param("eDate") String eDate,@Param("adeptId") String adeptId);
}