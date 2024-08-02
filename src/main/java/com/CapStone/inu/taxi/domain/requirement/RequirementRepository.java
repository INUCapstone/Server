package com.CapStone.inu.taxi.domain.requirement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequirementRepository extends JpaRepository<Requirement,Long> {
    Optional<Requirement> findByMember_Id(Long memberId);
    void deleteByMember_Id(Long memberId);
}
