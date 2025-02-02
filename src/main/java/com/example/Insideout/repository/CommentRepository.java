package com.example.Insideout.repository;

import com.example.Insideout.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.board.inquiryId = :inquiryId")
    Long countByInquiryId(@Param("inquiryId") Long inquiryId);
}

