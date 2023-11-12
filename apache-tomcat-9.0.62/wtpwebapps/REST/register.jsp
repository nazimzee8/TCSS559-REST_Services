<!DOCTYPE html>
<html>
<head>

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
    width: 400px;
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
        width: 400px;
        height: 50px;
        cursor: pointer;
        opacity: 0.6;
}

.LoginBanner a {
	font-family: cambria; 
   	font-size: 1.0rem; 
   	vertical-align: middle;
	text-decoration: none;
}

.icon input {
    height: 50px;
    width: 400px;
    border-radius: 30px;
    opacity: 0.3;
}
</style>
<body>
    <div class="UserPage" style="display: block;">
        <h1 style="text-align: center;  margin-top: 50px; font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif; font-size: 3vwh;">Airport Security User Submission Form </h1>
        <div class="LoginBanner">
           <img style="width: 100px; height: 100px; margin-left: 100px; margin-top: 25px;" src="https://freesvg.org/img/applications-internet.png">
            <form action="./register" method="post">
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
             	<button type="submit" style="display: flex; align-items: center;"><p style="margin-left: 130px;"> GET STARTED </p></button>
            	<br>
            	<table>
					<tr>
						<td><a href="http://localhost:8080/REST/api/user/home">Return to home</a></td>
						<td><a href="http://localhost:8080/REST/api/user/login" style="margin-left: 145px;">Proceed to Login</a>
					</tr>
				</table>
            </form>
        </div>
    </div>
</body>
</head>
</html>