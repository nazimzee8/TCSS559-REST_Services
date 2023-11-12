<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
</head>
<style>

body {
    background-image: url("https://d2bgjx2gb489de.cloudfront.net/gbb-blogs/wp-content/uploads/2016/12/21133650/Airport-Lounge_XXL.jpg");
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-size: 100% 100%;
}
.LoginBanner {
    margin-left: auto;
    margin-right: auto;
    display: block;
    width: 300px;
    height: 500px;
    margin-top: 100px;
    /*background-color: rgba(129, 218, 253, 0.247);*/
}

.LoginBanner button {
        border-radius: 30px;
        color: white;
        background-color: black;
        padding: 15px 32px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        margin-top: 20px;
        margin-left: -50px;
        width: 400px;
        height: 50px;
        cursor: pointer;
        opacity: 0.6;
}

.LoginBanner a {
	font-family: cambria; 
   	font-size: 1.0rem; 
   	margin-left: -50px;
   	vertical-align: middle;
	text-decoration: none;
}

.icon input {
    height: 50px;
    width: 400px;
    margin-left: -50px;
    border-radius: 30px;
    opacity: 0.3;
}
</style>
<body>
    <div class="UserPage" style="display: block;">
        <h1 style="text-align: center;  margin-top: 50px; font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif; font-size: 3vwh;">Airport Security User Submission Form </h1>
        <div class="LoginBanner">
           <img style="width: 100px; height: 100px; margin-left: 100px; margin-top: 25px;" src="C:\Users\nazimz\Documents\TCSS559\Luggage_Project\images\login_logo.png">
            
            <form action="./login" method="post">
                <div class="icon" style="margin-top: 50px;">
                    <span class="fa fa-user" aria-hidden="true"></span>
                    <div class="user_entry">
                        <input name="username" type="text" placeholder="Enter a Username" required/>
                    </div>
                </div>
                <div class="icon" style="margin-top: 20px;">
                    <span class="fa fa-lock" aria-hidden="true"></span>
                    <input name="password" id="password" type="Password" placeholder="Enter a Password" required/>
                </div>
                <button type="submit" style="display: flex; align-items: center;"><p style="margin-left: 100px;"> LOGIN </p></button>
            	<br>
            	<a href="http://localhost:8080/Project">Return to home</a>
            </form>
            
        </div>
    </div>
</body>
</html>