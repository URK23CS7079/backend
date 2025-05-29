package com.example.Backend.service;

import com.example.Backend.entity.*;
import com.example.Backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final OtpVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;
    private final PrivilegeRepository privilegeRepository;

    public Map<String, Object> verifyAndRespond(String email, String otp) {
        // 1. Find the latest OTP entry
        Optional<OtpVerification> otpOpt = otpVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (otpOpt.isEmpty()) return null;

        OtpVerification otpVerification = otpOpt.get();

        // 2. Check OTP validity
        if (otpVerification.isVerified() ||
            !otpVerification.getOtp().equals(otp) ||
            otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        // 3. Mark OTP as verified
        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);

        // 4. Get User
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return null;

        User user = userOpt.get();

        // 5. Get Role
        Optional<Role> roleOpt = roleRepository.findById(user.getRole().getRoleId());
        if (roleOpt.isEmpty()) return null;

        Role role = roleOpt.get();

        // 6. Get Privileges
        List<RolePrivilege> rolePrivileges = rolePrivilegeRepository.findByRoleRoleIdAndIsActiveTrue(role.getRoleId());

        List<String> privileges = new ArrayList<>();
        for (RolePrivilege rp : rolePrivileges) {
            Optional<Privilege> priv = privilegeRepository.findById(rp.getPrivilege().getPrivilegeId());
            priv.ifPresent(p -> privileges.add(p.getPrivilegeName()));
        }

        // 7. Build and return response
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phone_number", user.getPhoneNumber());
        response.put("role", role.getRoleName().toLowerCase());
        response.put("privileges", privileges);
        response.put("navigate_to", role.getNavigateTo());

        return response;
    }
}
