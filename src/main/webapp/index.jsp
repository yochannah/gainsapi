<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>GAINSL: Reports</title>
    <link href='http://fonts.googleapis.com/css?family=Raleway' rel='stylesheet' type='text/css'>
    <link type="text/css" rel="stylesheet" href="/css/style.css"/>
    <link rel="shortcut icon" href="http://faviconist.com/icons/2e4ff8c8a427d0559f763ceb86572c0c/favicon.ico" />
</head>

<body>
<html>

<body>

<form>
OrgName: <input type="text" id="orgName" value="Open University"><br>
Lat: <input type="text" id="latitude" value="51.123123"><br>
Long: <input type="text" id="longitude" value="12.1223234"><br>
Content: <input type="text" id="content"><br>
<button id="submit" type="button">Make a report!</button>
</form>

<script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
<script>

$('#submit').click(function(e) {
    var content = $('#content').val(),
    lat = $('#latitude').val(),
    long = $('#longitude').val();
    $.ajax({
        type:"POST",
        data:{"orgName" : "bob", "content" : content, "latitude" : lat, "longitude" : long},
        url:"http://localhost:8888/report"
    });
    
    e.preventDefault();
});

$('document').ready(function () { 
});    
</script>
</body>
</html>
</body>
</html>