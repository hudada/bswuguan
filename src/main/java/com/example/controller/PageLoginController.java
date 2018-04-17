package com.example.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.bean.AccountBean;
import com.example.bean.AdminBean;
import com.example.bean.BaseBean;
import com.example.bean.ForumAndReplyCount;
import com.example.bean.ForumBean;
import com.example.bean.NoticeBean;
import com.example.bean.PamentRecordBean;
import com.example.bean.ReplyBean;
import com.example.bean.UserBean;
import com.example.dao.AccountDao;
import com.example.dao.AdminDao;
import com.example.dao.ForumDao;
import com.example.dao.NoticeDao;
import com.example.dao.ReplyDao;
import com.example.dao.UserDao;
import com.example.dao.pamentRecordDao;
import com.example.utils.ResultUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.spring.web.json.Json;

import com.example.WebSecurityConfig;

@Controller
public class PageLoginController {

	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private pamentRecordDao payDao;
	@Autowired		
	private NoticeDao noticeDao;
	@Autowired
	private ForumDao forumDao;
	@Autowired
	private ReplyDao replyDao;

	// 返回登录页面
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(ModelMap map) {
		map.addAttribute("title","物业管理系统v1.0");
		return "newlogin";
	}

	// 登录接口
	@RequestMapping(value = "/dologin", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public BaseBean<String> userLogin(@RequestBody AdminBean adminBean, HttpSession session) {
		AdminBean admin = adminDao.findAdminBeanByNumber(adminBean.getAdminCode());
		if (admin != null) {
			if (admin.getPwd().equals(adminBean.getPwd())) {
				session.setAttribute(WebSecurityConfig.SESSION_KEY, adminBean);
				return ResultUtils.resultSucceed("登陆成功");
			} else {
				return ResultUtils.resultError("密码错误！");
			}
		} else {
			return ResultUtils.resultError("账户不存在！");
		}
	}

	// 退出登录接口
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String loginOut(HttpSession session) {
		session.removeAttribute(WebSecurityConfig.SESSION_KEY);
		return "redirect:/newlogin";
	}
	
	private ModelMap getPub(ModelMap map, HttpSession session,int position) {
		AdminBean admin = (AdminBean) session.getAttribute(WebSecurityConfig.SESSION_KEY);
		map.addAttribute("name", admin.getAdminCode());
		map.addAttribute("title","物业管理系统v1.0");
		map.addAttribute("left",""
				+ "<li>"
					+ "<a href=\"#\">"
					+ "<span class=\"badge badge-info pull-right\">"+userDao.count()+"</span>用户</a>"
				+ "</li>"
				+ "<li "+isActive(1,position)+">"
					+ "<a href=\"/userAdd\">"
					+ "<i class=\"icon-chevron-right\"></i>新增用户</a>"
				+ "</li>"
				+ "<li "+isActive(2,position)+">"
					+ "<a href=\"/table\">"
					+ "<i class=\"icon-chevron-right\"></i>用户管理</a>"
				+ "</li>"
				+ "<li>"
					+ "<a href=\"#\">"	
					+ "<span class=\"badge badge-info pull-right\">"+payDao.count()+"</span>缴费</a>"
				+ "</li>"
				+ "<li "+isActive(3,position)+">"
					+ "<a href=\"/record/0\">"
					+ "<i class=\"icon-chevron-right\"></i>新增水费记录</a>"
				+ "</li>"
				+ "<li "+isActive(4,position)+">"
					+ "<a href=\"/record/1\">"
					+ "<i class=\"icon-chevron-right\"></i>新增电费记录</a>"
				+ "</li>"
				+ "<li "+isActive(5,position)+">"
					+ "<a href=\"/record/2\">"
					+ "<i class=\"icon-chevron-right\"></i>新增燃气费记录</a>"
				+ "</li>"
				+ "<li "+isActive(6,position)+">"
					+ "<a href=\"/record/3\">"
					+ "<i class=\"icon-chevron-right\"></i>新增停车费记录</a>"
				+ "</li>"
				+ "<li "+isActive(7,position)+">"
					+ "<a href=\"/record/4\">"
					+ "<i class=\"icon-chevron-right\"></i>新增物管费记录</a>"
				+ "</li>"
				+ "<li "+isActive(8,position)+">"
					+ "<a href=\"/record/table\">"
					+ "<i class=\"icon-chevron-right\"></i>缴费管理</a>"
				+ "</li>"
				+ "<li>"
					+ "<a href=\"#\">"	
					+ "<span class=\"badge badge-info pull-right\">"+noticeDao.count()+"</span>公告</a>"
				+ "</li>"
				+ "<li "+isActive(9,position)+">"
					+ "<a href=\"/notice/add\">"
					+ "<i class=\"icon-chevron-right\"></i>新增公告</a>"
				+ "</li>"
				+ "<li "+isActive(10,position)+">"
					+ "<a href=\"/notice/table\">"
					+ "<i class=\"icon-chevron-right\"></i>公告管理</a>"
				+ "</li>"
				+ "<li>"
					+ "<a href=\"#\">"	
					+ "<span class=\"badge badge-info pull-right\">"+forumDao.count()+"</span>论坛</a>"
				+ "</li>"
				+ "<li "+isActive(11,position)+">"
					+ "<a href=\"/forum/table\">"
					+ "<i class=\"icon-chevron-right\"></i>论坛管理</a>"
				+ "</li>");
		return map;
	}

	private String isActive(int curr,int position) {
		return position==curr?"class=\"active\"":"";
	}

	// 返回管理首页
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap map, HttpSession session) {
		getPub(map, session,0);
		return "newindex";
	}

