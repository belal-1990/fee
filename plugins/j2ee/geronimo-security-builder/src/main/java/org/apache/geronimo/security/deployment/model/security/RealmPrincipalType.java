//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.26 at 10:44:48 AM PDT 
//


package org.apache.geronimo.security.deployment.model.security;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for realmPrincipalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="realmPrincipalType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://geronimo.apache.org/xml/ns/security-2.0}loginDomainPrincipalType">
 *       &lt;attribute name="realm-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "realmPrincipalType")
public class RealmPrincipalType
    extends LoginDomainPrincipalType
{

    @XmlAttribute(name = "realm-name", required = true)
    protected String realmName;

    /**
     * Gets the value of the realmName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRealmName() {
        return realmName;
    }

    /**
     * Sets the value of the realmName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRealmName(String value) {
        this.realmName = value;
    }

}
