package com.behavior.mapper.mapper45;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.quartz.listeners.JobChainingJobListener;

public interface CallTask45Mapper {
	int insertUserTrace(@Param("traceList") List<List<Map<Object,Object>>> traceList);
	int insertUserFunctionStat(@Param("traceList") List<List<Map<Object,Object>>> traceList);
	List<Map<Object,Object>> queryLoginUser(@Param("logDays") int logDays,@Param("status") int status);


	int insertApacheLog(Map<String,Object> data);

	int insertUserData(@Param("traceList") List<List<Map<Object,Object>>> traceList);
	int insertOneDayUserData(@Param("traceList") List<List<Map<Object,Object>>> traceList);

	int insertIps(Map<Object, Object> data);
	List<Map<Object,Object>> queryIpArea();

	int insertChacuoIps(Map<Object, Object> data);

	int insertIpAddress(@Param("traceList") List<List<Map<Object,Object>>> traceList);

	List<String> queryIpAddress(@Param("page") int page,@Param("pageSize") int pageSize,@Param("desc") String desc);


	List<List<Map<Object,Object>>> getTaskEveryDay(@Param("opId") int opId);
}