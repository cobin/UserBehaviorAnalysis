package com.behavior.mapper.mapper131;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取课程列表数据
 * @author  Cobin
 * @date    2020/8/8 9:41
 * @version 1.0
*/
public interface CallTask131Mapper {
	/**
	 * 获取日期内的所有课程
	 * @param sDate 开始日期
	 * @return
	 */
	List<Map<Object,Object>> queryCmsCourse(@Param("sDate") String sDate);

	/**
	 * 课程的属性列表
	 * @param sDate 开始日期
	 * @return
	 */
	List<Map<Object,Object>> queryCourseProperty(@Param("sDate") String sDate);

	/**
	 * 课程的属性列表
	 * @param sDate 开始日期
	 * @return
	 */
	List<Map<Object,Object>> queryCmsUserStock(@Param("sDate") String sDate);

	/**
	 * 抓取佳美需要的答题相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryVotes(@Param("id") int id);

	/**
	 * 抓取佳美需要的答题相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryVoteItems(@Param("id") int id);

	/**
	 * 抓取佳美需要的答题相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryVoteRecord2(@Param("id") int id);
	/**
	 * 抓取道雨需要的视频相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryWebNews(@Param("id") int id);

	/**
	 * 抓取道雨需要的视频相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryWebNewsClasses(@Param("id") int id);

	/**
	 * 抓取道雨需要的视频相关
	 * @param id
	 * @return
	 */
	List<Map<Object,Object>> queryWebNewsComment(@Param("id") int id);
}