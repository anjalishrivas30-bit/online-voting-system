<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="text-center py-5">
    <div class="display-4 fw-semibold">403</div>
    <h1 class="h4 mb-3">You do not have access to this page</h1>
    <p class="text-secondary">Your account does not have the role needed for this area.</p>
    <a class="btn btn-primary" href="<c:url value='/'/>">Go to my home page</a>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
