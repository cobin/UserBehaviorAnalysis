package com.behavior.mapper.mapper1114;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author  Cobin
 * @date    2020/3/28 9:43
 * @version 1.0
*/
public interface CallTask1114Mapper {
	int updateSmallOrderWap(Map<Object,Object> update);
	List<Map<String,Integer>> getSmallOrderWapActInfo();
	List<Map<Object,Object>> getSmallOrderUserInfo(@Param("sDate") String sDate,@Param("eDate") String eDate,@Param("adeptId") String adeptId,@Param("exDeptId") String exclusionDeptId);

	List<Integer> getMissionActUnits(@Param("actId") int actId);
	/**
	 * 获取活动信息
	 * @param actId 活动编号
	 * @return
	 */
	Map<String,Object> getMissionActInfoById(@Param("actId") int actId);
	/**
	 * 获取小单轮次成单率
	 * @param nType 1 电商 2 小单 3 整体团队
	 * @return
	 */
	List<Map<String,Object>> getHistoryActProductRate(@Param("nType") int nType,@Param("fromWhere") String fromWhere);

	int updateForecastData(@Param("traceList") List<Map<Object, Object>> traceList);
}