package com.behavior.mapper.mapper9104;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 财务流水记录进行整合处理，形成详细的流水记录流
 * @author  Cobin
 * @date    2020/4/1 9:41
 * @version 1.0
*/
public interface CallTask9104Mapper {
	/**
	 * 调用财务更新过程，对新数据进行增加和修改的数据更新，无效的数据进行删除
	 * @param update 条件参数
	 * @return
	 */
	int updateFinMerDataSystemToday(Map<Object,Object> update);

	/**
	 * 更新财务流水数据
	 * @param traceList 记录集合
	 * @return
	 */
	String updateFinMerDataSystemFlowDetails(@Param("traceList") List<List<Map<String,Integer>>> traceList);

	/**
	 * 获取需要处理的资金流水号
	 * @return
	 */
	List<Map<String,Integer>> getFinMerDataSystemTodayIds();

	/**
	 * 获取所有的资金流水信息
	 * @return
	 */
	List<Map<String,Object>> getFinMerDataSystemAll();

	/**
	 * 获取已经存在的资金流水父节点的记录
	 * @return
	 */
	List<Map<String,Object>> getFinMerDataSystemFlowDetail();

	/**
	 * 获取已经存在的资金流水子节点分支的记录
	 * @return
	 */
	List<Map<String,Object>> getFinMerDataSystemFlowDetailBranch();
}