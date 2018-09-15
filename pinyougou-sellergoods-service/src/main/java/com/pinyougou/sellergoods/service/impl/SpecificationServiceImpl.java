package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加,保存规格以及规格选项
	 */
	@Override
	public void add(Specification specification) {
		//保存规格,由于保存规格选项时需要知道规格的ID,因此需要将规格的ID返回
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.insert(tbSpecification);
		//保存规格选项
		for(TbSpecificationOption option:specification.getSpecificationOptionList()){
			option.setSpecId(tbSpecification.getId());
			tbSpecificationOptionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//修改规格
		specificationMapper.updateByPrimaryKey(specification.getTbSpecification());
		//修改规格选项 先删除,再保存
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(specification.getTbSpecification().getId());
		tbSpecificationOptionMapper.deleteByExample(example);
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			tbSpecificationOption.setSpecId(specification.getTbSpecification().getId());
			tbSpecificationOptionMapper.insert(tbSpecificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification=new Specification();

		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		specification.setTbSpecification(tbSpecification);
		//获取规格选项列表
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(id);
		List<TbSpecificationOption> tbSpecificationOptionsList = tbSpecificationOptionMapper.selectByExample(example);
		specification.setSpecificationOptionList(tbSpecificationOptionsList);
		return specification;
	}

	/**
	 * 批量删除,删除规格的同时要把规格选项删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);
			tbSpecificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map<String, Object>> selectAll() {

		List<Map<String, Object>> list = specificationMapper.selectAll();
		return list;
	}

}
