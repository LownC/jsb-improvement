package com.mysite.sbb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonUtilTest {
    private final CommonUtil commonUtil = new CommonUtil();

    @Test
    @DisplayName("마크다운 문법이 HTML 로 변환된다")
    void t1() {
        String html = commonUtil.markdown("**굵게** 그리고 *기울임*\n\n- 항목1\n- 항목2");

        assertThat(html).contains("<strong>굵게</strong>");
        assertThat(html).contains("<em>기울임</em>");
        assertThat(html).contains("<li>항목1</li>");
    }

    @Test
    @DisplayName("본문에 직접 쓴 HTML 태그는 이스케이프되어 스크립트가 실행되지 않는다")
    void t2() {
        String html = commonUtil.markdown("<script>alert('x')</script> 안녕");

        assertThat(html).doesNotContain("<script>");
        assertThat(html).contains("&lt;script&gt;");
    }

    @Test
    @DisplayName("null 은 빈 문자열로 처리한다")
    void t3() {
        assertThat(commonUtil.markdown(null)).isEmpty();
    }
}
