package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobChain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Mapper
public interface XxlJobChainDao {

    int save(XxlJobChain xxlJobChain);

    int saveList(List<XxlJobChain> list);

    int update(XxlJobChain xxlJobChain);

    int updateStatus(@Param("id") Integer id, @Param("oldHandleCode") Integer oldHandleCode, @Param("newHandleCode") Integer newHandleCode);

    List<XxlJobChain> selectByLogId(Long logId);

    XxlJobChain selectById(Integer id);

}
