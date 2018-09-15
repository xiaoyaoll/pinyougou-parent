package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<TbContent> findAll(Long categoryId) {
		/**
		 * 利用缓存思路:先从缓存中查找,如果没有,则从数据库中查询,并且保存到缓存中去.
		 * 缓存刷新的思路:在对数据库进行了修改,删除,添加操作后,要把Redis中的缓存删除(也就是更新),把新的保存到Redis中去
		 */
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if(list==null){
			//从数据库查询
			TbContentExample example=new TbContentExample();
			TbContentExample.Criteria criteria = example.createCriteria();
			//根据ID查询不同分类
			criteria.andCategoryIdEqualTo(categoryId);
			//状态为有效
			criteria.andStatusEqualTo("0");
			example.setOrderByClause("sort_order");
			//保存到Redis缓存
			list=contentMapper.selectByExample(example);
			redisTemplate.boundHashOps("content").put(categoryId,list);
		}
		return list;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		//删除对应分类ID的数据
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());

		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		/**
		 * 修改分析:可能修改分类,也可能不修改分类,这两种情况都要考虑
		 */
		//先查出来
		Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		Long newCategoryId =content.getCategoryId();
		if(oldCategoryId.longValue()!=content.getCategoryId().longValue()){
			//修改了分类
			redisTemplate.boundHashOps("content").delete(newCategoryId);
		}
		redisTemplate.boundHashOps("content").delete(oldCategoryId);

		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbContent content = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		TbContentExample.Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
