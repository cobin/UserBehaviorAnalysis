package com.behavior.scheduler;

import com.behavior.BehaviorMain;
import com.behavior.mapper.mapper9104.CallTask9104Mapper;
import com.cobin.util.Tools;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  Cobin
 * @date    2020/3/28 10:12
 * @version 1.0
*/
public class WorkCallFinMerDataSystemNotify extends WorkJob {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			BehaviorMain bm = (BehaviorMain)arg0.getScheduler().getContext().get("context");			
			execWork(bm,null);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void execWork(BehaviorMain bm,String sdate ){
		long curTime = System.currentTimeMillis();
		CallTask9104Mapper ct9104 = bm.getMapper(CallTask9104Mapper.class);
		//执行本日数据处理
		Map<Object,Object> param = new HashMap<>(5);
		param.put("dataType",1);
		ct9104.updateFinMerDataSystemToday(param);
		log.info("财务处理结果,"+param);
		//所有的ID集合
		List<Map<String,Integer>> ids = ct9104.getFinMerDataSystemTodayIds();
		//所有的已经存在的流水记录
		Map<String,Map<String,Object>> exitFlowDetail = getFowDetails(ct9104);
		Map<String,Map<String,Object>> exitFlowDetailBranch = getFowDetailsBranch(ct9104);
		//所有的财务数据
        List<Map<String,Object>> resultFinMer = ct9104.getFinMerDataSystemAll();
		Map<Integer,Map<Integer,Map<String,Object>>> query = getFinMerAll(resultFinMer,false);
		Map<Integer,Map<Integer,Map<String,Object>>> queryBranch = getFinMerAll(resultFinMer,true);
		List<Map<String,Integer>> matchFlows = new ArrayList<>(resultFinMer.size()*3);
		Map<String,String> matchExist = new HashMap<>(resultFinMer.size()*2);
		//遍历访问此流水号是否有父类
		for(Map<String,Integer> id:ids){
			int rId = id.get("rId");
			searchFinMerId(matchFlows, matchExist, exitFlowDetail, query, rId, new int[]{rId},false);
		}
		matchExist.clear();
		//遍历访问此流水号是否有父类
		for(Map<String,Integer> id:ids){
			int rId = id.get("rId");
			searchFinMerId(matchFlows, matchExist, exitFlowDetailBranch, queryBranch, rId, new int[]{rId},true);
		}

		log.info("财务流水处理,耗时:"+(System.currentTimeMillis()-curTime)+">>>>总数："+matchFlows.size()+"("+matchFlows.size()+")");
		curTime = System.currentTimeMillis();
		List<List<Map<String,Integer>>> resultFlow = new ArrayList<>();
		List<Map<String,Integer>> rowFlow = null ;
		//入库操作
		for(int i=0;i<matchFlows.size();i++){
			if(i%1000==0){
				if(resultFlow.size()>=50){
					String msg =ct9104.updateFinMerDataSystemFlowDetails(resultFlow);
					log.info(msg+">>耗时:"+(System.currentTimeMillis()-curTime));
					curTime = System.currentTimeMillis();
					resultFlow.clear();
				}
				rowFlow = new ArrayList<>();
				resultFlow.add(rowFlow);
			}
			rowFlow.add(matchFlows.get(i));
		}
		if(!resultFlow.isEmpty()) {
			String msg = ct9104.updateFinMerDataSystemFlowDetails(resultFlow);
			log.info(msg+">>耗时:"+(System.currentTimeMillis()-curTime));
		}
	}

	/**
	 * 查找当前ID的父类集合
	 * @param matchFlows	父子集合
	 * @param matchExist	存在的结合
	 * @param exitFlowDetail 存在的流水集合
	 * @param query			全集数据
	 * @param rId			需要查询的ID
	 * @param curRids		当前ID属于的父类ID集合
	 */
	private void searchFinMerId(List<Map<String,Integer>> matchFlows ,Map<String,String> matchExist,Map<String,Map<String,Object>> exitFlowDetail,Map<Integer,Map<Integer,Map<String,Object>>> query,int rId,int[] curRids,boolean isBranch){
		Map<Integer,Map<String,Object>> row = query.get(rId);
		for(Map.Entry<Integer,Map<String,Object>> me:row.entrySet()){
			int _mId = me.getKey();
			Map<String,Object> mv = me.getValue();
			boolean isNotExit = true;
			for(int ie:curRids){
				if(ie==_mId){
					isNotExit = false;
					break;
				}
			}
			if(isNotExit){
				int[] _curRids = new int[curRids.length+1];
				int i = 0;
				for(int curRid:curRids) {
					addMatchFlow(matchFlows, matchExist,exitFlowDetail,mv, curRid, _mId,isBranch?2:1);
					_curRids[i] = curRid;
					i++;
				}
				_curRids[i] = _mId;
				searchFinMerId(matchFlows,matchExist,exitFlowDetail,query,_mId,_curRids,isBranch);
			}else {
				addMatchFlow(matchFlows, matchExist,exitFlowDetail,mv, rId, _mId,isBranch?2:1);
			}
		}
	}

