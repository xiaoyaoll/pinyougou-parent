package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {

        return tbBrandMapper.selectByExample(null);
    }



    /**
     * 分页查询品牌列表
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        //使用mybits分页插件pageHelper
        PageHelper.startPage(pageNum, pageSize);
        //查询
        Page page= (Page) tbBrandMapper.selectByExample(null);
        //封装数据
        PageResult pageResault=new PageResult(page.getTotal(),page);
        return pageResault;
    }

    /**
     * 添加品牌
     * @param tbBrand
     */
    @Override
    public void save(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    /**
     * 根据ID查询品牌
     * @param id
     */
    @Override
    public TbBrand selectOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌
     * @param tbBrand
     */
    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 删除品牌
     * @param ids
     */
    @Override
    public void delete(String[] ids) {
        for (String id : ids) {
            tbBrandMapper.deleteByPrimaryKey(Long.parseLong(id));
        }
    }

    /**
     * 按照条件查询品牌列表
     * @param tbBrand 封装查询条件的实体
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        TbBrandExample tbBrandExample=null;

        //查询条件判断筛选
        if(tbBrand!=null){
            tbBrandExample=new TbBrandExample();
            TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
            if(tbBrand.getName()!=null&&tbBrand.getName()!=""){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar()!=""){
                criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
            }
            //mybatis分页插件PageHelper分页
            PageHelper.startPage(pageNum,pageSize);
            Page rows= (Page) tbBrandMapper.selectByExample(tbBrandExample);
            return new PageResult(rows.getTotal(),rows);
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> selectAll() {
        return tbBrandMapper.selectAll();
    }
}
