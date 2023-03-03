package com.ks.sd.api.pjt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.pjt.dto.SubProjectMngResponse;
import com.ks.sd.api.pjt.dto.SubProjectSaveRequest;
import com.ks.sd.api.pjt.dto.SubProjectSaveResponse;
import com.ks.sd.api.pjt.dto.SubProjectUpdateRequest;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.entity.SubProjectId;
import com.ks.sd.api.pjt.repository.SubProjectRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class SubProjectService {
    @Autowired
    private SubProjectRepository subProjectRepository;

    public List<SubProjectMngResponse> getSubProjects(Integer pjtNo) {
        List<SubProjectMngResponse> subProjectMngResponses = new ArrayList<>();
        
        Optional<List<SubProject>> optSubProjects =
            subProjectRepository.findByProjectAndDelYn(
                Project.builder().pjtNo(pjtNo).build(),
                "N"
            );
        
        optSubProjects.ifPresent(subProjects -> {
            subProjects.forEach(subProject -> {
                SubProjectMngResponse subProjectMngResponse
                    = SubProjectMngResponse.builder().subProject(subProject).build();
                    subProjectMngResponses.add(subProjectMngResponse);
            });
        });

        return subProjectMngResponses;
    }

    public SubProjectSaveResponse save(SubProjectSaveRequest subProjectSaveRequest) {
        Integer subPjtCnt = subProjectRepository.countByProject(subProjectSaveRequest.toEntity().getProject());

        SubProject subProject = subProjectRepository.save(subProjectSaveRequest.toEntity(subPjtCnt));

        return SubProjectSaveResponse.builder().subProject(subProject).build();
    }

    public SubProjectSaveResponse update(SubProjectUpdateRequest subProjectUpdateRequest) {
        SubProjectId subProjectId = 
            SubProjectId.builder()
            .project(subProjectUpdateRequest.getPjtNo())
            .subPjtNo(subProjectUpdateRequest.getSubPjtNo()).build();

        SubProject subProject = subProjectRepository.findById(subProjectId)
            .orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));
            
        return subProject.update(subProjectUpdateRequest.toEntity());
    }
}
