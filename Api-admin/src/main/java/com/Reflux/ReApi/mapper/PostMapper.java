package com.Reflux.ReApi.mapper;

import com.Reflux.ReApi.model.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 *
 * @author Aaron
 */
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

}



