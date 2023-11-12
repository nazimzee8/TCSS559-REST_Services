<!DOCTYPE html>
<html>
<head>
</head>

<style>
body {
    background-image: url("https://img.freepik.com/premium-photo/traveling-luggage-airport-terminal_34013-3.jpg?w=2000");
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-size: 100% 100%;
}

.Banner {
    margin-left: auto;
    margin-right: auto;
    display: block;
    width: 400px;
    height: 500px;
    margin-top: 100px;
    /*background-color: rgba(129, 218, 253, 0.247);*/
}

.Banner button {
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

.Banner a {
	font-family: cambria; 
   	font-size: 1.0rem; 
   	vertical-align: middle;
	text-decoration: none;
}

.icon input {
    height: 50px;
    width: 400px;
    margin-left: 0;
    border-radius: 30px;
    opacity: 0.3;
}

</style>
<body>
	<div class="Luggage">
		<h1 style="text-align: center;  margin-top: 150px; font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif; font-size: 3vwh;"> Luggage Tracking </h1>
		<div class="Banner">
			<form action="./search" method="post">
				<div class="icon" style="margin-top: 50px;">
		             <span class="fa fa-rfid" aria-hidden="true"></span>
		             <div class="rfid_entry">
		                 <input name="rfid" type="text" placeholder="Enter a valid rfid tag" required/>
		             </div>
		         </div>
		         <button type="submit" style="display: flex; align-items: center;"><p style="margin-left: 140px;"> Submit </p></button>
				<br>
				<a href="http://localhost:8080/REST/api/user/home">Return to home</a>
			</form>
		</div>
	</div>

</body>
</html>