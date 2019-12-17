package com.behavior.mapper.mapper45;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask45Mapper {
	int insertUserTrace(@Param("traceList") List<List<Map<Object,Object>>> traceList);
	int insertUserFunctionStat(@Param("traceList") List<List<Map<Object,Object>>> traceList);
	List<Map<Object,Object>> queryLoginUser(@Param("logDays") int logDays,@Param("status") int status);
	
	int insertApacheLog(Map<String,Object> data);
}