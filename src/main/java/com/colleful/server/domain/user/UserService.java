package com.colleful.server.domain.user;

import com.colleful.server.domain.team.Team;
import com.colleful.server.domain.team.TeamService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TeamService teamService;

    public UserService(UserRepository userRepository,
        TeamService teamService) {
        this.userRepository = userRepository;
        this.teamService = teamService;
    }

    public Boolean join(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<User> getUserInfo(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserInfoByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserInfoByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public Page<User> getAllUserInfo(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public Boolean isExist(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public Boolean changeUserInfo(User from, User to) {
        try {
            Optional.of(to).map(User::getNickname).ifPresent(from::setNickname);
            Optional.of(to).map(User::getDepartment).ifPresent(from::setDepartment);
            Optional.of(to).map(User::getSelfIntroduction).ifPresent(from::setSelfIntroduction);
            userRepository.save(from);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean changePassword(User user, String password) {
        try {
            user.setPassword(password);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean joinTeam(User user, Team team) {
        try {
            user.joinTeam(team);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean leaveTeam(User user) {
        try {
            user.leaveTeam();
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Boolean withdrawal(User user) {
        try {
            List<Team> myTeams = teamService.getAllTeamsByLeader(user.getId());
            for (Team team : myTeams) {
                teamService.deleteTeam(team);
            }
            userRepository.deleteById(user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(RuntimeException::new);
    }
}
