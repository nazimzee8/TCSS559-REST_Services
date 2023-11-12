package net.tcss559;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.*;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;  
import org.w3c.dom.Element;  
import java.io.File;
import java.io.IOException;
import java.io.StringWriter; 

@WebService(targetNamespace = "http://tcss559.net/", portName = "UniversityPort", serviceName = "UniversityService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@SuppressWarnings("unused")
public class University {
	Map <Integer, Student> campus;
	Document doc;
	public University() throws ParserConfigurationException, IOException, SAXException {
		this.initialize();
	}
	
	public void initialize() throws ParserConfigurationException, IOException, SAXException {
		File file = new File("C:/Users/nazimz/eclipse-workspace/Quiz1WSFinal/src/main/java/net/tcss559/Students.xml"); 
		campus = new HashMap<Integer, Student>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
		DocumentBuilder db = dbf.newDocumentBuilder();   
		doc = db.parse(file);
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("student"); 
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				boolean grad = Boolean.valueOf(element.getElementsByTagName("grad").item(0).getTextContent());
				int credits = Integer.parseInt(element.getElementsByTagName("credits").item(0).getTextContent());
				double gpa = Double.parseDouble(element.getElementsByTagName("gpa").item(0).getTextContent());
				String status = element.getElementsByTagName("status").item(0).getTextContent();
				String name = element.getElementsByTagName("name").item(0).getTextContent();
				int studentID = Integer.parseInt(element.getAttribute("studentID"));
				Student student = new Student(grad, credits, gpa, status, name, studentID);
				this.campus.put(studentID, student);
			}
		}
	}
	
	/*public University(Map<Integer, Student> campus) {
		this.campus = campus;
	}*/
	@WebMethod(operationName = "enrollStudent", action = "urn:EnrollStudent")
	public Map<Integer, Student> enrollStudent(@WebParam(name = "name") String name, @WebParam(name = "studentID") int studentID) {
		Student student = new Student(name, studentID);
		this.campus.put(studentID, student);
		this.enrollHelper(name, studentID);
		return this.campus;
	}
	
	public void enrollHelper(String name, int studentID) {
		Element newStudent = doc.createElement("student");
		Node campus = doc.getFirstChild();
		
		Attr ID = doc.createAttribute("studentID");
		ID.appendChild(doc.createTextNode(""+ studentID));
		newStudent.setAttribute("studentID", ""+ studentID);
		
		Element _name = doc.createElement("name");
		_name.appendChild(doc.createTextNode(name));
		newStudent.appendChild(_name);
		
		Element grad = doc.createElement("grad");
		grad.appendChild(doc.createTextNode(String.valueOf(false)));
		newStudent.appendChild(grad);
		
		Element credits = doc.createElement("credits");
		credits.appendChild(doc.createTextNode("" + 0));
		newStudent.appendChild(credits);
		
		Element gpa = doc.createElement("gpa");
		gpa.appendChild(doc.createTextNode("" + 0));
		newStudent.appendChild(gpa);
		
		Element status = doc.createElement("status");
		status.appendChild(doc.createTextNode("Freshman"));
		newStudent.appendChild(status);
		Node node = (Node) newStudent;
		campus.appendChild(node);
	}
	

	@WebMethod(operationName = "addStudent", action = "urn:AddStudent")
	public Map<Integer, Student> addStudent(@WebParam(name = "name") String name, @WebParam(name = "studentID") int studentID, @WebParam(name = "credits") int credits, @WebParam(name = "gpa") double gpa) {
		Student student = new Student(false, credits, gpa, "Freshman", name, studentID);
		student.setGrad();
		student.updateStatus();
		this.campus.put(studentID, student);
		this.addHelper(name, studentID, credits, gpa);
		return this.campus;
	}
	
	public void addHelper(String name, int studentID,  int credits, double gpa){
		Element newStudent = doc.createElement("student");
		Student student = new Student(false, credits, gpa, "Freshman", name, studentID);
		Node campus = doc.getFirstChild();
		
		Attr ID = doc.createAttribute("studentID");
		ID.appendChild(doc.createTextNode(""+ studentID));
		newStudent.setAttribute("studentID", ""+ studentID);
		
		Element _name = doc.createElement("name");
		_name.appendChild(doc.createTextNode(name));
		newStudent.appendChild(_name);
		
		Element grad = doc.createElement("grad");
		student.setGrad();
		grad.appendChild(doc.createTextNode(String.valueOf(student.grad)));
		newStudent.appendChild(grad);
		
		Element _credits = doc.createElement("credits");
		_credits.appendChild(doc.createTextNode("" + credits));
		newStudent.appendChild(_credits);
		
		Element _gpa = doc.createElement("gpa");
		_gpa.appendChild(doc.createTextNode("" + gpa));
		newStudent.appendChild(_gpa);
		
		Element status = doc.createElement("status");
		student.updateStatus();
		status.appendChild(doc.createTextNode(student.status));
		newStudent.appendChild(status);
		campus.appendChild(newStudent);
	}
	
	@WebMethod(operationName = "searchStudent", action = "urn:SearchStudent")
	public String searchStudent(@WebParam(name = "studentID") int studentID) {
		return this.campus.get(studentID).toString();
	}
	
	@WebMethod(operationName = "removeStudent", action = "urn:RemoveStudent")
	public boolean removeStudent(@WebParam(name = "studentID") int studentID) {
		if (this.campus.get(studentID) == null) return false;
		this.remStudentByID(studentID);
		this.campus.remove(studentID);
		return true;
	}
	
	public void remStudentByID(int studentID) {
		NodeList students = doc.getElementsByTagName("student");
		Node campus = doc.getFirstChild();
		for (int i = 0; i < students.getLength(); i++) {
			Node node = students.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				int nodeID = Integer.parseInt(element.getAttribute("studentID"));
				if (studentID == nodeID) {
					campus.removeChild(element);
					break;
				}
			} 
		}
	}
	
	
	@WebMethod(operationName = "updateStudent", action = "urn:UpdateStudent")
	public Student updateStudent(@WebParam(name = "studentID") int studentID, @WebParam(name = "credits") int credits, @WebParam(name = "grade") double grade) {
		if (this.campus.get(studentID) == null) return null;
		this.helperUpdate(studentID, credits, grade);
		Student student = this.campus.get(studentID);
		student.updateGPA(credits, grade);
		student.updateCredits(credits);
		student.updateStatus();
		student.setGrad();
		return student;
	}
	
	public void helperUpdate(int studentID, int new_credits, double grade) {
		NodeList students = doc.getElementsByTagName("student");
		for (int i = 0; i < students.getLength(); i++) {
			Node node = students.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				int nodeID = Integer.parseInt(element.getAttribute("studentID"));
				boolean grad = Boolean.valueOf(element.getElementsByTagName("grad").item(0).getTextContent());
				int credits = Integer.parseInt(element.getElementsByTagName("credits").item(0).getTextContent());
				double gpa = Double.parseDouble(element.getElementsByTagName("gpa").item(0).getTextContent());
				String status = element.getElementsByTagName("status").item(0).getTextContent();
				String name = element.getElementsByTagName("name").item(0).getTextContent();
				Student student = new Student(grad, credits, gpa, status, name, studentID);
				
				if (studentID == nodeID) {
					student.updateGPA(new_credits, grade);
					student.updateCredits(new_credits);
					student.updateStatus();
					student.setGrad();
					element.getElementsByTagName("gpa").item(0).setTextContent(String.valueOf(student.gpa));
					element.getElementsByTagName("credits").item(0).setTextContent(String.valueOf(student.credits));
					element.getElementsByTagName("status").item(0).setTextContent(student.status);
					element.getElementsByTagName("grad").item(0).setTextContent(String.valueOf(student.grad));
					break;
				}
			} 
		}
	}
	
	// Test methods
	
	public static void main(String[] args) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		University university = new University();
		//university.removeStudent(11111);
		university.addStudent("Nathan Brown", 85734, 190, 3.9);
		university.enrollStudent("Ash Ketchum", 90056);
		university.removeStudent(85734);
		university.updateStudent(90056, 5, 3.2);
		for (Map.Entry<Integer, Student> student : university.campus.entrySet()) {
			System.out.println(student.getValue());
		}
		System.out.println("\n");

		// Used to output XML file after changes.
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(university.doc);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
	}
}