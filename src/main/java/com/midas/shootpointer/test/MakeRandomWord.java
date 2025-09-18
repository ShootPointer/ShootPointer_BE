package com.midas.shootpointer.test;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("testdata")
public class MakeRandomWord {
    private static final String[] TITLES = {
            "고양이", "강아지", "여행", "프로그래밍", "커피", "음악", "스포츠", "게임", "책", "영화",
            "바다", "산", "도시", "학교", "도서관", "캠핑", "사진", "노래", "비밀", "추억",
            "도전", "성공", "실패", "열정", "휴식", "식사", "저녁", "아침", "점심", "계획"
    };

    private static final String[] CONTENT_WORDS = {
            "오늘", "정말", "재미있는", "코드를", "작성했다", "바람이", "시원하다", "점심은", "맛있게", "먹었다",
            "새로운", "아이디어가", "떠올랐다", "도전적인", "프로젝트", "좋은", "경험", "운동을", "했다", "기분이",
            "행복하다", "슬펐다", "웃었다", "걷는다", "달린다", "배웠다", "읽었다", "들었다", "봤다", "만났다",
            "친구와", "함께", "놀았다", "공부했다", "잠을", "잤다", "하루가", "길었다", "짧았다", "즐거웠다",
            "인터넷", "게임을", "했다", "버스를", "탔다", "길이", "막혔다", "저녁은", "특별했다", "시간이", "빨리",
            "흘렀다", "노을이", "아름다웠다", "별이", "빛났다", "책을", "샀다", "영화를", "봤다", "커피를", "마셨다",
            "노래를", "불렀다", "연습했다", "성공했다", "실패했다", "도전했다", "다짐했다", "회고했다", "계획했다"
    };

    private static final Random random = new Random();

    public String generateTitle() {
        // 2~5개의 랜덤 단어로 제목 생성
        int wordCount = 10 + random.nextInt(4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            sb.append(TITLES[random.nextInt(TITLES.length)]);
            if (i < wordCount - 1) sb.append(" ");
        }
        return sb.toString();
    }

    public String generateContent() {
        // 20~40개의 랜덤 단어로 문장 생성
        int wordCount = 50 + random.nextInt(51);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            sb.append(CONTENT_WORDS[random.nextInt(CONTENT_WORDS.length)]).append(" ");
        }
        sb.append(".");
        return sb.toString();
    }
}

