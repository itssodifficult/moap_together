package com.example.myapplication;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Item {
    private String 경도;
    private String 상호명;
    private String 소재지도로명주소;
    private String 소재지지번주소;
    private String 위도;
    private String 전화번호;

    // Getter and setter for 경도
    public String get경도() {
        return 경도;
    }
    public double getLongitude() {
        try {
            return Double.parseDouble(경도);
        } catch (NumberFormatException e) {
            // Parsing 실패 시 기본값 반환 혹은 예외 처리 로직 추가
            return 0.0; // 기본값은 0.0으로 설정
        }
    }

    public void set경도(String 경도) {
        this.경도 = 경도;
    }

    // Getter and setter for 상호명
    public String get상호명() {
        return 상호명;
    }

    public void set상호명(String 상호명) {
        this.상호명 = 상호명;
    }

    // Getter and setter for 소재지도로명주소
    public String get소재지도로명주소() {
        return 소재지도로명주소;
    }

    public void set소재지도로명주소(String 소재지도로명주소) {
        this.소재지도로명주소 = 소재지도로명주소;
    }

    // Getter and setter for 소재지지번주소
    public String get소재지지번주소() {
        return 소재지지번주소;
    }

    public void set소재지지번주소(String 소재지지번주소) {
        this.소재지지번주소 = 소재지지번주소;
    }

    // Getter and setter for 위도
    public String get위도() {
        return 위도;
    }
    public double getLatitude() {
        try {
            return Double.parseDouble(위도);
        } catch (NumberFormatException e) {
            // Parsing 실패 시 기본값 반환 혹은 예외 처리 로직 추가
            return 0.0; // 기본값은 0.0으로 설정
        }
    }

    public void set위도(String 위도) {
        this.위도 = 위도;
    }

    // Getter and setter for 전화번호
    public String get전화번호() {
        return 전화번호;
    }

    public void set전화번호(String 전화번호) {
        this.전화번호 = 전화번호;
    }
}