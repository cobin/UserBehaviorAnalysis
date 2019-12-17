package com.behavior.mapper.mapper1111;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask1111Mapper {
	int updateUsersCourseVideo(Map<Object,Object> update);
	
	int updateRoomDataByDate(Map<Object,Object> update);
	
	int updateUserClassRoomStaticInsure(Map<Object,Object> update);
	int updateUserClassRoomStatic360(Map<Object,Object> update);
	int updateUserClassRoomStatic3600(Map<Object,Object> update);
	int updateUserClassRoomStatic3600Expand(Map<Object,Object> update);
	int updateUserClassRoomStatic18000(Map<Object,Object> update);
	int updateUserClassRoomStatic28800(Map<Object,Object> update);
	int updateUserClassRoomStaticAll(Map<Object,Object> update);
	

	int updateUsersCourseInsureDate(Map<Object,Object> update);
	int updateUsersCourse360Date(Map<Object,Object> update);
	int updateUsersCourse3600Date(Map<Object,Object> update);
	int updateUsersCourse18000Date(Map<Object,Object> update);
	int updateUsersCourse28800Date(Map<Object,Object> update);
	int updateUsersCourseAllDate(Map<Object,Object> update);
		
	List<Map<Object,Object>> queryRoomData(@Param("calDate") String sDate);
}