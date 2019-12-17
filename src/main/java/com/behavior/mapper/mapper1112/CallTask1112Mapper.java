package com.behavior.mapper.mapper1112;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask1112Mapper {
	int updateSmallSingleCompare(Map<Object,Object> update);
	int updateMonitorWapPc(Map<Object,Object> update);
	List<Map<Object,Object>> queryMonitorWapPcAdept(@Param("regionTypes") String wappcRegions);
}