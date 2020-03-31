package com.behavior.mapper.mapper9104;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CallTask9104Mapper {
	int updateFinMerDataSystemToday(Map<Object, Object> update);
	String updateFinMerDataSystemFlowDetails(@Param("traceList") List<List<Map<String,Integer>>> traceList);
	List<Map<String,Integer>> getFinMerDataSystemTodayIds();
	List<Map<String,Object>> getFinMerDataSystemAll();
	List<Map<String,Object>> getFinMerDataSystemFlowDetail();
}