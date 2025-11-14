package com.app.webnest.api.privateapi;

import com.app.webnest.domain.dto.ApiResponseDTO;
import com.app.webnest.domain.dto.CommentNotificationDTO;
import com.app.webnest.domain.dto.FollowNotificationDTO;
import com.app.webnest.domain.dto.NotificationResponseDTO;
import com.app.webnest.domain.dto.PostNotificationDTO;
import com.app.webnest.domain.vo.CommentNotificationVO;
import com.app.webnest.domain.vo.FollowNotificationVO;
import com.app.webnest.domain.vo.PostNotificationVO;
import com.app.webnest.service.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/notification/*")
public class NotificationApi {
    private final NotificationServiceImpl notificationService;

    @PostMapping("get-notification")
    public ResponseEntity<ApiResponseDTO> getNotification(@RequestBody Long userId){
        Map<String, Object> result = new HashMap<>();
        List<PostNotificationDTO> foundPosts = notificationService.getPostNotificationByUserId(userId);
        List<FollowNotificationDTO> foundFollows = notificationService.getFollowNotificationByUserId(userId);
        List<CommentNotificationDTO>  foundComments = notificationService.getCommentNotificationByUserId(userId);

        result.put("posts", foundPosts);
        result.put("follows", foundFollows);
        result.put("comments", foundComments);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("newNotifications", result));
    }

    @PostMapping("post/insert")
    public ResponseEntity<ApiResponseDTO> insertPostNotification(@RequestBody PostNotificationVO postNotificationVO){
        notificationService.addPostNotification(postNotificationVO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete insert post notification"));
    }

    @PostMapping("comment/insert")
    public ResponseEntity<ApiResponseDTO> insertCommentNotification(@RequestBody CommentNotificationVO commentNotificationVO){
        notificationService.addCommentNotification(commentNotificationVO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete insert comment notification"));
    }

    @PostMapping("follow/insert")
    public ResponseEntity<ApiResponseDTO> insertFollowNotification(@RequestBody FollowNotificationVO followNotificationVO){
        notificationService.addFollowNotification(followNotificationVO);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete insert follow notification"));
    }
    @PutMapping("/post/modify")
    public ResponseEntity<ApiResponseDTO> modifyPostNotification(@RequestBody Long id){
        notificationService.modifyPostNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("completed read one post notification"));
    }
    @PutMapping("/comment/modify")
    public ResponseEntity<ApiResponseDTO> modifyCommentNotification(@RequestBody Long id){
        notificationService.modifyCommentNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("completed read one comment notification"));
    }
    @PutMapping("/follow/modify")
    public ResponseEntity<ApiResponseDTO> modifyFollowNotification(@RequestBody Long id){
        notificationService.modifyFollowNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("completed read one follow notification"));
    }

    @PutMapping("modify-all/{receiverUserId}")
    public ResponseEntity<ApiResponseDTO> modifyAllNotification(@PathVariable Long receiverUserId){
        notificationService.modifyEveryCommentNotification(receiverUserId);
        notificationService.modifyEveryFollowNotification(receiverUserId);
        notificationService.modifyEveryPostNotification(receiverUserId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete read all"));
    }

    @DeleteMapping("post/delete")
    public ResponseEntity<ApiResponseDTO> deletePostNotification(@RequestBody Long id){
        notificationService.removePostNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete remove post notification"));
    }
    @DeleteMapping("comment/delete")
    public ResponseEntity<ApiResponseDTO> deleteCommentNotification(@RequestBody Long id){
        notificationService.removeCommentNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete remove comment notification"));
    }
    @DeleteMapping("follow/delete")
    public ResponseEntity<ApiResponseDTO> deleteFollowNotification(@RequestBody Long id){
        notificationService.removeFollowNotification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete remove follow notification"));
    }

    @DeleteMapping("delete-all/{receiverUserId}")
    public ResponseEntity<ApiResponseDTO> deleteAllNotification(@PathVariable Long receiverUserId){
        notificationService.removeEveryPostNotification(receiverUserId);
        notificationService.removeEveryFollowNotification(receiverUserId);
        notificationService.removeEveryCommentNotification(receiverUserId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("complete remove all"));
    }
}
