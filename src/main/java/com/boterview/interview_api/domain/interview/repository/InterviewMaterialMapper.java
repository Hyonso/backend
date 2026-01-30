package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.InterviewMaterial;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewMaterialMapper {


    @Insert("INSERT INTO interview_material (material_id, interview_id, material_type, s3_uri, created_at) " +
            "VALUES (#{materialId}, #{interviewId}, #{materialType}, #{s3Uri}, #{createdAt})")
    void insert(InterviewMaterial material);

}
