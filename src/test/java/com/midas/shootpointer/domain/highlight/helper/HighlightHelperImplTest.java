package com.midas.shootpointer.domain.highlight.helper;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HighlightHelperImplTest {
    @InjectMocks
    private HighlightHelperImpl highlightHelper;

    @Mock
    private HighlightUtil highlightUtil;

    @Mock
    private HighlightValidator highlightValidator;


    @Test
    @DisplayName("highlightUtil.getDirectoryPath(String)이 실행되는지 검증합니다.")
    void getDirectoryPath() {
        //given
        String key="key";

        //when
        highlightHelper.getDirectoryPath(key);

        //then
        verify(highlightUtil, times(1)).getDirectoryPath(key);
    }

    @Test
    @DisplayName("highlightUtil.findHighlightByHighlightId(UUID)이 실행되는지 검증합니다.")
    void findHighlightByHighlightId() {
        //given
        UUID highlightId=UUID.randomUUID();

        //when
        highlightHelper.findHighlightByHighlightId(highlightId);

        //then
        verify(highlightUtil,times(1)).findHighlightByHighlightId(highlightId);
    }

    @Test
    @DisplayName("highlightUtil.savedAll(List<HighlightEntity>)이 실행되는지 확인합니다.")
    void savedAll() {
        //given
        List<HighlightEntity> entities=new ArrayList<>();

        //when
        highlightHelper.savedAll(entities);

        //then
        verify(highlightUtil,times(1)).savedAll(entities);
    }

    @Test
    @DisplayName("highlightValidator.filesExist(String)이 실행되는지 검증합니다.")
    void filesExist() {
        //given
        String dir="dir";

        //when
        highlightHelper.filesExist(dir);

        //then
        verify(highlightValidator,times(1)).filesExist(dir);
    }

    @Test
    @DisplayName("highlightValidator.isExistHighlightId(UUID)이 실행되는지 검증합니다.")
    void isExistHighlightId() {
        //given
        UUID uuid=UUID.randomUUID();

        //when
        highlightHelper.isExistHighlightId(uuid);

        //then
        verify(highlightValidator,times(1)).isExistHighlightId(uuid);
    }

    @Test
    @DisplayName("highlightValidator.isValidMembersHighlight(UUID, UUID)이 실행되는지 검증합니다.")
    void isValidMembersHighlight() {
        //given
        UUID highlightId=UUID.randomUUID();
        UUID memberId=UUID.randomUUID();

        //when
        highlightHelper.isValidMembersHighlight(highlightId,memberId);

        //then
        verify(highlightValidator,times(1)).isValidMembersHighlight(highlightId,memberId);
    }

    @Test
    @DisplayName("highlightValidator.isValidMp4File(MultipartFile)이 실행되는지 검증합니다.")
    void isValidMp4File() {
        //given
        MockMultipartFile mockMultipartFile =makeMockFile("test.mp4");

        //when
        highlightHelper.isValidMp4File(mockMultipartFile);

        //then
        verify(highlightValidator,times(1)).isValidMp4File(mockMultipartFile);
    }

    @Test
    @DisplayName("highlightValidator.isValidFileSize(MultipartFile)이 실행되는지 검증합니다.")
    void isValidFileSize() {
        //given
        MockMultipartFile mockMultipartFile=makeMockFile("test.mp4");

        //when
        highlightHelper.isValidFileSize(mockMultipartFile);

        //then
        verify(highlightValidator,times(1)).isValidFileSize(mockMultipartFile);
    }

    @Test
    @DisplayName("highlightValidator.isExistDirectory(String)이 실행되는지 검증합니다.")
    void isExistDirectory() {
        //given
        String dir="dir";

        //when
        highlightHelper.isExistDirectory(dir);

        //then
        verify(highlightValidator,times(1)).isExistDirectory(dir);
    }

    @Test
    @DisplayName("highlightValidator.areValidFiles(List<MultipartFile>)이 실행되는지 검증합니다.")
    void areValidFiles() {
        //given
        List<MultipartFile> list=List.of(
                makeMockFile("test1.mp4"),
                makeMockFile("test2.mp4"),
                makeMockFile("test3.mp4")
        );

        //when
        highlightHelper.areValidFiles(list);

        //then
        verify(highlightValidator,times(1)).areValidFiles(list);
    }

    @Test
    @DisplayName("highlightUtil.fetchMembersHighlights(memberId,pageable)이 실행되는지 검증합니다.")
    void fetchMembersHighlights(){
        //given
        Page<HighlightEntity> page=Page.empty();
        UUID memberId=UUID.randomUUID();
        Pageable pageable= PageRequest.of(0,10);

        //when
        highlightHelper.fetchMembersHighlights(memberId,pageable);

        //then
        verify(highlightUtil).fetchMembersHighlights(memberId,pageable);
    }

    private MockMultipartFile makeMockFile(String fileName){
        byte[] bytes = new byte[10];
        return new MockMultipartFile(
                "file",
                fileName,
                "video/mp4",
                bytes
        );
    }
}