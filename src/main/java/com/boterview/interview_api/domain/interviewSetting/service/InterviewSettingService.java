package com.boterview.interview_api.domain.interviewSetting.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.boterview.interview_api.common.service.S3Service;
import com.boterview.interview_api.domain.interviewSetting.dto.InterviewSettingRequestDto;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import com.boterview.interview_api.domain.interviewSetting.entity.PreQuestion;
import com.boterview.interview_api.domain.interviewSetting.entity.SettingSkill;
import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import com.boterview.interview_api.domain.interviewSetting.repository.InterviewSettingMapper;
import com.boterview.interview_api.domain.interviewSetting.repository.PreQuestionMapper;
import com.boterview.interview_api.domain.interviewSetting.repository.SettingSkillMapper;
import com.boterview.interview_api.domain.interviewSetting.repository.SkillMapper;
import com.boterview.interview_api.stomp.registry.RoomRegistry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewSettingService {

	private final InterviewSettingMapper interviewSettingMapper;
	private final PreQuestionMapper preQuestionMapper;
	private final SettingSkillMapper settingSkillMapper;
	private final SkillMapper skillMapper;
	private final S3Service s3Service;
	private final RoomRegistry roomRegistry;

	@Transactional
	public String saveSettings(String userId, InterviewSettingRequestDto dto, MultipartFile resume) {
		String settingId = UUID.randomUUID().toString();

		String resumeUri = null;
		if (resume != null && !resume.isEmpty()) {
			String key = "resume/" + settingId + "/" + resume.getOriginalFilename();
			resumeUri = s3Service.upload(resume, key);
		}

		InterviewSetting setting = InterviewSetting.builder()
				.settingId(settingId)
				.userId(userId)
				.questionCount(dto.getQuestionCount())
				.interviewerStyle(dto.getPersonalityType())
				.interviewerGender(dto.getGender())
				.interviewerAppearance(dto.getAppearanceStyle())
				.createdAt(LocalDateTime.now())
				.resumeUri(resumeUri)
				.position(dto.getTargetPosition().name())
				.build();
		interviewSettingMapper.insert(setting);

		for (String skillName : dto.getSkills()) {
<<<<<<< HEAD
			String skillId = resolveSkillId(skillName);
=======
			String skillId = UUID.randomUUID().toString();
			Skill skill = Skill.builder().skillId(skillId).skill(skillName).build();
			skillMapper.insert(skill);
>>>>>>> 461bf75 (add: id가 BIGINT 타입으로 되어있는것 수정)
			settingSkillMapper.insert(SettingSkill.builder()
					.settingId(settingId)
					.skillId(skillId)
					.build());
		}

		if (dto.getPreQuestions() != null) {
			for (InterviewSettingRequestDto.PreQuestion pq : dto.getPreQuestions()) {
				preQuestionMapper.insert(PreQuestion.builder()
						.preQuestionId(UUID.randomUUID().toString())
						.settingId(settingId)
						.question(pq.getQuestion())
						.answer(pq.getAnswer())
						.build());
			}
		}

		roomRegistry.getOrCreateRoom(settingId);
		return settingId;
	}

	private String resolveSkillId(String skillName) {
		Optional<Skill> existing = skillMapper.findBySkill(skillName);
		if (existing.isPresent()) {
			return existing.get().getSkillId();
		}

		String newSkillId = UUID.randomUUID().toString();
		try {
			skillMapper.insert(Skill.builder().skillId(newSkillId).skill(skillName).build());
		} catch (DuplicateKeyException e) {
			return skillMapper.findBySkill(skillName)
					.map(Skill::getSkillId)
					.orElseThrow(() -> e);
		}

		return skillMapper.findBySkill(skillName)
				.map(Skill::getSkillId)
				.orElse(newSkillId);
	}
}
