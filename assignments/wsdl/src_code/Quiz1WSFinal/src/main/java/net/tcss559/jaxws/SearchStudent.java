
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

@XmlRootElement(name = "searchStudent", namespace = "http://tcss559.net/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchStudent", namespace = "http://tcss559.net/")

public class SearchStudent {

    @XmlElement(name = "studentID")
    private int studentID;

    public int getStudentID() {
        return this.studentID;
    }

    public void setStudentID(int newStudentID)  {
        this.studentID = newStudentID;
    }

}

