package hwannee.project.User.service;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.AddUserRequest;
import hwannee.project.User.repository.UserRepository;
import hwannee.project.libs.Encrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Encrypt encrypt;

    // 회원 가입 서비스
    public String save(AddUserRequest dto){

        return userRepository.save(User.builder()
                        .name(dto.getName())
                        .tel(dto.getTel())
                        .birth(dto.getBirth())
                        .address(dto.getAddress())
                        .detail(dto.getDetail())
                        .id(dto.getId())
                        // 비밀번호는 암호화하여 저장
                        .passWord(encrypt.cryptoPassWord(dto.getPassWord()))
                        .role(dto.getRole())
                        .build()).getId();
    }

    // 아이디로 회원 조회
    public User findById(String id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

    // 전화번호로 회원 조회
    public User findByTel(String tel){
        return userRepository.findByTel(tel)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

    // 전화번호로 회원 조회
    public User findByIdx(Integer idx){
        return userRepository.findByIdx(idx)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
    }

    // 전화번호 변경
    public User modifyTel(Integer userIdx, String modifyTel){
        User user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

        user.setTel(modifyTel);

        userRepository.save(user);
        return user;
    }
    // 주소 변경
    public User modifyAddress(Integer userIdx, String modifyAddress, String modifyDetail){
        User user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

        user.setAddress(modifyAddress);
        user.setDetail(modifyDetail);

        userRepository.save(user);
        return user;
    }
    // 비밀번호 변경
    public User modifyPassword(Integer userIdx, String modifyPw){
        User user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

        // 전달 받은 평문 비밀번호를 다시 단방향 암호화하여 저장
        user.setPassWord(encrypt.cryptoPassWord(modifyPw));

        userRepository.save(user);
        return user;
    }
}
