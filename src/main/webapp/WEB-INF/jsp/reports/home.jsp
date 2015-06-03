<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
    <body>
        <h4>Asana Reporting Tool home page</h4>
        <div>
        <!-- Credentials display -->
        Your are logged in as <sec:authentication property="userAuthentication.details.email"/>
        ...
        <!-- Roles display -->
        <sec:authentication property="authorities" var="roles" scope="page" />
        Your roles are:
        <ul>
            <c:forEach var="role" items="${roles}">
                <li>${role}</li>
            </c:forEach>
        </ul>

        </div>
    </body>
</html>