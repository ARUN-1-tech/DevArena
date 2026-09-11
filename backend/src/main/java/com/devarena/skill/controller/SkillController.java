package com.devarena.skill.controller;

import com.devarena.common.api.ApiResponse;
import com.devarena.security.DevArenaUserDetails;
import com.devarena.skill.dto.PlayerSkillDto;
import com.devarena.skill.dto.SkillDto;
import com.devarena.skill.service.SkillProgressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillProgressionService skillProgressionService;

    public SkillController(SkillProgressionService skillProgressionService) {
        this.skillProgressionService = skillProgressionService;
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getSkillTree() {
        List<SkillDto> skills = skillProgressionService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.ok("Skill tree retrieved", skills));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PlayerSkillDto>>> getMySkills(
            @AuthenticationPrincipal DevArenaUserDetails userDetails
    ) {
        List<PlayerSkillDto> playerSkills = skillProgressionService.getPlayerSkills(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Player skills retrieved", playerSkills));
    }
}
