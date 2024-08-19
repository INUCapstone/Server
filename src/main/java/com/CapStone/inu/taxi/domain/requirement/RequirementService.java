package com.CapStone.inu.taxi.domain.requirement;

import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.domain.requirement.dto.request.CreateRequirementReq;
import com.CapStone.inu.taxi.domain.requirement.dto.request.UpdateRequirementReq;
import com.CapStone.inu.taxi.domain.requirement.dto.response.RequirementRes;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.CapStone.inu.taxi.global.common.StatusCode.MEMBER_NOT_EXIST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final MemberRepository memberRepository;

    public RequirementRes getRequirement(Long memberId){
        Requirement requirement= findRequirementByMemberId(memberId);

        return RequirementRes.from(requirement);
    }

    @Transactional
    public RequirementRes createRequirement(Long memberId, CreateRequirementReq req){
        Member member=findByMemberId(memberId);
        Requirement requirement=requirementRepository.save(req.toEntity(member));

        return RequirementRes.from(requirement);
    }

    @Transactional
    public RequirementRes updateRequirement(Long memeberId, UpdateRequirementReq req){
        Requirement requirement= findRequirementByMemberId(memeberId);
        requirement.updateRequirement(req);

        return RequirementRes.from(requirement);
    }

    @Transactional
    public void deleteRequirement(Long memberId){
        requirementRepository.deleteByMember_Id(memberId);
    }


    private Requirement findRequirementByMemberId(Long memberId){
        return requirementRepository.findByMember_Id(memberId)
                .orElseThrow(()-> new CustomException(StatusCode.REQUIREMENT_NOT_EXIST));
    }

    private Member findByMemberId(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(MEMBER_NOT_EXIST));
    }
}
