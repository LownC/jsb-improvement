package com.mysite.sbb;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

/**
 * 교재 3-12: 마크다운 문자열을 HTML 로 바꾼다. 템플릿에서 ${@commonUtil.markdown(...)} 으로 호출한다.
 * 교재와 달리 escapeHtml(true) 를 켜서 본문에 직접 쓴 <script> 같은 HTML 태그는 문자로 표시되게 한다.
 * (마크다운 문법으로 만들어진 HTML 만 허용 → XSS 방지)
 */
@Component
public class CommonUtil {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();

    public String markdown(String markdown) {
        if (markdown == null) return "";

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
