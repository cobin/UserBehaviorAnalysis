package com.behavior.mapper.mapper69;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author think
 */
public interface CallTask69Mapper {
	List<Map<Object, Object>> queryKeeperUnLockUser(@Param("traceList") List<Map<Object, Object>> personIds);
	List<Map<Object, Object>> queryNextVisitTime();	
	List<Map<Object, Object>> queryBehaviour(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryBehavLog9(@Param("sDate") Date sDate, @Param("eDate") Date eDate, @Param("qparam3") String param3, @Param("qs3") String s3);
	List<Map<Object, Object>> queryBehavLog10(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> querySalesRelTelallocStat(@Param("sDate") int sDate, @Param("nUserId") int nUserId, @Param("nRowCount") int nRowCount);
	List<Map<Object, Object>> queryEbPersonQuestion(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonQuestionLog(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonKeeperLog(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonKeeperLog10(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonPresaleLog10(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonUnLockLog(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbPersonPresaleLog(@Param("sDate") Date sDate, @Param("eDate") Date eDate);
	List<Map<Object, Object>> queryEbMobileRegLogDate();
	List<Map<Object, Object>> queryEbMobileRegLog(@Param("personId") int personId, @Param("rowCount") int rowCount);
	List<Map<Object, Object>> queryEbFlashMaturevalue(@Param("sDate") int sDate);
	List<Map<Object, Object>> queryAgentName2Domain();
	List<Map<Object, Object>> queryEbSalesRelTelallocStat(@Param("sDate") String sDate);	
	List<Map<Object, Object>> queryEbPersonPresaleDetail(@Param("sDate") Date sDate);	
	List<Map<Object, Object>> queryEbRelPersonNerve(@Param("sDate") Date sDate);	
}