	@RequestMapping(value = "/userAdd", method = RequestMethod.GET)
	public String userAdd(ModelMap map, HttpSession session) {
		getPub(map, session,1);
		return "user/add";
	}
	
	@RequestMapping(value = "/edit/{number}", method = RequestMethod.GET)
	public String editPage(@PathVariable String number,ModelMap map, HttpSession session) {
		getPub(map, session,2);
		map.addAttribute("userBean", userDao.findUserByNumber(number));
		return "user/edit";
	}
	
	@RequestMapping(value = "/table", method = RequestMethod.GET)
	public String table(ModelMap map, HttpSession session) {
		getPub(map, session,2);
		map.addAttribute("list", userDao.findAll());
		return "user/table";
	}
	
	@RequestMapping(value = "/record/0", method = RequestMethod.GET)
	public String addPage0(ModelMap map, HttpSession session) {
		getPub(map, session,3);
		map.addAttribute("list",userDao.findAll());
		return "pamentRecord/add0";
	}
	@RequestMapping(value = "/record/1", method = RequestMethod.GET)
	public String addPage1(ModelMap map, HttpSession session) {
		getPub(map, session,4);
		map.addAttribute("list",userDao.findAll());
		return "pamentRecord/add1";
	}
	@RequestMapping(value = "/record/2", method = RequestMethod.GET)
	public String addPage2(ModelMap map, HttpSession session) {
		getPub(map, session,5);
		map.addAttribute("list",userDao.findAll());
		return "pamentRecord/add2";
	}
	@RequestMapping(value = "/record/3", method = RequestMethod.GET)
	public String addPage3(ModelMap map, HttpSession session) {
		getPub(map, session,6);
		map.addAttribute("list",userDao.findAll());
		return "pamentRecord/add3";
	}
	@RequestMapping(value = "/record/4", method = RequestMethod.GET)
	public String addPage4(ModelMap map, HttpSession session) {
		getPub(map, session,7);
		map.addAttribute("list",userDao.findAll());
		return "pamentRecord/add4";
	}
	
	@RequestMapping(value = "/record/table", method = RequestMethod.GET)
	public String tableRecord(ModelMap map, HttpSession session) {
		getPub(map, session,8);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<PamentRecordBean> list = payDao.findOrderByState();
		for(PamentRecordBean bean:list) {
			bean.setDate(format.format(new Date(Long.parseLong(bean.getDate()))));
		}
		map.addAttribute("list", list);
		return "pamentRecord/table";
	}
	
	@RequestMapping(value = "/notice/add", method = RequestMethod.GET)
	public String addPage(ModelMap map, HttpSession session) {
		getPub(map, session,9);
		return "notice/add";
	}
	
	@RequestMapping(value = "/notice/edit/{id}", method = RequestMethod.GET)
	public String noticeEditPage(@PathVariable String id,ModelMap map
			, HttpSession session) {
		getPub(map, session,10);
		map.addAttribute("noticeBean", noticeDao.findOne(Long.parseLong(id)));
		return "notice/edit";
	}
	
	@RequestMapping(value = "/notice/table", method = RequestMethod.GET)
	public String noticeTable(ModelMap map, HttpSession session) {
		getPub(map, session,10);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<NoticeBean> list = noticeDao.findAll();
		for(NoticeBean bean:list) {
			bean.setDate(format.format(new Date(Long.parseLong(bean.getDate()))));
		}
		map.addAttribute("list", list);
		return "notice/table";
	}
	
	@RequestMapping(value = "/forum/table", method = RequestMethod.GET)
	public String forumTable(ModelMap map, HttpSession session) {
		getPub(map, session,11);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ForumAndReplyCount> result = new ArrayList<>();
		List<ForumBean> forums=forumDao.findAll();
		for (ForumBean forumBean : forums) {
			ForumAndReplyCount forumAndReplyCount = new ForumAndReplyCount();
			forumAndReplyCount.setId(forumBean.getId());
			forumAndReplyCount.setTitle(forumBean.getTitle());
			forumAndReplyCount.setInfo(forumBean.getInfo());
			forumAndReplyCount.setNumber(forumBean.getNumber());
			forumAndReplyCount.setDate(format.format(new Date(Long.parseLong(forumBean.getDate()))));
			forumAndReplyCount.setState(forumBean.getState());
			int count = replyDao.findCountByForumId(forumBean.getId()+"");
			forumAndReplyCount.setCount(count);
			result.add(forumAndReplyCount);
		}
		map.addAttribute("list", result);
		return "forum/table";
	}
	
	@RequestMapping(value = "/reply/table/{id}", method = RequestMethod.GET)
	public String replyTable(@PathVariable String id,ModelMap map
			, HttpSession session) {
		getPub(map, session,11);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ReplyBean> list = replyDao.findReplayByForumId(id);
		for(ReplyBean bean:list) {
			bean.setDate(format.format(new Date(Long.parseLong(bean.getDate()))));
		}
		map.addAttribute("list", list);
		return "reply/table";
	}
}
