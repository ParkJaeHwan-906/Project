package hwannee.project.libs;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import hwannee.project.config.cloud.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3 {

    private final AmazonS3Client amazonS3Client;
    private final S3Properties s3Properties;

    private Set<String> uploadedFileNames = new HashSet<>();
    private Set<Long> uploadedFileSizes = new HashSet<>();

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSizeString;

    // 여러장의 파일 저장
    public List<String> saveFiles(List<MultipartFile> multipartFiles){

        List<String> uploadUrls = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles){
            if(isDuplicate(multipartFile)){
                throw new IllegalArgumentException("중복된 파일입니다.");
            }

            String uploadUrl = saveFile(multipartFile);
            uploadUrls.add(uploadUrl);
        }

        clear();
        return uploadUrls;
    }

    // 단일 파일 저장
    public String saveFile(MultipartFile file) {

        String bucket = s3Properties.getBucket();
        String randomFilename = generateRandomFilename(file);

        log.info("파일 업로드를 시작합니다. : " + randomFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3Client.putObject(bucket, randomFilename, file.getInputStream(), metadata);
        } catch (Exception e) {
            log.error("파일을 업로드 하던 중 S3 오류가 발생했습니다. : " + e.getMessage());
            throw new IllegalArgumentException();
        }

        log.info("파일 업로드를 성공하였습니다. : "+ randomFilename);

        return amazonS3Client.getUrl(bucket, randomFilename).toString();
    }

    // 파일 삭제
    public void deleteFile(String objectKey){
        String bucket = s3Properties.getBucket();

        if(!amazonS3Client.doesObjectExist(bucket, objectKey)){
            throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
        }

        try{
            amazonS3Client.deleteObject(bucket, objectKey);
        } catch (Exception e){
            log.error("파일 삭제 중 오류 발생 : "+e.getMessage());
            throw new IllegalArgumentException("파일 삭제 중 오류가 발생하였습니다.");
        }
        log.info("파일이 성공적으로 삭제되었습니다 : " + objectKey);
    }

    // 객체 주소 추출
    public String getObjectKey(String fileUrl){
        String[] urlParts = fileUrl.split("/");
        String fileBucket = urlParts[2].split("\\.")[0];

        String bucket = s3Properties.getBucket();

        if(!fileBucket.equals(bucket)){
            throw new IllegalArgumentException("존재하지 않는 버킷입니다.");
        }

        String objectKey = String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));

        return objectKey;
    }

    // 요청에 중복되는 파일 여부 확인
    private boolean isDuplicate(MultipartFile multipartFile){
        String fileName = multipartFile.getOriginalFilename();
        Long fileSize = multipartFile.getSize();

        if(uploadedFileNames.contains(fileName) && uploadedFileSizes.contains(fileSize)){
            return true;
        }

        uploadedFileSizes.add(fileSize);
        uploadedFileNames.add(fileName);

        return false;
    }

    private void clear() {
        uploadedFileNames.clear();
        uploadedFileSizes.clear();
    }

    // 랜덤파일명 생성 (파일명 중복 방지)
    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

    // 파일 확장자 체크
    private String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("올바르지 않은 파일 확장자입니다.");
        }
        return fileExtension;
    }
}
