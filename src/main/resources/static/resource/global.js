// 강사 14168 42~44강: 전체 페이지에 적용되는 공통 스크립트
document.addEventListener("DOMContentLoaded", () => {
    // a 태그에 method와 onclick 속성이 같이 있는 경우 onclick 속성을 data-onclick으로 옮겨
    // 아래 클릭 핸들러가 먼저 실행되도록 한다.
    document.querySelectorAll("a[method][onclick]").forEach((anchor) => {
        const onclick = anchor.getAttribute("onclick");
        if (onclick) {
            anchor.removeAttribute("onclick");
            anchor.setAttribute("data-onclick", onclick);
        }
    });

    // <a method="DELETE|PUT|POST" href="..."> 를 클릭하면 GET 이동 대신
    // CSRF 토큰과 _method 를 담은 숨은 폼을 만들어 POST 로 전송한다.
    // 서버의 HiddenHttpMethodFilter 가 _method 값을 보고 @DeleteMapping/@PutMapping 으로 연결한다.
    document.querySelectorAll("a[method]").forEach((anchor) => {
        anchor.addEventListener("click", (e) => {
            e.preventDefault();

            const onclickAfterCode = anchor.getAttribute("data-onclick");
            if (onclickAfterCode) {
                const onclickAfter = new Function(onclickAfterCode);
                if (!onclickAfter()) return false; // confirm 에서 취소하면 중단
            }

            const href = anchor.getAttribute("href");
            const url = new URL(href, window.location.origin);
            const method = anchor.getAttribute("method");

            const form = document.createElement("form");
            form.action = url.pathname;
            form.method = "POST";

            const csrfToken =
                document.querySelector('meta[name="_csrf"]')?.getAttribute("content") || "";
            const csrfInput = document.createElement("input");
            csrfInput.type = "hidden";
            csrfInput.name = "_csrf";
            csrfInput.value = csrfToken;
            form.appendChild(csrfInput);

            const methodInput = document.createElement("input");
            methodInput.type = "hidden";
            methodInput.name = "_method";
            methodInput.value = method;
            form.appendChild(methodInput);

            for (const [key, value] of url.searchParams.entries()) {
                const paramInput = document.createElement("input");
                paramInput.type = "hidden";
                paramInput.name = key;
                paramInput.value = value;
                form.appendChild(paramInput);
            }

            document.body.appendChild(form);
            form.submit();
            setTimeout(() => form.remove());
        });
    });
});
