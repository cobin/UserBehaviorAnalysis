package com.behavior.mapper.mapper9101;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask9101Mapper {
	List<Map<String, Object>> testChinese();	
	List<Map<String, Object>> queryConfig(@Param("startDate") String sDate, @Param("endDate") String eDate);
	List<List<Map<String, Object>>> queryDepartRate(@Param("startDate") int sDate, @Param("keepRate") double keepRate);
	List<List<Map<String, Object>>> queryWeekUsersTrapezium(@Param("configPersonnel") List<Map<String, Double>> personnel
            , @Param("configRetention") List<Map<String, Double>> retention
            , @Param("configActWeek") List<Map<String, Double>> actWeek
            , @Param("configOldUser") List<Map<String, Object>> oldUser);
	List<Map<String, Object>> queryBakeAgain(@Param("configPersonnel") List<Map<String, Double>> personnel
            , @Param("configRetention") List<Map<String, Double>> retention
            , @Param("configOldUser") List<Map<String, Object>> oldUser);
	
	void  updateForecastDept(@Param("userPart") List<Map<String, Object>> userPart
            , @Param("deptData") List<Map<String, Object>> deptData, @Param("rId") int recruitId);
	
	void updateConfigPersonnel(@Param("configPersonnel") List<Map<String, Double>> configPersonnel, @Param("rId") int recruitId);
	void updateConfigRetention(@Param("configRetention") List<Map<String, Double>> configRetention, @Param("rId") int recruitId);
	void updateRecruitDeptAnnual(@Param("deptAnnual") List<Map<String, Object>> deptAnnual, @Param("rId") int recruitId);
	void updateRecruitEveryWeek(@Param("everyWeek") List<Map<String, Object>> everyWeek, @Param("rId") int recruitId);
	
	List<Map<String,Object>> queryWeek(@Param("everyWeek") List<Map<String, Object>> everyWeek
            , @Param("userAll") List<Map<String, Object>> userAll, @Param("userPart") List<Map<String, Object>> userPart
            , @Param("rId") int recruitId);
	
	void updateActStatus(@Param("actStatus") List<Map<String, Double>> actStatus, @Param("rId") int recruitId);
}