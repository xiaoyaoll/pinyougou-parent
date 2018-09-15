package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import entity.Result;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}


	@RequestMapping("/selectAll")
	public List<Map<String, Object>> selectAll(){
		return brandService.selectAll();
	}
	/**
	 * 分页查询品牌列表
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int pageNum, int pageSize){
		return brandService.findPage(pageNum,pageSize);
	}

	/**
	 * 新增品牌和修改品牌列表
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public Result saveOrUpdate(@RequestBody TbBrand tbBrand){
		try{
			if(tbBrand.getId()==null){
				brandService.save(tbBrand);
			}else{
				brandService.update(tbBrand);
			}
			Result msg=new Result(true,"操作成功");
			return msg;
		}catch (Exception e){
			Result msg=new Result(false,"操作失败");
			return msg;
		}
	}

	/**
	 * 根据ID查询品牌
	 * @param id
	 * @return
	 */
	@RequestMapping("/selectOne")
	public TbBrand selectOne(String id){
		return brandService.selectOne(Long.parseLong(id));
	}

	/**
	 * 根据ID删除品牌
	 * @param ids ID数组
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody String[] ids){
		try {
			brandService.delete(ids);
			Result msg=new Result(true,"操作成功");
			return msg;
		}catch (Exception e){
			Result msg=new Result(false,"操作失败");
			return msg;
		}
	}

	/**
	 * 分页条件查询品牌列表
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand, int pageNum, int pageSize){
		return brandService.findPage(tbBrand,pageNum,pageSize);
	}


}
