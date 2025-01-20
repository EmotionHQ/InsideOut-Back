package com.example.Insideout.repository;

import com.example.Insideout.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoard_InquiryId(Long inquiryId);
}

