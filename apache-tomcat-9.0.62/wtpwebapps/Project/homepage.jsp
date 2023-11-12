<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
</head>
<style>
    .header_small {
        display: block; 
        height: 2%; 
        position: fixed; 
        top: 0; 
        width: 100%; 
        background-color: #efefefbb;
    }
    .header_small h4 {
        text-align: center;
        margin-top: -0.1px;
        font-size: 1rem; 
        font-family: cambria;
    }
    .header_main img {
        height: 7.5%; 
        width: 10%; 
        position: fixed; 
        right: 5%;
        margin-top: 1%;
    }
    .header_sources {
        height: 60px; 
        width: 100%;
        background-color: black;
        /**text-align: center;**/
    }
    .sources td {
        width: 23%;
        padding: 1%;
        vertical-align: middle;
    }
    
    .sources a {
    	font-family: cambria; 
        font-size: 1.1rem; 
        color: white;
    	text-decoration: none;
    }
    
    .screen img {
        width: 60%;
        height: 100%; 
        background-position: center; 
        background-repeat: no-repeat; 
        background-size: cover;
        position: fixed;
        left: 0;
    }
    
    .screen h1 {
        text-align: left; 
        font-family: monospace; 
        font-size: 2.2rem;
        margin-left: 10%;
    }
    .screen button {
        border: none;
        color: white;
        background-color: black;
        padding: 15px 32px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        width: 45%;
        margin-right: 35%;
        cursor: pointer;
    }
</style>
<body>
    <div class="HomePage">
        <div class="header_small">
            <h4>GET READY FOR TAKE-OFF</h4>
        </div>
        <div class="header_main" style="display: inline-block; height: 120px; width: 100%;">
            <img src="C:\Users\nazimz\Documents\TCSS559\Luggage_Project\images\luggage.png">
            <h1 style="text-align: center; margin-top: 50px; font-size: 2.0rem; font-family: cambria"> Efficient Airport Control</h1>
        </div>
        <div class="header_sources">
            <table class="sources" style="margin-left: auto; margin-right: auto;">
                <tr>
                    <th colspan = "5"></th>
                </tr>
                <tr>
                    <td><span class="Tracking"><a href=""> Tracking </a></span></td>
                    <td><span class="Luggage"><a href=""> Set Luggage </a></span></td>
                    <td><span class="Flight"><a href=""> Select Flight </a></span></td>
                    <td><span class="Login"><a href="http://localhost:8080/Project/login"> Login </a></span></td>
                    <td><span class="Logout"><a href="http://localhost:8080/Project"> Logout </a></span>
                </tr>
            </table>
        </div>

        <div class="Bottom" style="width: 100%; height: 100%;">
            <table class="screen" style="width: 100%; height: 100%;" >
                <tr>
                    <td style="display:inline-block; margin-bottom: 70px;"><span class="left"><img src="C:\Users\nazimz\Documents\TCSS559\Luggage_Project\images\Airport.jpg"></span></td>
                    <td style="display:inline-block; width: 40%; position: fixed; right: 0;">
                        <h1 style="margin-top: 30%">
                            RUNNING LATE?</h1>
                        <h1 style="margin-top: -3%;">OUR LUGGAGE TRACKING HAS YOU COVERED!</h1>
                        <br>
                        <h1 style="opacity: 0.5; font-size: 1.4rem;"> 
                            Ride the skies with our punctual airlines that will meet your timely needs!</h1>
                        <br></br>
                        <br></br>
                        <button onclick="window.location.href = 'http://localhost:8080/Project/register'">Register</button>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</body>
</html>