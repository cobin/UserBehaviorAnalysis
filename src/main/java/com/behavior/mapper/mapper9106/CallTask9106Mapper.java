package com.behavior.mapper.mapper9106;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 财务流水记录进行整合处理，形成详细的流水记录流
 * @author  Cobin
 * @date    2020/4/1 9:41
 * @version 1.0
*/
public interface CallTask9106Mapper {
	/**
	 * 批量保存数据
	 * @param startDate 开始时间
	 * @param endDate	结束时间
	 * @return
	 */
	List<Long> getActiveDatePeerIp(@Param("activeStartDate") int startDate,@Param("activeEndDate") int endDate);

	List<Map<Object,Object>> queryIpArea();

	int insertIps(Map<Object, Object> data);
}