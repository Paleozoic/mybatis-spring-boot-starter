package com.maxplus1.db.starter.config.common.page;

import com.maxplus1.db.starter.config.common.ReqData;
import org.apache.ibatis.session.RowBounds;

import javax.servlet.http.HttpServletRequest;

/**
 * 分页工具，处理ReqData，转化为分页参数：RowBounds
 */
public class PageUtils {

	/**
	 * 当前页
	 */
	public final static String DEFAULT_PARAMNAME_PAGENUM = "pageNum";
	/**
	 * 每页数量
	 */
	public final static String DEFAULT_PARAMNAME_PAGESIZE = "pageSize";

	public final static Integer DEFAULT_PAGE_NUM = 1;

	public final static Integer DEFAULT_PAGE_SIZE = 30;
	
	public static RowBounds buildRowBounds(HttpServletRequest req){
		/**
		 * <!-- 设置为true时，会将RowBounds第一个参数offset当成pageNum页码使用 -->
	     * <!-- 和startPage中的pageNum效果一样-->
	     * <property name="offsetAsPageNum" value="true"/>
		 */
		return new RowBounds(getPageNum(req), getPageSize(req));
	}

	private static int getPageNum(HttpServletRequest req){
		String pageCurrent = req.getParameter(DEFAULT_PARAMNAME_PAGENUM);
		try{
			int res = Integer.parseInt(pageCurrent);
			return res>0?res:DEFAULT_PAGE_NUM;
		}catch(Exception e){
			return DEFAULT_PAGE_NUM;
		}
	}
	
	private static int getPageSize(HttpServletRequest req){
		String pageSize = req.getParameter(DEFAULT_PARAMNAME_PAGESIZE);
		try{
			int res = Integer.parseInt(pageSize);
			return res>0?res:DEFAULT_PAGE_SIZE;
		}catch(Exception e){
			return DEFAULT_PAGE_SIZE;
		}
	}

	public static RowBounds buildRowBounds(ReqData reqData) {
		return new RowBounds(reqData.getPageNum(), reqData.getPageSize());
	}
}