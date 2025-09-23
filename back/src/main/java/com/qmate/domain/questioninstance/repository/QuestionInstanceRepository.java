package com.qmate.domain.questioninstance.repository;

import com.qmate.domain.questioninstance.entity.QuestionInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionInstanceRepository extends JpaRepository<QuestionInstance, Long>, QuestionInstanceQueryRepository {

}