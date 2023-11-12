<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>File Uploading Form</title>
</head>
<body>
	<form action="./" method="post" enctype="multipart/form-data">
		Assign4 File Upload(Input Data):
		<br>
		<br>
		<input type="file" name="fileName">
		<br>
		<input type="submit" value="Upload">
    </form>
</body>
</html>
