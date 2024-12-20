package com.daybyday.postingBoard.controller;

import com.daybyday.postingBoard.dto.PostDTO;
import com.daybyday.postingBoard.dto.UserDTO;
import com.daybyday.postingBoard.dto.response.CommonResponse;
import com.daybyday.postingBoard.service.impl.PostServiceImpl;
import com.daybyday.postingBoard.service.impl.UserServiceImpl;
import com.daybyday.postingBoard.aop.LoginCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
/* API내용 확인 하는 부분: https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html
* 매개변수 @RequestBody https://velog.io/@tco0427/RequestParam%EA%B3%BC-RequestBody*/
@RestController
@RequestMapping("/posts")
@Log4j2
public class PostController {

    private final PostServiceImpl postService;
    private final UserServiceImpl userService;

    //PostController 생성자
    public PostController(PostServiceImpl postService, UserServiceImpl userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDTO>> registerPost(String accountId, @RequestBody PostDTO postDTO) {
        postService.register(accountId, postDTO);
        CommonResponse commonResponse = new CommonResponse<>(HttpStatus.OK, "SUCCESS", "registerPost", postDTO);
        return ResponseEntity.ok(commonResponse);
    }

    @GetMapping("my-posts")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<List<PostDTO>>> myPostInfo(String accountId) {
        UserDTO memberInfo = userService.getUserInfo(accountId);
        List<PostDTO> postDTOList = postService.getMyProducts(memberInfo.getId());
        CommonResponse commonResponse = new CommonResponse<>(HttpStatus.OK, "SUCCESS", "myPostInfo", postDTOList);
        return ResponseEntity.ok(commonResponse);
    }

    @PatchMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostRequest>> updatePosts(String accountId,
                               @PathVariable(name = "postId") int postId,
                               @RequestBody PostRequest postRequest) {
        UserDTO memberInfo = userService.getUserInfo(accountId);
        PostDTO postDTO = PostDTO.builder()
                .id(postId)
                .name(postRequest.getName())
                .contents(postRequest.getContents())
                .views(postRequest.getViews())
                .categoryId(postRequest.getCategoryId())
                .userId(memberInfo.getId())
                .fileId(postRequest.getFileId())
                .updateTime(new Date())
                .build();
        postService.updateProducts(postDTO);
        CommonResponse commonResponse = new CommonResponse<>(HttpStatus.OK, "SUCCESS", "updatePosts", postDTO);
        return ResponseEntity.ok(commonResponse);
    }



    @DeleteMapping("{postId}")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<CommonResponse<PostDeleteRequest>> deleteposts(String accountId,
                               @PathVariable(name = "postId") int postId,
                               @RequestBody PostDeleteRequest postDeleteRequest) {
        log.info("====================");
        UserDTO memberInfo = userService.getUserInfo(accountId);
        log.info(memberInfo.getId()+"///"+postId);
        postService.deleteProduct(memberInfo.getId(), postId);
        CommonResponse commonResponse = new CommonResponse<>(HttpStatus.OK, "SUCCESS", "deleteposts", postDeleteRequest);
        return ResponseEntity.ok(commonResponse);
    }




    // -------------- response 객체 --------------

    @Getter
    @AllArgsConstructor
    private static class PostResponse {
        private List<PostDTO> postDTO;
    }



    @Setter
    @Getter
    private static class PostRequest {
        private String name;
        private String contents;
        private int views;
        private int categoryId;
        private int userId;
        private int fileId;
        private Date updateTime;
    }

    // -------------- request 객체 --------------

    @Setter
    @Getter
    private static class PostDeleteRequest {
        private int id;
        private int accountId;
    }
}
