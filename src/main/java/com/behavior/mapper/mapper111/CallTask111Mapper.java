package com.behavior.mapper.mapper111;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface CallTask111Mapper {
	List<Map<Object, Object>> querySourceZK360(@Param("sDate") Date sDate); // 周克需要的股票及功能信息的用户群体
	List<Map<Object, Object>> queryUserFunctionStat(@Param("nYearMonthDay") int nYearMonthDay, @Param("traceList") List<List<Map<Object, Object>>> users, @Param("funIds") String funIds);
	List<Map<Object, Object>> queryUserFunctionStat360(@Param("nYearMonthDay") int nYearMonthDay, @Param("traceList") List<List<Map<Object, Object>>> users);
	List<Map<Object, Object>> queryUserActiveStocks(@Param("nYearMonthDay") int nYearMonthDay, @Param("traceList") List<List<Map<Object, Object>>> users);
	int insertUserTrace(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserFunctionStat(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserFunctionStat360(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserFunctionStat3600(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserActiveStocks3600(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserActiveStocks(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserActiveStocksTJ(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserActiveStocksQX(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	List<Map<Object,Object>> queryLoginUser(@Param("sDate") String sDate, @Param("loginDate") String loginDate);
	int updateUserIndividualIndex(Map<Object, Object> update);
	
	List<Map<Object, Object>> queryUserLock(@Param("sdate") int sdate, @Param("edate") int edate);
	int updateUserUnLock(@Param("traceList") List<List<Map<Object, Object>>> users);
	
	int queryUserFunctionStat360Date();
	int queryPhoneMaxDate();
	int queryNewClassMaxDate();
	List<Map<Object, Object>> queryPhoneUsers(@Param("sDate") int sDate);
	List<Map<Object, Object>> queryPhoneUsersOneDay(@Param("sDate") int sDate);
	List<Map<Object, Object>> querySample360Users();
	int getZhouKeMaxDate360();
	int insertUserActiveStocksZK(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertUserFunctionStatZK(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int getUserOnlineMaxDate();
	int insertUserOnline(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int insertNextVisitTime(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int queryUserFunctionStat3600MaxDate(@Param("qDate") String qDate);
	
	int insertBehaviour(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getBehaviourMaxDate();
	int insertBehavLog9(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getBehavLog9MaxDate();
	int insertBehavLog10(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getBehavLog10MaxDate();
	
	int insertApacheLog(Map<String, Object> data);
	
	int insertEbPersonQuestion(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbPersonQuestionMaxDate();
	
	int insertEbPersonQuestionLog(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbPersonQuestionLogMaxDate();
	int insertEbPersonKeeper(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbPersonKeeperMaxDate();
	
	List<Map<Object,Object>> queryNewPersonKeeper(@Param("sDate") int sDate, @Param("seDate") Integer seDate);
	int insertEbPersonKeeper10(@Param("traceList") List<List<Map<Object, Object>>> traceList, @Param("traceListB01") List<List<Map<Object, Object>>> traceListB01);
	Date getEbPersonKeeper10MaxDate();
	int updateEbPersonKeeper10(@Param("sDate") int sDate);
	int updateEbPersonKeeperBandB1();
	
	int insertEbPersonUnLockLog(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbPersonUnLockLogMaxDate();
	int insertEbPersonPresaleLog(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int deleteEbMobileRegLog(@Param("traceList") List<String> traceList);
	int insertEbMobileRegLog(@Param("traceList") List<String> traceList);
	int updateEbMobileRegLog(@Param("traceList") List<String> traceList);
	List<Map<Object, Object>> queryEbMobileRegLog(@Param("sPersonId") int sPersonId, @Param("ePersonId") int ePersonId);
	int deleteEbFlashMaturevalue(@Param("sDate") int sDate);
	int insertEbFlashMaturevalue(@Param("traceList") List<String> traceList);
	int insertAgentName2Domain(@Param("traceList") List<List<Map<Object, Object>>> traceList);
		
	int insertUsersLogin(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int deleteUsersLogin(@Param("sDate") int sDate);
	
	
	int updateUsersCourseInsureDate(Map<Object, Object> update);
	int updateUsersCourse360Date(Map<Object, Object> update);
	int updateUsersCourse3600Date(Map<Object, Object> update);
	int updateUsersCourse18000Date(Map<Object, Object> update);
	int updateUsersCourseAllDate(Map<Object, Object> update);
	
	int insertEbSalesRelTelallocStat(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	
	int insertEbRelPersonNerve(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbRelPersonNerveMaxDate();
	
	int insertEbPersonPresaleDetail(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	Date getEbPersonPresaleDetailMaxDate();
}