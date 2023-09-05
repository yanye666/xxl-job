package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobChainLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Mapper
public interface XxlJobChainLogDao {

    int save(XxlJobChainLog xxlJobChainLog);

    int update(XxlJobChainLog xxlJobChainLog);

    XxlJobChainLog selectByExecutorLogId(Long executorLogId);

    List<XxlJobChainLog> selectByExecutorChainId(@Param("logId") Long logId, @Param("chainId") Integer chainId);

    int countByLogId(@Param("logId") Long logId, @Param("handleCode") Integer handleCode);

}
