<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Assignment 3 Form Handling</title>
</head>
<body>
<b> Assignment 3 Form WSM Matrix </b>
	<form name="WSM_AppForm"  action="./" method="get"> 
      <br>  
<table> 
    <thead> 
    <tr> 
    <td>Attributes</td> 
        <td><input type="text" name="a01" size="10" value="Price"></td> 
        <td><input type="text" name="a02" size="10" value="Gas"></td> 
        <td><input type="text" name="a03" size="10" value="Efficiency"></td> 
        <td><input type="text" name="a04" size="10" value="Aesthetic"></td> 
        <td><input type="text" name="a05" size="10" value="Miles"></td> 
    </tr> 
    </thead> 
    <tbody> 
    <tr> 
        <td>Weights</td> 
        <td><input type="text" name="w1" size="10"></td> 
        <td><input type="text" name="w2" size="10"></td> 
        <td><input type="text" name="w3" size="10"></td> 
        <td><input type="text" name="w4" size="10"></td> 
        <td><input type="text" name="w5" size="10"></td> 
    </tr> 
    <tr> 
        <td><input type="text" name="attr" size="10"></td> 
        <td><input type="text" name="row1" size="10"></td> 
        <td><input type="text" name="row1" size="10"></td> 
        <td><input type="text" name="row1" size="10"></td> 
        <td><input type="text" name="row1" size="10"></td> 
        <td><input type="text" name="row1" size="10"></td> 
    </tr>    
    <tr> 
        <td><input type="text" name="attr" size="10"></td> 
        <td><input type="text" name="row2" size="10"></td> 
        <td><input type="text" name="row2" size="10"></td> 
        <td><input type="text" name="row2" size="10"></td> 
        <td><input type="text" name="row2" size="10"></td> 
        <td><input type="text" name="row2" size="10"></td> 
    </tr> 
    <tr> 
        <td><input type="text" name="attr" size="10"></td> 
        <td><input type="text" name="row3" size="10"></td> 
        <td><input type="text" name="row3" size="10"></td> 
        <td><input type="text" name="row3" size="10"></td> 
        <td><input type="text" name="row3" size="10"></td> 
        <td><input type="text" name="row3" size="10"></td> 
    </tr> 
    <tr> 
        <td><input type="text" name="attr" size="10"></td> 
        <td><input type="text" name="row4" size="10"></td> 
        <td><input type="text" name="row4" size="10"></td> 
        <td><input type="text" name="row4" size="10"></td> 
        <td><input type="text" name="row4" size="10"></td> 
        <td><input type="text" name="row4" size="10"></td> 
    </tr> 
    <tr> 
        <td><input type="text" name="attr" size="10"></td> 
        <td><input type="text" name="row5" size="10"></td> 
        <td><input type="text" name="row5" size="10"></td> 
        <td><input type="text" name="row5" size="10"></td> 
        <td><input type="text" name="row5" size="10"></td> 
        <td><input type="text" name="row5" size="10"></td> 
    </tr> 
    <tr> 
    <td>Type</td> 
    <td> 
        <input type="radio" id="attrib1a" name="attrib1" value="1"> 
        <label for="attrib1a">Yes</label><br> 
        <input type="radio" id="attrib1b" name="attrib1" value="0"> 
        <label for="attrib1b">No</label> 
    </td> 
    <td> 
        <input type="radio" id="attrib2a" name="attrib2" value="1"> 
        <label for="attrib2a">Yes</label><br> 
        <input type="radio" id="attrib2b" name="attrib2" value="0"> 
        <label for="attrib2b">No</label></td> 
    <td> 
        <input type="radio" id="attrib3a" name="attrib3" value="1"> 
                <label for="attrib3a">Yes</label><br> 
        <input type="radio" id="attrib3b" name="attrib3" value="0"> 
        <label for="attrib3b">No</label> 
    </td> 
    <td> 
        <input type="radio" id="attrib4a" name="attrib4" value="1"> 
        <label for="attrib4a">Yes</label><br> 
        <input type="radio" id="attrib4b" name="attrib4" value="0"> 
        <label for="attrib4b">No</label> 
    </td> 
    <td> 
        <input type="radio" id="attrib5a" name="attrib5" value="1"> 
        <label for="attrib5a">Yes</label><br> 
        <input type="radio" id="attrib5b" name="attrib5" value="0"> 
        <label for="attrib5b">No</label> 
    </td> 
    </tr> 
    </tbody> 
</table> 
<input type="submit" value="Compute WSM" /> 
   </form>     
</body>
</html>