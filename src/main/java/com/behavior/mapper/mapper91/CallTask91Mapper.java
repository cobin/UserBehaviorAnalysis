package com.behavior.mapper.mapper91;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface CallTask91Mapper {
	@Deprecated
	/**
	 * @see CallTask111Mapper.queryNewPersonKeeper()
	 */
	List<Map<Object, Object>> queryNewBieUserClass(@Param("sDate") int sDate, @Param("seDate") Integer seDate);
	@Deprecated
	/**
	 * @see CallTask111Mapper.queryNewPersonKeeper()
	 */
	List<Map<Object, Object>> queryNewBieUserClassAll(@Param("sDate") int sDate, @Param("seDate") Integer seDate);
	List<Map<Object, Object>> queryNerve(@Param("traceList") List<List<Map<Object, Object>>> personId, @Param("nDays") Integer nDays);
	List<Map<Object, Object>> queryTelPhone(@Param("sDate") String sDate, @Param("seDate") String seDate, @Param("traceList") List<List<Map<Object, Object>>> personIds);
	List<Map<Object, Object>> queryTelPhoneDate(@Param("sDate") String sDate, @Param("seDate") String seDate, @Param("traceList") List<List<Map<Object, Object>>> personIds);
	List<Map<Object, Object>> queryTelPhoneFirstDate(@Param("traceList") List<List<Map<Object, Object>>> personIds);
	List<Map<Object, Object>> queryKeeperUnLockUser(@Param("traceList") List<List<Map<Object, Object>>> personIds);
	int updateWorkerOnCallLog(Map<Object, Object> update);
	List<Map<Object, Object>> queryUpgraderClass360(@Param("servLevelId") int servLevelId, @Param("sDate") int sDate); //20160530
	List<Map<Object, Object>> queryPersonReg(@Param("logType") String logType, @Param("sDate") int sDate); // 7,11 20160530
		
	int insertSalesRelTelallocStat(@Param("traceList") List<List<Map<Object, Object>>> traceList);
	int deleteSalesRelTelallocStat(@Param("nDate") int nDate);
	int getSalesRelTelallocStatMaxDate();
	
	@Select("select * from DB_WebNerveData.dbo.gtja_20170922 a,DB_WebNerveData.dbo.gtja2_20170922 b where a.csdc_client_name=b.PName and b.autoId=#{autoId} and a.AutoID>#{id}")
	List<Map<Object,Object>> getVer1(@Param("autoId") int verId, @Param("id") int autoId);
	
	//@Select("select * from DB_WebNerveData.dbo.gtja2_20170922")
	List<Map<Object,Object>> getVer2();
	
	@Update("update DB_WebNerveData.dbo.gtja2_20170922 set xf=#{ver} where AutoID=#{autoId}")
	int updateVer2(@Param("ver") String cardNo, @Param("autoId") long autoId);
}