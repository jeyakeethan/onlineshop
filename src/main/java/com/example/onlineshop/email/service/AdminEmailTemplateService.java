package com.example.onlineshop.email.service;

import com.example.onlineshop.email.dto.EmailTemplateDTO;
import com.example.onlineshop.email.repository.EmailTemplateRepository;
import com.example.onlineshop.email.dto.EmailTemplateUpdateDTO;
import com.example.onlineshop.email.entity.EmailTemplate;
import com.example.onlineshop.common.security.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service @RequiredArgsConstructor
public class AdminEmailTemplateService {
    private final EmailTemplateRepository emailTemplateRepository;
    private final AuthenticationHelper authenticationHelper;

    public Page<EmailTemplateDTO> getTemplates(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        return emailTemplateRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "templateKey")))
                .map(EmailTemplateDTO::new);
    }

    @Transactional
    public EmailTemplateDTO updateTemplate(Long templateId, EmailTemplateUpdateDTO request) {
        if (request == null || request.getSubject() == null && request.getBody() == null
                && request.getAllowedVariables() == null && request.getActive() == null) {
            throw new IllegalArgumentException("At least one template field must be provided");
        }
        EmailTemplate template = emailTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Email template not found"));
        String allowed = request.getAllowedVariables() == null
                ? template.getAllowedVariables() : request.getAllowedVariables();
        Set<String> variables = parseVariables(allowed);
        if (request.getSubject() != null) {
            validateVariables(request.getSubject(), variables);
            template.setSubject(request.getSubject().trim());
        }
        if (request.getBody() != null) {
            validateVariables(request.getBody(), variables);
            template.setBody(request.getBody());
        }
        if (request.getAllowedVariables() != null) {
            template.setAllowedVariables(request.getAllowedVariables().trim());
        }
        if (request.getActive() != null) template.setActive(request.getActive());
        template.setLastUpdatedBy(authenticationHelper.getCurrentUserId());
        template.setLastUpdatedAt(LocalDateTime.now());
        return new EmailTemplateDTO(emailTemplateRepository.save(template));
    }

    private Set<String> parseVariables(String value) {
        if (value == null || value.isBlank()) return Set.of();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(variable -> variable.matches("[A-Za-z][A-Za-z0-9_]*"))
                .collect(Collectors.toSet());
    }

    private void validateVariables(String text, Set<String> allowed) {
        Matcher matcher = Pattern.compile("\\{\\{\\s*([A-Za-z][A-Za-z0-9_]*)\\s*}}")
                .matcher(text);
        while (matcher.find()) {
            if (!allowed.contains(matcher.group(1))) {
                throw new IllegalArgumentException("Template contains an undeclared variable: " + matcher.group(1));
            }
        }
    }
}
