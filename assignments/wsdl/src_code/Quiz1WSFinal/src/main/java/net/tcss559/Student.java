package net.tcss559;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://tcss559.net/", portName = "StudentPort", serviceName = "StudentService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class Student {
	String name;
	boolean grad;
	int credits;
	double gpa;
	String status;
	int studentID;
	
	public Student() {
		this.name = "";
		this.grad = false;
		this.credits = 0;
		this.gpa = 0;
		this.status = "Freshman";
		this.studentID = 00000;
	}
	
	// New Student 
	
	public Student(String name, int studentID){
		this.name = name;
		this.grad = false;
		this.credits = 0;
		this.gpa = 0;
		this.status = "Freshman";
		this.studentID = studentID;
	}
	
	// Returning Student
	public Student(boolean grad, int credits, double gpa, String status, String name, int studentID) {
		this.grad = grad;
		this.credits = credits;
		this.gpa = gpa;
		this.status = status;
		this.name = name;
		this.studentID = studentID;
	}
	
	public void updateCredits(int credits) {
		this.credits += credits;
	}
	
	public void updateGPA(int credits, double grade) {
		this.gpa += ((credits * grade ) / (this.credits + credits));
	}
	
	public void updateStatus() {
		int result = (this.credits % 190) - 45;
		if (result > 90 || this.credits >= 190)
			this.status = "Senior";
		else if (result < 0) {
			this.status = "Freshman";
		}
		else if (result < 45) {
			this.status = "Sophomore";
		}
		else if (result < 90) {
			this.status = "Junior";
		}
	}
	
	public void setGrad() {
		if (this.credits >= 190) this.grad = true;
		else this.grad = false;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(128);
		str.append("StudentID: " + this.studentID + "\n");
		str.append("Name: " + this.name + "\n");
		str.append("Grad: " + this.grad + "\n");
		str.append("Credits: " + this.credits + "\n");
		str.append("GPA: " + this.gpa + "\n");
		str.append("Status: " + this.status + "\n");
		return  "" + str;
	}
}
