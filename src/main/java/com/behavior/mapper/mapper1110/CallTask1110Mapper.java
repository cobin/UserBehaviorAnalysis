package com.behavior.mapper.mapper1110;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask1110Mapper {
	int updateKeeperPlatStatus360(Map<Object,Object> update);
	List<Map<Object,Object>> queryFunctionUsers(@Param("actId") int actId);
	List<Map<Object,Object>> queryActPeriod();
	
	List<Map<Object,Object>> queryPerson3600(@Param("sourceIds") String sourceIds);
}