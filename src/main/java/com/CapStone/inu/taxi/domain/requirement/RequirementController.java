package com.CapStone.inu.taxi.domain.requirement;

import com.CapStone.inu.taxi.domain.requirement.dto.request.CreateRequirementReq;
import com.CapStone.inu.taxi.domain.requirement.dto.request.UpdateRequirementReq;
import com.CapStone.inu.taxi.domain.requirement.dto.response.RequirementRes;
import com.CapStone.inu.taxi.global.common.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.CapStone.inu.taxi.global.common.StatusCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requirements")
public class RequirementController {
    private final RequirementService requirementService;

    @PostMapping
    public ResponseEntity<CommonResponse<RequirementRes>> createRequirement(Principal principal, @RequestBody @Valid CreateRequirementReq req){
        Long memberId=Long.parseLong(principal.getName());
        return ResponseEntity
                .status(REQUIREMENT_CREATE.getStatus())
                .body(CommonResponse.from(REQUIREMENT_CREATE.getMessage(),requirementService.createRequirement(memberId,req)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<RequirementRes>> getRequirement(Principal principal){
        Long memberId=Long.parseLong(principal.getName());
        return ResponseEntity
                .status(REQUIREMENT_FOUND.getStatus())
                .body(CommonResponse.from(REQUIREMENT_FOUND.getMessage(),requirementService.getRequirement(memberId)));
    }

    @PutMapping
    public ResponseEntity<CommonResponse<RequirementRes>> updateRequirement(Principal principal, @RequestBody @Valid UpdateRequirementReq req){
        Long memberId=Long.parseLong(principal.getName());
        return ResponseEntity
                .status(REQUIREMENT_UPDATE.getStatus())
                .body(CommonResponse.from(REQUIREMENT_UPDATE.getMessage(),requirementService.updateRequirement(memberId,req)));
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Object>> deleteRequirement(Principal principal){
        Long memberId=Long.parseLong(principal.getName());
        requirementService.deleteRequirement(memberId);
        return ResponseEntity
                .status(REQUIREMENT_DELETE.getStatus())
                .body(CommonResponse.from(REQUIREMENT_DELETE.getMessage()));
    }
}
