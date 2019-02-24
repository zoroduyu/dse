package com.duy.dse.dao;

import java.util.List;

/**
 * 基类 定义一些常用的公共方法
 *
 * @author wang.zhang
 */
public interface BaseDao<T> {
    /**
     * 根据主键删除
     *
     * @param id 主键id
     */
    void deleteByPrimaryKey(Long id);

    /**
     * 增加
     *
     * @param entity 数据实体
     */
    void insertBySelective(T entity);

    /**
     * 根据主键查询
     *
     * @param id 主键id
     * @return 数据对象
     */
    T selectByPrimaryKey(Long id);

    /**
     * 根据条件查询对象集合
     *
     * @param query 查询条件
     * @return 对象集合
     */
    List<T> selectBySelective(T query);

    /**
     * 根据主键更新实体
     *
     * @param entity 实体对象
     */
    void updateByPrimaryKeySelective(T entity);
}
