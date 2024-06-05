package com.example.HiBuddy.domain.comment;

import com.example.HiBuddy.domain.comment.dto.request.CommentsRequestDto;
import com.example.HiBuddy.domain.comment.dto.response.CommentsResponseDto;
import com.example.HiBuddy.domain.post.Posts;
import com.example.HiBuddy.domain.user.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentsConverter {

    public static CommentsResponseDto.UserDto toUserDto(Users user) {
        return CommentsResponseDto.UserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImageUrl())
                .build();
    }

    public static Comments toCommentEntity(Users user, Posts post, CommentsRequestDto.CreateCommentRequestDto request) {
        return Comments.builder()
                .comment(request.getComment())
                .user(user)
                .post(post)
                .build();
    }

    public static CommentsResponseDto.CommentDto toCommentInfoResultDto(Comments comment, Posts post, Users user, String createdAt) {
        CommentsResponseDto.UserDto userDto = toUserDto(post.getUser());

        return CommentsResponseDto.CommentDto.builder()
                .commentId(comment.getId())
                .comment(comment.getComment())
                .isAuthor(true)
                .user(userDto)
                .createdAt(createdAt)
                .build();
    }

    public static CommentsResponseDto.CommentsInfoPageDto toCommentsInfoResultPageDto(List<CommentsResponseDto.CommentDto> commentInfoDtoList, int totalPages, int totalElements,
                                                                                       boolean isFirst, boolean isLast, int size, int number, int numberOfElements) {
        return CommentsResponseDto.CommentsInfoPageDto.builder()
                .result(commentInfoDtoList)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .isFirst(isFirst)
                .isLast(isLast)
                .size(size)
                .number(number)
                .numberOfElements(numberOfElements)
                .build();
    }
}