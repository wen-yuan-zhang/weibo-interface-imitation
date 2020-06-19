//package com.example.myapp.interacts;
//
//import com.mobilecourse.backend.dao.UserDao;
//import com.mobilecourse.backend.model.User;
//import com.mobilecourse.backend.util.MailUtil;
//import com.mobilecourse.backend.util.RedisUtil;
//import com.mobilecourse.backend.util.UploadUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@RestController
//@EnableAutoConfiguration
//public class UserController extends CommonController {
//
//    private UserDao userMapper;
//    private RedisUtil redisUtil;
//    private MailUtil mailUtil;
//    private UploadUtil uploadUtil;
//
//    @Autowired
//    public UserController(UserDao userMapper, RedisUtil redisUtil, MailUtil mailUtil, UploadUtil uploadUtil) {
//        this.userMapper = userMapper;
//        this.redisUtil = redisUtil;
//        this.mailUtil = mailUtil;
//        this.uploadUtil = uploadUtil;
//    }
//
//
//    @RequestMapping(value = "/getCheckCode", method = {RequestMethod.GET})
//    public String sendCheckCode(@RequestParam(value = "mail") String mail) {
//        mail = mail.trim();
//        // 判断邮箱是否已经注册过
//        int count = userMapper.getUserCountGivenMail(mail);
//        if (count != 0) {
//            return wrapperMsg(400, "邮箱已经注册过，请直接登陆!");
//        }
//        // 判断是否已经发送过验证码
//        if (redisUtil.hget("checkCode", mail) != null) {
//            return wrapperMsg(400, "验证码已经发送过，请查看邮箱！");
//        } else {
//            // 产生验证码，然后发送
//            System.out.println(mail);
//            String checkCode = RandomStringUtils.randomAlphanumeric(6);
//            boolean success = mailUtil.send(mail, "验证码", checkCode);
//            if (success) {
//                redisUtil.hset("checkCode", mail, checkCode, 5 * 60);
//                return wrapperMsg(200, "验证码已发送，有效时间为5min，请及时查看邮箱！");
//            } else {
//                return wrapperMsg(400, "验证码发送失败，请确保邮箱地址有效");
//            }
//        }
//    }
//
//    // 进行用户的注册
//    @RequestMapping(value = "/user", method = {RequestMethod.POST})
//    public String registerUser(@RequestParam(value = "nickName") String nickName,
//                               @RequestParam(value = "realName") String realName,
//                               @RequestParam(value = "isMale") Boolean isMale,
//                               @RequestParam(value = "age") Integer age,
//                               @RequestParam(value = "isTeacher") Boolean isTeacher,
//                               @RequestParam(value = "school") String school,
//                               @RequestParam(value = "department") String department,
//                               @RequestParam(value = "mail") String mail,
//                               @RequestParam(value = "password") String password,
//                               @RequestParam(value = "checkCode") String checkCode) {
//        // 检验验证码是否正确
//        String rightCheckCode = (String) redisUtil.hget("checkCode", mail);
//
//        if (rightCheckCode == null) {
//            return wrapperMsg(400, "验证码不存在或已过期！");
//        } else if (!rightCheckCode.equals(checkCode)) {
//            return wrapperMsg(400, "验证码错误！");
//        }
//
//        User user = new User(nickName, realName, isMale, isTeacher, age, school, department, mail, password);
//        if (!user.isValidUser()) {
//            return wrapperMsg(400, "不合法的用户字段，请重试！");
//        }
//
//        try {
//            this.userMapper.insertUser(user);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return wrapperMsg(500, "服务器错误！");
//        }
//
//        return wrapperMsg(200, "注册成功！");
//    }
//
//    // 用户进行登陆
//    @RequestMapping(value = "/login", method = {RequestMethod.POST})
//    public Map<String, Object> Login(@RequestParam(value = "mail") String mail,
//                                     @RequestParam(value = "password") String password) {
//
//        mail = mail.trim();
//        password = password.trim();
//        Map<String, Object> resultMap = new HashMap<>();
//        Integer userId;
//
//        try {
//            userId = this.userMapper.getUserIdGivenMailandPwd(mail, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultMap.put("code", 500);
//            resultMap.put("msg", "服务器错误!");
//            return resultMap;
//        }
//
//        if (userId != null) {
//            if (redisUtil.hget("loginMail", mail) != null) {
//                resultMap.put("code", 400);
//                resultMap.put("msg", "您已经登陆过，请勿重复登陆!");
//                return resultMap;
//            } else {
//                String sessionId;
//                while (true) {
//                    sessionId = RandomStringUtils.randomAlphanumeric(16);
//                    if (redisUtil.hget("sessionId", sessionId) == null) {
//                        redisUtil.hset("sessionId", sessionId, userId, 30 * 24 * 60 * 60);
//                        redisUtil.hset("loginMail", mail, 0, 30 * 24 * 60 * 60);
//                        break;
//                    }
//                }
//                resultMap.put("code", 200);
//                resultMap.put("msg", "成功登陆!");
//                resultMap.put("sessionId", sessionId);
//                return resultMap;
//            }
//        } else {
//            resultMap.put("code", 400);
//            resultMap.put("msg", "密码错误或用户不存在！");
//            return resultMap;
//        }
//    }
//
//    // 获得用户的主页信息
//    @RequestMapping(value = "/user/mainpage", method = {RequestMethod.GET})
//    public Map<String, Object> MainPage(@RequestParam(value = "id") int id,
//                                        @RequestParam(value = "sessionId") String sessionId) {
//        Map<String, Object> resultMap = new HashMap<>();
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            resultMap.put("code", 400);
//            resultMap.put("msg", "未登陆或登陆已过期");
//            return resultMap;
//        }
//        int targetId;
//        if (id == -1) {
//            targetId = userId;
//        } else {
//            targetId = id;
//            boolean userExist = this.userMapper.isUserExist(targetId);
//            // 判断查询的用户是否存在
//            if (!userExist) {
//                resultMap.put("code", 400);
//                resultMap.put("msg", "查询的用户不存在！");
//                return resultMap;
//            }
//        }
//        Map<String, Object> infoMap = this.userMapper.getUserMainPageInfo(targetId);
//        resultMap.put("code", 200);
//        resultMap.put("info", infoMap);
//        return resultMap;
//    }
//
//    // 修改用户信息
//    @RequestMapping(value = "/user", method = {RequestMethod.PUT})
//    public String updateUserInfo(@RequestParam(value = "sessionId") String sessionId,
//                                 @RequestParam(value = "nickName", required = false) String nickName,
//                                 @RequestParam(value = "signature", required = false) String signature,
//                                 @RequestParam(value = "realName", required = false) String realName,
//                                 @RequestParam(value = "age", required = false) Integer age,
//                                 @RequestParam(value = "school", required = false) String school,
//                                 @RequestParam(value = "department", required = false) String department,
//                                 @RequestParam(value = "isMale", required = false) Boolean isMale,
//                                 @RequestParam(value = "isTeacher", required = false) Boolean isTeacher,
//                                 @RequestParam(value = "interest", required = false) String interest,
//                                 @RequestParam(value = "experience", required = false) String experience) {
//
//        sessionId = sessionId.trim();
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            return wrapperMsg(400, "未登陆或登陆已过期!");
//        }
//        User user = new User(userId, nickName, signature, realName, isMale, isTeacher,
//                age, school, department, interest, experience);
//        if (!user.isValidUpdate()) {
//            return wrapperMsg(400, "修改不合法！");
//        }
//
//        try {
//            this.userMapper.updateUserInfo(user);
//        } catch (Exception e) {
//            return wrapperMsg(500, "服务器错误！");
//        }
//
//        return wrapperMsg(200, "用户信息修改成功！");
//    }
//
//
//    // 关注 或者 取消关注
//    @RequestMapping(value = "/follow", method = {RequestMethod.PUT})
//    public String followAnotherUser(@RequestParam(value = "sessionId") String sessionId,
//                                    @RequestParam(value = "id") int id) {
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            return wrapperMsg(400, "未登录或登陆已过期!");
//        }
//        if (userId == id) {
//            return wrapperMsg(400, "不可以关注自己!");
//        }
//        Boolean isUserExist = userMapper.isUserExist(id);
//        if (!isUserExist) {
//            return wrapperMsg(400, "关注/取消关注的用户不存在!");
//        }
//        Boolean hasFollowed = userMapper.hasFollowedSomebody(userId, id);
//        if (hasFollowed) {
//            userMapper.unfollowSomebody(userId, id);
//        } else {
//            userMapper.followSomebody(userId, id);
//        }
//        return wrapperMsg(200, "操作成功！");
//    }
//
//    // follow true --> 关注的人列表 false --> 粉丝列表
//    @RequestMapping(value = "/list/follow", method = {RequestMethod.GET})
//    public Map<String, Object> getFollowList(@RequestParam(value = "sessionId") String sessionId,
//                                             @RequestParam(value = "follow", required = false, defaultValue = "true") boolean follow) {
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        Map<String, Object> resultMap = new HashMap<>();
//        if (userId == null) {
//            resultMap.put("code", 400);
//            resultMap.put("msg", "未登陆或登陆已过期!");
//            return resultMap;
//        }
//        List<Integer> resultList;
//        if (follow) {
//            resultList = userMapper.getFollowingId(userId);
//        } else {
//            resultList = userMapper.getFollowerId(userId);
//        }
//        resultMap.put("code", 200);
//        resultMap.put("msg", "操作成功!");
//        resultMap.put("list", resultList);
//        return resultMap;
//    }
//
//
//    @RequestMapping(value = "/upload/profile", method = {RequestMethod.PUT})
//    public Map<String, Object> uploadProfile(@RequestParam(value = "sessionId") String sessionId,
//                                             @RequestParam(value = "profile") MultipartFile profile) {
//        Map<String, Object> resultMap = new HashMap<>();
//        Integer userId = (Integer) redisUtil.hget("sessionId", sessionId);
//        if (userId == null) {
//            resultMap.put("code", 400);
//            resultMap.put("msg", "未登陆或登陆已过期！");
//            return resultMap;
//        }
//        // 上传
//        resultMap = uploadUtil.uploadImage(profile, true);
//        // 删除掉原先的文件
//        if ((Integer) resultMap.get("code") == 200) {
//            String oldName = userMapper.getProfilePath(userId);
//            if (oldName != null) {
//                uploadUtil.deleteImage(oldName, true);
//            }
//            String newName = (String) resultMap.get("newName");
//            resultMap.remove("newName");
//            userMapper.setProfilePath(userId, newName);
//        }
//        return resultMap;
//    }
//
//}
