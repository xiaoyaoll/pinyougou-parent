package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    /**
     * 分页查询品牌列表
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 结果集
     */
    public PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加品牌列表
     * @param tbBrand
     */
    public void save(TbBrand tbBrand);

    /**
     * 根据ID查询单个品牌
     * @param id
     */
    public TbBrand selectOne(long id);

    /**
     * 修改品牌
     * @param tbBrand
     */
    public void update(TbBrand tbBrand);

    /**
     * 删除品牌
     * @param ids
     */
    public void delete(String[] ids);

    /**
     * 按条件查询品牌列表,方法的重载
     * @param tbBrand 封装查询条件的实体
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 返回查询结果
     */
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize);

    public List<Map<String, Object>> selectAll();
}
