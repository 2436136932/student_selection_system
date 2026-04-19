package com.example.studentselectionsystem.controller;

import com.example.studentselectionsystem.entity.ChatMessage;
import com.example.studentselectionsystem.entity.ChatSession;
import com.example.studentselectionsystem.entity.User;
import com.example.studentselectionsystem.service.ChatMessageService;
import com.example.studentselectionsystem.service.ChatSessionService;
import com.example.studentselectionsystem.service.FileService;
import com.example.studentselectionsystem.service.StudentService;
import com.example.studentselectionsystem.service.TeacherService;
import com.example.studentselectionsystem.service.UserService;
import com.example.studentselectionsystem.websocket.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
    
    @Autowired
    private FileService fileService;

    /**
     * 获取当前用户ID
     * @return 当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            try {
                String username = authentication.getName();
                Optional<User> userOptional = userService.findUserByUsername(username);
                if (userOptional.isPresent()) {
                    return userOptional.get().getId();
                }
            } catch (Exception e) {
                System.err.println("获取当前用户ID失败: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取当前用户角色
     * @return 当前用户角色
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            try {
                String username = authentication.getName();
                Optional<User> userOptional = userService.findUserByUsername(username);
                if (userOptional.isPresent()) {
                    return userOptional.get().getRole();
                }
            } catch (Exception e) {
                System.err.println("获取当前用户角色失败: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * 获取所有用户列表，排除当前用户
     * @return 用户列表
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsersExceptCurrent() {
        try {
            System.out.println("===== 进入getAllUsersExceptCurrent()方法 =====");
            
            Long currentUserId = getCurrentUserId();
            System.out.println("当前用户ID: " + currentUserId);
            
            // 使用userService查询所有用户
            System.out.println("使用userService查询所有用户...");
            List<User> users = userService.findAllUsers();
            System.out.println("userService查询获取到的用户列表数量: " + users.size());
            
            // 打印所有用户详情
            for (User user : users) {
                System.out.println("用户详情: ID=" + user.getId() + ", 用户名=" + user.getUsername() + ", 角色=" + user.getRole() + ", 真实姓名=" + user.getRealName() + ", 状态=" + user.getStatus());
            }
            
            List<Map<String, Object>> userList = new ArrayList<>();
            
            for (User user : users) {
                System.out.println("处理用户: ID=" + user.getId() + ", 用户名=" + user.getUsername() + ", 角色=" + user.getRole());
                
                if (user.getId().equals(currentUserId)) {
                    System.out.println("跳过当前用户: " + user.getUsername());
                    continue;
                }
                
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("realName", user.getRealName());
                userMap.put("role", user.getRole());
                userMap.put("avatar", user.getAvatar());
                
                // 根据用户角色添加额外信息
                if ("teacher".equals(user.getRole())) {
                    System.out.println("处理教师用户: " + user.getUsername());
                    try {
                        Optional<com.example.studentselectionsystem.entity.Teacher> teacherOptional = teacherService.findTeacherByUserId(user.getId());
                        System.out.println("教师查询结果: " + (teacherOptional.isPresent() ? "找到教师信息" : "未找到教师信息"));
                        
                        if (teacherOptional.isPresent()) {
                            com.example.studentselectionsystem.entity.Teacher teacher = teacherOptional.get();
                            System.out.println("教师学院: " + teacher.getDepartment());
                            userMap.put("department", teacher.getDepartment());
                        } else {
                            System.out.println("未找到教师信息，用户ID: " + user.getId());
                        }
                    } catch (Exception e) {
                        System.err.println("获取教师信息失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if ("student".equals(user.getRole())) {
                    System.out.println("处理学生用户: " + user.getUsername());
                    try {
                        Optional<com.example.studentselectionsystem.entity.Student> studentOptional = studentService.findStudentByUserId(user.getId());
                        System.out.println("学生查询结果: " + (studentOptional.isPresent() ? "找到学生信息" : "未找到学生信息"));
                        
                        if (studentOptional.isPresent()) {
                            com.example.studentselectionsystem.entity.Student student = studentOptional.get();
                            System.out.println("学生专业: " + student.getMajor() + ", 班级: " + student.getClassName() + ", 学号: " + student.getStudentNumber());
                            userMap.put("major", student.getMajor());
                            userMap.put("className", student.getClassName());
                            userMap.put("studentNumber", student.getStudentNumber());
                        } else {
                            System.out.println("未找到学生信息，用户ID: " + user.getId());
                        }
                    } catch (Exception e) {
                        System.err.println("获取学生信息失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("处理其他角色用户: " + user.getRole());
                }
                
                userList.add(userMap);
                System.out.println("用户处理完成: " + userMap);
            }
            
            System.out.println("最终返回的用户列表数量: " + userList.size());
            System.out.println("===== 退出getAllUsersExceptCurrent()方法 =====");
            
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("getAllUsersExceptCurrent()方法异常: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建或获取聊天会话
     * @param request 请求体，包含接收者ID
     * @return 创建或获取的聊天会话
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createOrGetChatSession(@RequestBody Map<String, Long> request) {
        try {
            Long currentUserId = getCurrentUserId();
            Long receiverId = request.get("receiverId");
            
            if (receiverId == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            
            // 检查是否已存在会话
            Optional<ChatSession> existingSession = chatSessionService.findChatSessionByUserIds(currentUserId, receiverId);
            if (existingSession.isPresent()) {
                return new ResponseEntity<>(existingSession.get(), HttpStatus.OK);
            }
            
            // 创建新会话
            ChatSession chatSession = new ChatSession();
            chatSession.setUser1Id(currentUserId);
            chatSession.setUser2Id(receiverId);
            chatSession.setStatus("active");
            chatSession.setLastMessage("开始聊天吧！");
            chatSession.setLastMessageTime(new Date());
            
            ChatSession createdChatSession = chatSessionService.createChatSession(chatSession);
            return new ResponseEntity<>(createdChatSession, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新聊天会话
     * @param id 会话ID
     * @param chatSession 聊天会话信息
     * @return 更新后的聊天会话
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/sessions/{id}")
    public ResponseEntity<ChatSession> updateChatSession(@PathVariable("id") Long id, @RequestBody ChatSession chatSession) {
        try {
            ChatSession updatedChatSession = chatSessionService.updateChatSession(id, chatSession);
            if (updatedChatSession != null) {
                return new ResponseEntity<>(updatedChatSession, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 关闭聊天会话
     * @param id 会话ID
     * @return 响应状态
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/sessions/{id}/close")
    public ResponseEntity<HttpStatus> closeChatSession(@PathVariable("id") Long id) {
        try {
            chatSessionService.closeChatSession(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID查找聊天会话
     * @param id 会话ID
     * @return 聊天会话信息
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/sessions/{id}")
    public ResponseEntity<ChatSession> findChatSessionById(@PathVariable("id") Long id) {
        Optional<ChatSession> chatSession = chatSessionService.findChatSessionById(id);
        if (chatSession.isPresent()) {
            return new ResponseEntity<>(chatSession.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取当前用户的聊天会话列表
     * @return 聊天会话列表
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/sessions")
    public ResponseEntity<List<Map<String, Object>>> getCurrentUserChatSessions() {
        try {
            System.out.println("===== 进入getCurrentUserChatSessions()方法 =====");
            
            Long currentUserId = getCurrentUserId();
            System.out.println("当前用户ID: " + currentUserId);
            
            List<ChatSession> chatSessions;

            // 所有用户都获取自己的聊天会话
            chatSessions = chatSessionService.findChatSessionsByUserId(currentUserId);
            System.out.println("获取到的聊天会话数量: " + chatSessions.size());

            // 转换为包含名称的响应对象
            List<Map<String, Object>> responseSessions = new ArrayList<>();
            
            for (ChatSession session : chatSessions) {
                System.out.println("处理会话: ID=" + session.getId() + ", 用户1=" + session.getUser1Id() + ", 用户2=" + session.getUser2Id());
                
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("id", session.getId());
                sessionMap.put("user1Id", session.getUser1Id());
                sessionMap.put("user2Id", session.getUser2Id());
                sessionMap.put("status", session.getStatus());
                sessionMap.put("lastMessage", session.getLastMessage());
                sessionMap.put("lastMessageTime", session.getLastMessageTime());
                sessionMap.put("createdAt", session.getCreatedAt());
                sessionMap.put("updatedAt", session.getUpdatedAt());
                
                // 添加未读消息数量
                long unreadCount = chatMessageService.countUnreadMessagesBySessionIdAndUserId(session.getId(), currentUserId);
                sessionMap.put("unreadCount", unreadCount);
                System.out.println("会话 " + session.getId() + " 的未读消息数量: " + unreadCount);
                
                // 添加对方用户的信息
                Long otherUserId = session.getUser1Id().equals(currentUserId) ? session.getUser2Id() : session.getUser1Id();
                System.out.println("对方用户ID: " + otherUserId);
                
                Optional<User> otherUser = userService.findUserById(otherUserId);
                System.out.println("对方用户查询结果: " + (otherUser.isPresent() ? "找到用户信息" : "未找到用户信息"));
                
                if (otherUser.isPresent()) {
                    User user = otherUser.get();
                    sessionMap.put("otherUserId", otherUserId);
                    sessionMap.put("otherUserName", user.getUsername());
                    sessionMap.put("otherUserRealName", user.getRealName());
                    sessionMap.put("otherUserRole", user.getRole());
                    
                    System.out.println("对方用户角色: " + user.getRole());
                    
                    // 根据用户角色添加额外信息
                    if ("teacher".equals(user.getRole())) {
                        System.out.println("处理教师用户: " + user.getUsername());
                        try {
                            Optional<com.example.studentselectionsystem.entity.Teacher> teacherOptional = teacherService.findTeacherByUserId(user.getId());
                            System.out.println("教师查询结果: " + (teacherOptional.isPresent() ? "找到教师信息" : "未找到教师信息"));
                            
                            if (teacherOptional.isPresent()) {
                                com.example.studentselectionsystem.entity.Teacher teacher = teacherOptional.get();
                                System.out.println("教师学院: " + teacher.getDepartment());
                                sessionMap.put("otherUserDepartment", teacher.getDepartment());
                            } else {
                                System.out.println("未找到教师信息，用户ID: " + user.getId());
                            }
                        } catch (Exception e) {
                            System.err.println("获取教师信息失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else if ("student".equals(user.getRole())) {
                        System.out.println("处理学生用户: " + user.getUsername());
                        try {
                            Optional<com.example.studentselectionsystem.entity.Student> studentOptional = studentService.findStudentByUserId(user.getId());
                            System.out.println("学生查询结果: " + (studentOptional.isPresent() ? "找到学生信息" : "未找到学生信息"));
                            
                            if (studentOptional.isPresent()) {
                                com.example.studentselectionsystem.entity.Student student = studentOptional.get();
                                System.out.println("学生专业: " + student.getMajor() + ", 班级: " + student.getClassName() + ", 学号: " + student.getStudentNumber());
                                sessionMap.put("otherUserMajor", student.getMajor());
                                sessionMap.put("otherUserClassName", student.getClassName());
                                sessionMap.put("otherUserStudentNumber", student.getStudentNumber());
                            } else {
                                System.out.println("未找到学生信息，用户ID: " + user.getId());
                            }
                        } catch (Exception e) {
                            System.err.println("获取学生信息失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("处理其他角色用户: " + user.getRole());
                    }
                } else {
                    sessionMap.put("otherUserId", otherUserId);
                    sessionMap.put("otherUserName", "用户" + otherUserId);
                    System.out.println("未找到对方用户信息，使用默认名称");
                }
                
                responseSessions.add(sessionMap);
                System.out.println("会话处理完成: " + sessionMap);
            }
            
            System.out.println("最终返回的会话列表数量: " + responseSessions.size());
            System.out.println("===== 退出getCurrentUserChatSessions()方法 =====");

            return new ResponseEntity<>(responseSessions, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("getCurrentUserChatSessions()方法异常: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 发送聊天消息
     * @param chatMessage 聊天消息信息
     * @return 发送的聊天消息
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendChatMessage(@RequestBody ChatMessage chatMessage) {
        try {
            ChatMessage sentChatMessage = chatMessageService.sendChatMessage(chatMessage);
            return new ResponseEntity<>(sentChatMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据会话ID获取聊天消息
     * @param sessionId 会话ID
     * @return 聊天消息列表
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/messages/session/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatMessagesBySessionId(@PathVariable("sessionId") Long sessionId) {
        try {
            List<ChatMessage> chatMessages = chatMessageService.findChatMessagesBySessionId(sessionId);
            // 标记消息为已读
            Long currentUserId = getCurrentUserId();
            chatMessageService.markAllMessagesAsRead(sessionId, currentUserId);
            return new ResponseEntity<>(chatMessages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 响应状态
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<HttpStatus> markMessageAsRead(@PathVariable("messageId") Long messageId) {
        try {
            chatMessageService.markMessageAsRead(messageId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 标记会话中所有消息为已读
     * @param sessionId 会话ID
     * @return 响应状态
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/sessions/{sessionId}/read-all")
    public ResponseEntity<HttpStatus> markAllMessagesAsRead(@PathVariable("sessionId") Long sessionId) {
        try {
            Long currentUserId = getCurrentUserId();
            chatMessageService.markAllMessagesAsRead(sessionId, currentUserId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有聊天消息（管理员）
     * @return 聊天消息列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllChatMessages() {
        try {
            List<ChatMessage> chatMessages = chatMessageService.findAllChatMessages();
            return new ResponseEntity<>(chatMessages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取当前用户的未读消息总数
     * @return 未读消息总数
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount() {
        try {
            Long currentUserId = getCurrentUserId();
            long unreadCount = chatMessageService.countUnreadMessagesByUserId(currentUserId);
            Map<String, Long> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取当前在线用户ID列表
     * @return 在线用户ID列表
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/online-users")
    public ResponseEntity<List<String>> getOnlineUsers() {
        try {
            List<String> onlineUserIds = chatWebSocketHandler.getOnlineUserIds();
            return new ResponseEntity<>(onlineUserIds, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取当前在线人数
     * @return 在线人数
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/online-count")
    public ResponseEntity<Map<String, Integer>> getOnlineCount() {
        try {
            int count = chatWebSocketHandler.getOnlineUserCount();
            Map<String, Integer> response = new HashMap<>();
            response.put("onlineCount", count);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件信息
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/files/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(file);
            Long fileSize = file.getSize();
            String fileName = file.getOriginalFilename();
            
            Map<String, Object> response = new HashMap<>();
            response.put("fileUrl", fileUrl);
            response.put("fileName", fileName);
            response.put("fileSize", fileSize);
            response.put("message", "文件上传成功");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 上传图片
     * @param file 要上传的图片文件
     * @return 图片信息
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/files/upload-image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadImage(file);
            Long fileSize = file.getSize();
            String fileName = file.getOriginalFilename();
            
            Map<String, Object> response = new HashMap<>();
            response.put("fileUrl", fileUrl);
            response.put("fileName", fileName);
            response.put("fileSize", fileSize);
            response.put("message", "图片上传成功");
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 下载文件
     * @param fileUrl 文件URL
     * @return 文件流
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/files/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // 获取文件输入流
            InputStream inputStream = fileService.downloadFile(fileUrl);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileService.getFileSize(fileUrl)));
            
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}