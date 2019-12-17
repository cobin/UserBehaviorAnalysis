package com.behavior.mapper.mapper76;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask76Mapper {
	List<Map<Object, Object>> queryLogin(@Param("nYearMonth") String nYearMonth, @Param("traceList") List<List<Map<Object,Object>>> personId,@Param("nDays") Integer nDays);
	List<Map<Object, Object>> queryCompassUsersLogin(@Param("nYearMonth") String nYearMonth, @Param("loginDate") String loginDate,@Param("nHour") Integer nHour,@Param("nPersonId") Integer personId,@Param("nRowCount") Integer nRowCount);
	List<Map<Object, Object>> queryLoginOnline(@Param("traceList") List<List<Map<Object,Object>>> personId,@Param("nDays") Integer nDays);
	List<Map<Object, Object>> queryUserOnline(@Param("sdate") Integer nDate);
	int checkCompassTable(@Param("nYearMonth") String nYearMonth);
}