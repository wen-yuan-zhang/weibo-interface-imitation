//package com.example.myapp.interacts;
//
//
//import com.mobilecourse.backend.dao.BlogDao;
//import com.mobilecourse.backend.dao.UserDao;
//import com.mobilecourse.backend.model.Blog;
//import com.mobilecourse.backend.util.RedisUtil;
//import com.mobilecourse.backend.util.UploadUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@EnableAutoConfiguration
//public class BlogController extends CommonController {
//
//    private BlogDao blogMapper;
//    private UserDao userMapper;
//    private RedisUtil redisUtil;
//    private UploadUtil uploadUtil;
//
//    @Autowired
//    public BlogController(BlogDao blogMapper, UserDao userMapper, RedisUtil redisUtil, UploadUtil uploadUtil) {
//        this.blogMapper = blogMapper;
//        this.userMapper = userMapper;
//        this.redisUtil = redisUtil;
//        this.uploadUtil = uploadUtil;
//    }
//
//    @RequestMapping(value = "/blog", method = {RequestMethod.POST})
//    public String createBlog(@RequestParam(value = "sessionId") String sessionId,
//                             @RequestParam(value = "content") String content,
//                             @RequestParam(value = "pictures", required = false) MultipartFile[] pictures,
//                             @RequestParam(value = "tags", required = false) String tags) {
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            return wrapperMsg(400, "未登陆或登陆已过期！");
//        }
//        if (content.length() > 512) {
//            return wrapperMsg(400, "内容长度超出限制！");
//        }
//        String pathStr = null;
//        if (pictures != null) {
//            if (pictures.length > 6) {
//                return wrapperMsg(400, "上传图片数量超出限制！");
//            } else if (pictures.length > 0) {
//                List<String> imageNames = new ArrayList<>();
//                StringBuilder sb = new StringBuilder();
//                boolean uploadSuccess = true;
//                Map<String, Object> result = null;
//                for (MultipartFile img : pictures) {
//                    result = uploadUtil.uploadImage(img, false);
//                    if ((Integer) result.get("code") == 200) {
//                        imageNames.add((String) result.get("newName"));
//                    } else {
//                        uploadSuccess = false;
//                        break;
//                    }
//                }
//                // 一张图片上传失败就整体失败
//                if (!uploadSuccess) {
//                    for (String name : imageNames) {
//                        uploadUtil.deleteImage(name, false);
//                    }
//                    return wrapperMsg((Integer) result.get("code"), (String) result.get("msg"));
//                }
//                for (String name : imageNames) {
//                    sb.append(name).append(" ");
//                }
//                pathStr = sb.toString().trim();
//            }
//        }
//        Timestamp time = new Timestamp(System.currentTimeMillis());
//        Blog blog = new Blog(userId, content, pathStr, time, tags);
//        blogMapper.insertBlog(blog);
//        return wrapperMsg(200, "操作成功！");
//    }
//
//
//    @RequestMapping(value = "/blog", method = {RequestMethod.DELETE})
//    public String deleteBlog(@RequestParam(value = "sessionId") String sessionId,
//                             @RequestParam(value = "blogId") int blogId) {
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            return wrapperMsg(400, "未登陆或登陆已过期！");
//        }
//        boolean isOwner = blogMapper.isBlogExist(userId, blogId);
//        if (!isOwner) {
//            return wrapperMsg(400, "没有操作权限！");
//        }
//        String picturePath = blogMapper.getBlogImagePath(blogId);
//        if (picturePath != null) {
//            String[] images = picturePath.split(" ");
//            for (String img : images) {
//                System.out.println(uploadUtil.deleteImage(img, false));
//            }
//        }
//        blogMapper.deleteBlog(blogId);
//        return wrapperMsg(200, "操作成功！");
//    }
//
//
//}
