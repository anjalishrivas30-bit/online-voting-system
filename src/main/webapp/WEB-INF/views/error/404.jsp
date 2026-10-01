<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="text-center py-5">
    <div class="display-4 fw-semibold">404</div>
    <h1 class="h4 mb-3">Page not found</h1>
    <p class="text-secondary"><c:out value="${empty message ? 'The page you asked for does not exist.' : message}"/></p>
    <a class="btn btn-primary" href="<c:url value='/'/>">Go to my home page</a>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
