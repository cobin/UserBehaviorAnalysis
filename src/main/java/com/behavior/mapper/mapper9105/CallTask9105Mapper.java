package com.behavior.mapper.mapper9105;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 财务流水记录进行整合处理，形成详细的流水记录流
 * @author  Cobin
 * @date    2020/4/1 9:41
 * @version 1.0
*/
public interface CallTask9105Mapper {
	/**
	 * 调用财务更新过程，对新数据进行增加和修改的数据更新
	 * @param update 条件参数
	 * @return
	 */
	int updateFinanceStatement(Map<Object, Object> update);

	/**
	 * 批量保存数据
	 * @param traceList
	 * @return
	 */
	List<Map<Object,Object>> updateMuliFinanceStatement(@Param("traceList") List<List<Map<String, String>>> traceList);
}