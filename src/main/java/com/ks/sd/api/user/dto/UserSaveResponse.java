package com.ks.sd.api.user.dto;

import com.ks.sd.api.user.entity.User;
import com.ks.sd.base.dto.BaseResponse;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserSaveResponse extends BaseResponse {
    private String userId;
    private String userNm;

    @Builder
    public UserSaveResponse(User user) {
        this.userId = user.getUserId();
        this.userNm = user.getUserNm(); 
        this.setDelYn(user.getDelYn());
        this.setCreatedDt(user.getCreatedDt());
        this.setUpdatedDt(user.getUpdatedDt());
    }
}