	/**
	 * 增加匹配成功的父子对
	 * @param matchFlows
	 * @param matchExist
	 * @param exitFlowDetail
	 * @param curRowData
	 * @param rId
	 * @param _mId
	 */
	private void addMatchFlow(List<Map<String, Integer>> matchFlows, Map<String, String> matchExist,Map<String,Map<String,Object>> exitFlowDetail,Map<String,Object> curRowData, int rId, int _mId,int branch) {
		String _key = rId+"_"+_mId;
		if(!matchExist.containsKey(_key)) {
			boolean isOk = true;
			Map<String,Object> m = exitFlowDetail.get(_key);
			if(m!=null) {
				String claim1 = String.valueOf(m.get("claim"));
				String claim2 = String.valueOf(curRowData.get("claim"));
				if(claim1.equals(claim2)){
					isOk = false;
				}
			}
			if(isOk) {
				Map<String, Integer> mv = new HashMap<>(2);
				mv.put("rId", rId);
				mv.put("flowRid", _mId);
				mv.put("branch",branch);
				matchFlows.add(mv);
			}
			matchExist.put(_key,"");
		}
	}

	/**
	 * 得到所有的财务数据
	 * @param result
	 * @return
	 */
	private Map<Integer,Map<Integer,Map<String,Object>>> getFinMerAll(List<Map<String,Object>> result,boolean isBranch){
		Map<Integer,Map<Integer,Map<String,Object>>> vals = new HashMap<>(result.size());
		Map<Integer,Map<String,Object>> mapResult = new HashMap<>(result.size());
		for(Map<String ,Object> r:result){
			int rId = (int)r.get("rId");
			mapResult.put(rId,r);
		}
		for(Map<String ,Object> r:result){
			int rId = (int)r.get("rId");
			//自己本身
			addFinMerId(vals,rId,r);
			Integer gId = (Integer)r.get("groupRelationId");
			if(gId!=null){
				//合并而来，本资金号作为合并后的资金号的父类
				if(isBranch){
					addFinMerId(vals,rId,mapResult.get(gId));
				}else {
					addFinMerId(vals, gId, r);
				}
			}
			Integer sId = (Integer)r.get("splitRelationId");
			if(sId!=null){
				//拆分而来，拆分的资金号作为本资金号的父类
				if(isBranch){
					addFinMerId(vals,sId,r);
				}else {
					addFinMerId(vals, rId, mapResult.get(sId));
				}
			}
			String _claim = (String) r.get("claim");
			if (_claim.length() > 8) {
				//退货产生 (锐创) 原始订单 4082080 冲销订单 4082185 原始资金号 0新产生的资金号 741871
				String[] v = Tools.regExPickUp(_claim, "资金号[^\\d]{0,10}([\\d]+$)");
				if (v != null && v.length>0) {
					int _claimId = Tools.getInt(v[v.length-1].trim());
					if(_claimId>0) {
						//退货而来的新资金号，本资金号作为新资金号的父类
						if(isBranch){
							addFinMerId(vals, rId, mapResult.get(_claimId));
						}else {
							addFinMerId(vals, _claimId, r);
						}
						r.put("newRelationId", _claimId);
					}
					v = Tools.regExPickUp(_claim, "原始资金号[^\\d]{0,10}([\\d]+)");
					if (v != null && v.length>0) {
						_claimId = Tools.getInt(v[v.length - 1].trim());
						if (_claimId > 0) {
							//退货而来的原始资金号，原始资金号作为本资金号的父类
							if(isBranch){
								addFinMerId(vals,_claimId, mapResult.get( rId));
							}else {
								addFinMerId(vals, rId, mapResult.get(_claimId));
							}
						}
					}
				}else{
					//退款-对应流水[124194],对应流水[122984]
					v = Tools.regExPickUp(_claim, "退款[^\\d]{4,10}\\[([\\d]+)\\]$");
					if (v != null && v.length>0) {
						int _claimId = Tools.getInt(v[v.length - 1].trim());
						if (_claimId > 0) {
							//退款而来的原始资金号，本资金号作为原始资金号的父类
							if(isBranch){
								addFinMerId(vals, rId, mapResult.get(_claimId));
							}else {
								addFinMerId(vals, _claimId, r);
							}
						}
					}
				}
			}
		}
		return vals;
	}

	/**
	 * 获取资金流水的父节点集合
	 * @param ct9104
	 * @return
	 */
	private Map<String,Map<String,Object>> getFowDetails(CallTask9104Mapper ct9104){
		List<Map<String,Object>> result = ct9104.getFinMerDataSystemFlowDetail();
		return getFowDetails(result);
	}

	/**
	 * 获取资金流水的子节点集合
	 * @param ct9104
	 * @return
	 */
	private Map<String,Map<String,Object>> getFowDetailsBranch(CallTask9104Mapper ct9104){
		List<Map<String,Object>> result = ct9104.getFinMerDataSystemFlowDetailBranch();
		return getFowDetails(result);
	}

	/**
	 * List记录集合转为Map集合
	 * @param result
	 * @return
	 */
	private Map<String,Map<String,Object>> getFowDetails(List<Map<String,Object>> result){
		Map<String,Map<String,Object>> flows = new HashMap<>(result.size());
		for(Map<String,Object> row:result){
			flows.put(row.get("rId")+"_"+row.get("flowRid"),row);
		}
		return flows;
	}


	/**
	 * 每条记录有N条相关的记录列表
	 * @param vals 总的记录
	 * @param id	某个财务记录
	 * @param r		符合条件的记录
	 */
	private boolean addFinMerId(Map<Integer,Map<Integer,Map<String,Object>>> vals,int id,Map<String ,Object> r){
		if(r==null){
			return false;
		}
		//ID为rId标识，表示跟rId相关的记录有N条，以Map的形式保存List数据
		Map<Integer,Map<String,Object>> cs = vals.get(id);
		if(cs==null){
			cs = new HashMap<>();
			vals.put(id,cs);
		}
		cs.put((int)r.get("rId"),r);
		return true;
	}
}
