package org.morgorithm.frames.service;

import lombok.AllArgsConstructor;
import org.morgorithm.frames.domain.Role;
import org.morgorithm.frames.dto.AdminDto;
import org.morgorithm.frames.entity.Admin;
import org.morgorithm.frames.repository.AdminRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements UserDetailsService {
    private AdminRepository adminRepository;

    @Transactional
    public Long joinUser(AdminDto adminDto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));

        return adminRepository.save(adminDto.toEntity()).getId();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> memberEntityWrapper = adminRepository.findByUsername(username);
        if (!memberEntityWrapper.isPresent()) throw new UsernameNotFoundException("존재하지 않는 계정입니다.");
        Admin member = memberEntityWrapper.get();

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (("templates/admin").equals(username)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));
        }

        return new User(member.getUsername(), member.getPassword(), authorities);
    }
}
