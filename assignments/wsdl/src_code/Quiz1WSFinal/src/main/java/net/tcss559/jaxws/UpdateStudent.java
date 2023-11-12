
package net.tcss559.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 3.5.4
 * Thu Oct 27 14:52:30 PDT 2022
 * Generated source version: 3.5.4
 */

@XmlRootElement(name = "updateStudent", namespace = "http://tcss559.net/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateStudent", namespace = "http://tcss559.net/", propOrder = {"studentID", "credits", "grade"})

public class UpdateStudent {

    @XmlElement(name = "studentID")
    private int studentID;
    @XmlElement(name = "credits")
    private int credits;
    @XmlElement(name = "grade")
    private double grade;

    public int getStudentID() {
        return this.studentID;
    }

    public void setStudentID(int newStudentID)  {
        this.studentID = newStudentID;
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int newCredits)  {
        this.credits = newCredits;
    }

    public double getGrade() {
        return this.grade;
    }

    public void setGrade(double newGrade)  {
        this.grade = newGrade;
    }

}

